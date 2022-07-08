package dev.hilligans.mcauthserver.network.Packets;

import dev.hilligans.mcauthserver.network.PacketBase;
import dev.hilligans.mcauthserver.network.PacketData;
import io.netty.channel.ChannelHandlerContext;

public class SAuthenticationError extends PacketBase {

    public String error;

    public SAuthenticationError() {
        super(2);
    }

    public SAuthenticationError(String error) {
        super(2);
        this.error = error;
    }

    @Override
    public void encode(PacketData packetData) {
        packetData.writeString(error);
    }

    @Override
    public void decode(PacketData packetData) {

    }

    @Override
    public void handle(ChannelHandlerContext ctx) {

    }
}
