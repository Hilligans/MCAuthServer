package dev.hilligans.mcauthserver;

import dev.hilligans.mcauthserver.network.http.HTTPUtil;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Authenticate {

    public static void authenticate(String code, Consumer<String> consumer, Consumer<String> onError) {
        try {
            String c = "client_id=" + Main.client_id + "&client_secret=" + Main.server_secret + "&code=" + code + "&grant_type=authorization_code&redirect_uri=https://localhost";
            HTTPUtil.sendContent("https://login.live.com/oauth20_token.srf", c, s -> {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String access = jsonObject.getString("access_token");
                    String ss = """
                            {
                                   Properties: {
                                       AuthMethod: "RPS",
                                       SiteName: "user.auth.xboxlive.com",
                                   },
                                   RelyingParty: "http://auth.xboxlive.com",
                                   TokenType: "JWT",
                                }
                            """;
                    JSONObject sss = new JSONObject(ss);
                    sss.getJSONObject("Properties").put("RpsTicket", "d=" + access);
                    HTTPUtil.sendContent("https://user.auth.xboxlive.com/user/authenticate", sss.toString(), s1 -> {
                        try {
                            JSONObject jsonObject1 = new JSONObject(s1);
                            String token = jsonObject1.getString("Token");
                            String userHash = jsonObject1.getJSONObject("DisplayClaims").getJSONArray("xui").getJSONObject(0).getString("uhs");
                            JSONObject out = new JSONObject(
                                    """
                                            {
                                                "Properties": {
                                                    "SandboxId": "RETAIL",
                                                    "UserTokens": [""" +
                                            token + """
                                               ]
                                               },
                                               "RelyingParty": "rp://api.minecraftservices.com/",
                                               "TokenType": "JWT"
                                            }"""
                            );
                            HTTPUtil.sendContent("https://xsts.auth.xboxlive.com/xsts/authorize", out.toString(), s2 -> {
                                try {
                                    JSONObject result = new JSONObject(s2);
                                    String xstsToken = result.getString("Token");
                                    String mcTokenJson = executePost("https://api.minecraftservices.com/authentication/login_with_xbox", new JSONObject().put("identityToken", String.format("XBL3.0 x=%s;%s", userHash, xstsToken)).put("ensureLegacyEnabled", true).toString());
                                    JSONObject jsonObject2 = new JSONObject(mcTokenJson);
                                    consumer.accept(jsonObject2.getString("access_token"));
                                } catch (Exception e) {
                                    onError.accept(e.getMessage());
                                }
                            }, "application/json");
                        } catch (Exception e) {
                            onError.accept(e.getMessage());
                        }
                    }, "application/json");
                } catch (Exception e) {
                    onError.accept(e.getMessage());
                }
            }, "application/x-www-form-urlencoded");
        } catch (Exception e) {
            onError.accept(e.getMessage());
        }
    }

    public static String executePost(String targetURL, String urlParameters) {
        HttpURLConnection connection = null;

        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/json");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
