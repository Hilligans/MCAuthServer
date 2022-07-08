package dev.hilligans.mcauthserver.network.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

public class HttpSnoopClientHandler extends SimpleChannelInboundHandler<HttpObject> {

    public HttpSnoopClientInitializer httpSnoopClientInitializer;

    public HttpSnoopClientHandler(HttpSnoopClientInitializer httpSnoopClientInitializer) {
        this.httpSnoopClientInitializer = httpSnoopClientInitializer;
    }

    static int x = 0;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
     //   System.out.println("opened channel " + x++ + ":" + ctx.channel().id().toString());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
      //  System.out.println("read");
        try {
            if (msg instanceof HttpResponse response) {

             //   System.err.println("STATUS: " + response.status());
             //   System.err.println("VERSION: " + response.protocolVersion());
             //   System.err.println();

                if (!response.headers().isEmpty()) {
                    for (CharSequence name : response.headers().names()) {
                        for (CharSequence value : response.headers().getAll(name)) {
                         //   System.err.println("HEADER: " + name + " = " + value);
                        }
                    }
                    System.err.println();
                }

                if (HttpUtil.isTransferEncodingChunked(response)) {
                  //  System.err.println("CHUNKED CONTENT {");
                } else {
                  // System.err.println("CONTENT {");
                }
            }
             else if (msg instanceof HttpContent content) {
                String stringContent = content.content().toString(CharsetUtil.UTF_8);
              //  System.err.print(stringContent);
              //  System.err.flush();
                if (httpSnoopClientInitializer.runnable != null) {
                    httpSnoopClientInitializer.runnable.accept(stringContent);
                }
                if (content instanceof LastHttpContent) {
                 //   System.err.println("} END OF CONTENT");
                    ctx.close();
                }
            } else {
              //   System.out.println(msg);
            }
        } catch (Exception e) {
          //  System.out.println("err");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
       // System.out.println("Channel inactive" + ctx.channel().id().toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}