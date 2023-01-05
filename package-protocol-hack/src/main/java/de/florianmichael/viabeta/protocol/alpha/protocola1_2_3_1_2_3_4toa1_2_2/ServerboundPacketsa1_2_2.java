package de.florianmichael.viabeta.protocol.alpha.protocola1_2_3_1_2_3_4toa1_2_2;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import de.florianmichael.viabeta.pre_netty.type.PreNettyTypes;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettyPacketType;
import io.netty.buffer.ByteBuf;

import java.util.function.BiConsumer;

public enum ServerboundPacketsa1_2_2 implements ServerboundPacketType, PreNettyPacketType {

    KEEP_ALIVE(0, (user, buf) -> {
    }),
    LOGIN(1, (user, buf) -> {
        buf.readInt();
        PreNettyTypes.readUTF(buf);
        PreNettyTypes.readUTF(buf);
        buf.readLong();
        buf.readByte();
    }),
    HANDSHAKE(2, (user, buf) -> {
        PreNettyTypes.readUTF(buf);
    }),
    CHAT_MESSAGE(3, (user, buf) -> {
        PreNettyTypes.readUTF(buf);
    }),
    PLAYER_INVENTORY(5, (user, buf) -> {
        buf.readInt();
        int x = buf.readShort();
        for (int i = 0; i < x; i++) PreNettyTypes.readItemStackb1_2(buf);
    }),
    INTERACT_ENTITY(7, (user, buf) -> {
        buf.readInt();
        buf.readInt();
    }),
    PLAYER_MOVEMENT(10, (user, buf) -> {
        buf.readUnsignedByte();
    }),
    PLAYER_POSITION(11, (user, buf) -> {
        buf.readDouble();
        buf.readDouble();
        buf.readDouble();
        buf.readDouble();
        buf.readUnsignedByte();
    }),
    PLAYER_ROTATION(12, (user, buf) -> {
        buf.readFloat();
        buf.readFloat();
        buf.readUnsignedByte();
    }),
    PLAYER_POSITION_AND_ROTATION(13, (user, buf) -> {
        buf.readDouble();
        buf.readDouble();
        buf.readDouble();
        buf.readDouble();
        buf.readFloat();
        buf.readFloat();
        buf.readUnsignedByte();
    }),
    PLAYER_DIGGING(14, (user, buf) -> {
        buf.readUnsignedByte();
        buf.readInt();
        buf.readUnsignedByte();
        buf.readInt();
        buf.readUnsignedByte();
    }),
    PLAYER_BLOCK_PLACEMENT(15, (user, buf) -> {
        buf.readShort();
        buf.readInt();
        buf.readUnsignedByte();
        buf.readInt();
        buf.readUnsignedByte();
    }),
    HELD_ITEM_CHANGE(16, (user, buf) -> {
        buf.readInt();
        buf.readShort();
    }),
    ANIMATION(18, (user, buf) -> {
        buf.readInt();
        buf.readByte();
    }),
    SPAWN_ITEM(21, (user, buf) -> {
        buf.readInt();
        buf.readShort();
        buf.readByte();
        buf.readInt();
        buf.readInt();
        buf.readInt();
        buf.readByte();
        buf.readByte();
        buf.readByte();
    }),
    COMPLEX_ENTITY(59, (user, buf) -> {
        buf.readInt();
        buf.readShort();
        buf.readInt();
        int x = buf.readUnsignedShort();
        for (int i = 0; i < x; i++) buf.readByte();
    }),
    DISCONNECT(255, (user, buf) -> {
        PreNettyTypes.readUTF(buf);
    });

    private static final ServerboundPacketsa1_2_2[] REGISTRY = new ServerboundPacketsa1_2_2[256];

    static {
        for (ServerboundPacketsa1_2_2 packet : values()) {
            REGISTRY[packet.id] = packet;
        }
    }

    public static ServerboundPacketsa1_2_2 getPacket(final int id) {
        return REGISTRY[id];
    }

    private final int id;
    private final BiConsumer<UserConnection, ByteBuf> packetReader;

    ServerboundPacketsa1_2_2(final int id, final BiConsumer<UserConnection, ByteBuf> packetReader) {
        this.id = id;
        this.packetReader = packetReader;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public BiConsumer<UserConnection, ByteBuf> getPacketReader() {
        return this.packetReader;
    }

}