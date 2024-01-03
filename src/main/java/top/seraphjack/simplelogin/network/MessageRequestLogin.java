package top.seraphjack.simplelogin.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.PacketDistributor;
import top.seraphjack.simplelogin.client.PasswordHolder;

@SuppressWarnings({"InstantiationOfUtilityClass", "unused"})
public class MessageRequestLogin {

    public MessageRequestLogin() {
    }

    public static void encode(MessageRequestLogin msg, FriendlyByteBuf buffer) {
        // NO-OP
    }

    public static MessageRequestLogin decode(FriendlyByteBuf buffer) {
        return new MessageRequestLogin();
    }

    public static void handle(MessageRequestLogin message, CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            NetworkLoader.INSTANCE.send(new MessageLogin(PasswordHolder.instance().password()), PacketDistributor.SERVER.noArg());
        });
        ctx.setPacketHandled(true);
    }
}