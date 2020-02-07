package top.seraphjack.simplelogin.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import top.seraphjack.simplelogin.SLConstants;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class NetworkLoader {
    private static final String PROTOCOL_VERSION = "1.1";

    public static SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SLConstants.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private NetworkLoader() {
        throw new UnsupportedOperationException("No instance");
    }

    public static void registerPackets() {
        registerPacket(MessageLogin.class, MessageLogin::encode, MessageLogin::decode, MessageLogin::handle);
        registerPacket(MessageRequestLogin.class, MessageRequestLogin::encode, MessageRequestLogin::decode, MessageRequestLogin::handle);
        registerPacket(MessageChangePassword.class, MessageChangePassword::encode, MessageChangePassword::decode, MessageChangePassword::handle);
        registerPacket(MessageRequestEntries.class, MessageRequestEntries::encode, MessageRequestEntries::decode, MessageRequestEntries::handle);
        registerPacket(MessageEntries.class, MessageEntries::encode, MessageEntries::decode, MessageEntries::handle);
    }

    private static int id = 0;

    private static <MSG> void registerPacket(Class<MSG> msg, BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder,
                                             BiConsumer<MSG, Supplier<NetworkEvent.Context>> handler) {
        INSTANCE.registerMessage(id++, msg, encoder, decoder, handler);
    }
}
