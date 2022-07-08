package dev.hilligans.mcauthserver.network.Packets;


import dev.hilligans.mcauthserver.Authenticate;
import dev.hilligans.mcauthserver.network.PacketBase;
import dev.hilligans.mcauthserver.network.PacketData;
import io.netty.channel.ChannelHandlerContext;

public class CGetToken extends PacketBase {
    public String token;
    public CGetToken() {
        super(0);
    }

    @Override
    public void encode(PacketData packetData) {}

    @Override
    public void decode(PacketData packetData) {
        token = packetData.readString();
    }

    @Override
    public void handle(ChannelHandlerContext ctx) {
        Authenticate.authenticate(token, s -> ctx.channel().writeAndFlush(new PacketData(new SSendToken(s))), s -> ctx.channel().writeAndFlush(new PacketData(new SAuthenticationError(s))));
    }
}
