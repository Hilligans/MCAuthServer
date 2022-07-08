package dev.hilligans.mcauthserver.network.Packets;

import dev.hilligans.mcauthserver.network.PacketBase;
import dev.hilligans.mcauthserver.network.PacketData;
import io.netty.channel.ChannelHandlerContext;

public class SSendToken extends PacketBase {

    public String token;

    public SSendToken() {
        super(1);
    }

    public SSendToken(String token) {
        super(1);
        this.token = token;
    }

    @Override
    public void encode(PacketData packetData) {
        packetData.writeString(token);
    }

    @Override
    public void decode(PacketData packetData) {

    }

    @Override
    public void handle(ChannelHandlerContext ctx) {

    }
}
