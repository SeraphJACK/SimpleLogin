package top.seraphjack.simplelogin.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.client.PasswordHolder;

import java.util.function.Supplier;

@SuppressWarnings("InstantiationOfUtilityClass")
public class MessageRequestLogin {

    public MessageRequestLogin() {
    }

    public static void encode(MessageRequestLogin msg, PacketBuffer buffer) {
        // NO-OP
    }

    public static MessageRequestLogin decode(PacketBuffer buffer) {
        return new MessageRequestLogin();
    }

    public static void handle(MessageRequestLogin message, Supplier<NetworkEvent.Context> ctx) {
        NetworkLoader.INSTANCE.sendToServer(new MessageLogin(PasswordHolder.instance().password()));
        ctx.get().setPacketHandled(true);
    }
}
