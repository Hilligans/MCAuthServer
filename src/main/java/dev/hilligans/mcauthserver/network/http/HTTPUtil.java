package dev.hilligans.mcauthserver.network.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.json.JSONObject;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class HTTPUtil {

    public static String buildString1(String client, String access) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accessToken",access);
        jsonObject.put("clientToken",client);
        return jsonObject.toString();
    }

    public static void sendContent(String ip, String content, Consumer<String> consumer, String contentType) {
        try {
            URI uri = new URI(System.getProperty("url", ip));
            String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
            String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
            int port = uri.getPort();
            if (port == -1) {
                if ("http".equalsIgnoreCase(scheme)) {
                    port = 80;
                } else if ("https".equalsIgnoreCase(scheme)) {
                    port = 443;
                }
            }

            if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
                System.err.println("Only HTTP(S) is supported.");
                return;
            }

            // Configure SSL context if necessary.
            final boolean ssl = "https".equalsIgnoreCase(scheme);
            final SslContext sslCtx;
            if (ssl) {
                sslCtx = SslContextBuilder.forClient()
                        .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            } else {
                sslCtx = null;
            }

            // Configure the client.
            EventLoopGroup group = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(group)
                        .channel(NioSocketChannel.class)
                        .handler(new HttpSnoopClientInitializer(sslCtx, consumer));

                // Make the connection attempt.
                Channel ch = b.connect(host, port).sync().channel();

                // Prepare the HTTP request.
                FullHttpRequest request = new DefaultFullHttpRequest(
                        HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getRawPath());

                request.headers().set(HttpHeaders.Names.CONTENT_TYPE, contentType);
                request.headers().set(HttpHeaders.Names.HOST, host);
                request.headers().set(HttpHeaders.Names.ACCEPT, "application/json");
                request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE); // or HttpHeaders.Values.CLOSE
                request.headers().set(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);
                //   request.headers().add(HttpHeaders.Names.CONTENT_TYPE, "application/json");
                ByteBuf bbuf = Unpooled.copiedBuffer(content, StandardCharsets.UTF_8);
                request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, bbuf.readableBytes());
                request.content().clear().writeBytes(bbuf);


                // Send the HTTP request.
                ch.writeAndFlush(request);

                // Wait for the server to close the connection.
               // ch.closeFuture().sync();
            } finally {
                // Shut down executor threads to exit.
                //group.shutdownGracefully();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void sendContent(String ip, String content, Consumer<String> consumer, String contentType, String header, String val) {
        try {
            URI uri = new URI(System.getProperty("url", ip));
            String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
            String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
            int port = uri.getPort();
            if (port == -1) {
                if ("http".equalsIgnoreCase(scheme)) {
                    port = 80;
                } else if ("https".equalsIgnoreCase(scheme)) {
                    port = 443;
                }
            }

            if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
                System.err.println("Only HTTP(S) is supported.");
                return;
            }

            // Configure SSL context if necessary.
            final boolean ssl = "https".equalsIgnoreCase(scheme);
            final SslContext sslCtx;
            if (ssl) {
                sslCtx = SslContextBuilder.forClient()
                        .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            } else {
                sslCtx = null;
            }

            // Configure the client.
            EventLoopGroup group = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(group)
                        .channel(NioSocketChannel.class)
                        .handler(new HttpSnoopClientInitializer(sslCtx, consumer));

                // Make the connection attempt.
                Channel ch = b.connect(host, port).sync().channel();

                // Prepare the HTTP request.
                FullHttpRequest request = new DefaultFullHttpRequest(
                        HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getRawPath());

                request.headers().set(header,val);
                request.headers().set(HttpHeaders.Names.CONTENT_TYPE, contentType);
                request.headers().set(HttpHeaders.Names.HOST, host);
                request.headers().set(HttpHeaders.Names.ACCEPT, "application/json");
                request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE); // or HttpHeaders.Values.CLOSE
                request.headers().set(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);
                //   request.headers().add(HttpHeaders.Names.CONTENT_TYPE, "application/json");
                ByteBuf bbuf = Unpooled.copiedBuffer(content, StandardCharsets.UTF_8);
                request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, bbuf.readableBytes());
                request.content().clear().writeBytes(bbuf);


                // Send the HTTP request.
                ch.writeAndFlush(request);

                // Wait for the server to close the connection.
                // ch.closeFuture().sync();
            } finally {
                // Shut down executor threads to exit.
                //group.shutdownGracefully();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
