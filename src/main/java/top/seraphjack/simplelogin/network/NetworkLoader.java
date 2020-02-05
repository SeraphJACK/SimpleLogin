package top.seraphjack.simplelogin.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import top.seraphjack.simplelogin.SimpleLogin;

public class NetworkLoader {
    public static SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SimpleLogin.MODID, "test"),
            () -> "*",
            (version) -> true,
            (version) -> true
    );
    private static int msgId = 0;

    public NetworkLoader() {
        registerMessage(MessageLogin.MessageHandler.class, MessageLogin.class, Side.SERVER);
        registerMessage(MessageRequestLogin.MessageHandler.class, MessageRequestLogin.class, Side.CLIENT);
        registerMessage(MessageChangePassword.MessageHandler.class, MessageChangePassword.class, Side.SERVER);
    }

    private static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side) {
        INSTANCE.registerMessage(messageHandler, requestMessageType, msgId++, side);
    }
}
