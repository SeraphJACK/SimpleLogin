package top.seraphjack.simplelogin.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import top.seraphjack.simplelogin.client.PasswordHolder;

import java.util.function.Supplier;

@SuppressWarnings("InstantiationOfUtilityClass")
public class MessageRequestLogin {

    public MessageRequestLogin() {
    }

    public static void encode(MessageRequestLogin msg, FriendlyByteBuf buffer) {
        // NO-OP
    }

    public static MessageRequestLogin decode(FriendlyByteBuf buffer) {
        return new MessageRequestLogin();
    }

    public static void handle(MessageRequestLogin message, Supplier<NetworkEvent.Context> ctx) {
        NetworkLoader.INSTANCE.sendToServer(new MessageLogin(PasswordHolder.instance().password()));
        ctx.get().setPacketHandled(true);
    }
}
