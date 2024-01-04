package top.seraphjack.simplelogin.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.SimpleChannel;
import net.minecraftforge.network.*;
import top.seraphjack.simplelogin.SLConstants;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class NetworkLoader {
    private static final int PROTOCOL_VERSION = 1001000;

    public static SimpleChannel INSTANCE = ChannelBuilder.named(
            new ResourceLocation(SLConstants.MODID, "main")).networkProtocolVersion(PROTOCOL_VERSION).acceptedVersions(Channel.VersionTest.exact(PROTOCOL_VERSION)).clientAcceptedVersions(Channel.VersionTest.exact(PROTOCOL_VERSION)).simpleChannel();

    private NetworkLoader() {
        throw new UnsupportedOperationException("No instance");
    }

    public static void registerPackets() {
        registerPacket(MessageLogin.class,
                MessageLogin::encode,
                MessageLogin::decode,
                MessageLogin::handle,
                NetworkDirection.PLAY_TO_SERVER);
        registerPacket(MessageRequestLogin.class,
                MessageRequestLogin::encode,
                MessageRequestLogin::decode,
                MessageRequestLogin::handle,
                NetworkDirection.PLAY_TO_CLIENT);
        registerPacket(MessageChangePassword.class,
                MessageChangePassword::encode,
                MessageChangePassword::decode,
                MessageChangePassword::handle,
                NetworkDirection.PLAY_TO_SERVER);
        registerPacket(MessageChangePasswordResponse.class,
                MessageChangePasswordResponse::encode,
                MessageChangePasswordResponse::decode,
                MessageChangePasswordResponse::handle,
                NetworkDirection.PLAY_TO_CLIENT);
    }

    private static int id = 0;

    private static <MSG> void registerPacket(Class<MSG> msg, BiConsumer<MSG, FriendlyByteBuf> encoder,
                                             Function<FriendlyByteBuf, MSG> decoder,
                                             BiConsumer<MSG, CustomPayloadEvent.Context> handler,
                                             final NetworkDirection direction) {
        INSTANCE.messageBuilder(msg, id++, direction).consumerNetworkThread(handler).decoder(decoder).encoder(encoder).add();
        //INSTANCE.registerMessage(id++, msg, encoder, decoder, handler, Optional.of(direction));
    }
}