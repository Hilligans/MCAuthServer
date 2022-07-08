package dev.hilligans.mcauthserver;

import dev.hilligans.mcauthserver.network.ServerNetworkInit;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Main {

    public static String port = "99877";
    public static String server_secret;
    public static String client_id;

    public static void main(String[] args) throws Exception {
        server_secret = readString("secret.txt").get(0);
        ServerNetworkInit.startServer(port);
    }

    public static ArrayList<String> readString(String source) {
        InputStream stream = Main.class.getResourceAsStream(source);
        if(stream == null) {
            return null;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        ArrayList<String> strings = new ArrayList<>();
        reader.lines().forEach(strings::add);
        return strings;
    }
}
