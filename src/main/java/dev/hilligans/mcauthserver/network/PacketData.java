package dev.hilligans.mcauthserver.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

public class PacketData {

    public ChannelHandlerContext ctx;

    ByteBuf byteBuf;
    int packetId = 0;
    public int size = 0;

    public PacketData(PacketBase packetBase) {
        byteBuf = Unpooled.buffer();
        packetId = packetBase.packetId;
        packetBase.encode(this);
    }

    public PacketData(int val) {
        byteBuf = Unpooled.buffer();
        byteBuf.writeByte(val);
    }

    public PacketData(byte[] bytes) {
        byteBuf = Unpooled.buffer();
        byteBuf.writeBytes(bytes);
        packetId = byteBuf.readInt();
    }

    public PacketData(ByteBuf byteBuf) {
        packetId = byteBuf.readInt();
        byteBuf.readBytes(packetId);
    }

    public void writeToByteBuf(ByteBuf byteBuf) {
        byteBuf.writeInt(size + 8);
        byteBuf.writeInt(packetId);
        byteBuf.writeBytes(this.byteBuf);
    }

    public int readInt() {
        size -= 4;
        return byteBuf.readInt();
    }

    public float readFloat() {
        size -= 4;
        return byteBuf.readFloat();
    }

    public short readShort() {
        size -= 2;
        return byteBuf.readShort();
    }

    public byte readByte() {
        size -= 1;
        return byteBuf.readByte();
    }

    public long readLong() {
        size -= 8;
        return byteBuf.readLong();
    }

    public String readString() {
        short stringLength = readShort();
        if(stringLength == -1) {
            return " ";
        }
        StringBuilder val = new StringBuilder();
        for(short x = 0; x < stringLength; x++) {
            char val1 = (char) (readByte() & 0xFF);
            val.append(val1);
        }
        return val.toString();
    }

    public boolean readBoolean() {
        byte val = readByte();
        return val == (byte) 1;
    }

    public void writeInt(int val) {
        size += 4;
        byteBuf.writeInt(val);
    }

    public void writeFloat(float val) {
        size += 4;
        byteBuf.writeFloat(val);
    }

    public void writeShort(short val) {
        size += 2;
        byteBuf.writeShort(val);
    }

    public void writeByte(byte val) {
        size += 1;
        byteBuf.writeByte(val);
    }

    public void writeLong(long val) {
        size += 8;
        byteBuf.writeLong(val);
    }

    public void writeString(String val) {
        if(val.length() == 0) {
            writeShort((short)-1);
            return;
        }
        short stringLength = (short) val.length();
        writeShort(stringLength);

        //writeShort(stringLength);
        for(short x = 0; x < stringLength; x++) {
            writeByte((byte)val.charAt(x));
        }
    }

    public void writeBoolean(boolean val) {
        if(val) {
            writeByte((byte)1);
        } else {
            writeByte((byte)0);
        }
    }

    public PacketBase createPacket() {
        PacketBase packetBase = PacketBase.packets.get(packetId).get();
        packetBase.ctx = ctx;
        packetBase.decode(this);
        return packetBase;
    }


}
