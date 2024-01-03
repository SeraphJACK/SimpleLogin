package top.seraphjack.simplelogin.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import top.seraphjack.simplelogin.client.PasswordHolder;

public class MessageChangePasswordResponse {
    private final boolean success;

    public boolean success() {
        return success;
    }

    public MessageChangePasswordResponse(boolean success) {
        this.success = success;
    }

    public static void encode(MessageChangePasswordResponse msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.success);
    }

    public static MessageChangePasswordResponse decode(FriendlyByteBuf buf) {
        return new MessageChangePasswordResponse(buf.readBoolean());
    }

    public static void handle(MessageChangePasswordResponse message, CustomPayloadEvent.Context ctx) {
        if (message.success()) {
            PasswordHolder.instance().applyPending();
        } else {
            PasswordHolder.instance().dropPending();
        }
        ctx.setPacketHandled(true);
    }
}