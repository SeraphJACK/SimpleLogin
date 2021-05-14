package top.seraphjack.simplelogin.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import top.seraphjack.simplelogin.SimpleLogin;
import top.seraphjack.simplelogin.server.storage.SLStorage;
import top.seraphjack.simplelogin.utils.SHA256;

import java.util.Objects;
import java.util.function.Supplier;

public class MessageChangePassword {
    private final String original, to;

    public MessageChangePassword(String original, String to) {
        this.original = SHA256.getSHA256(original);
        this.to = SHA256.getSHA256(to);
    }

    public static void encode(MessageChangePassword msg, PacketBuffer buf) {
        buf.writeString(msg.original, 200);
        buf.writeString(msg.to, 200);
    }

    public static MessageChangePassword decode(PacketBuffer buf) {
        return new MessageChangePassword(buf.readString(200), buf.readString(200));
    }

    public static void handle(MessageChangePassword msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        assert context.getSender() != null;
        String username = Objects.requireNonNull(ctx.get().getSender()).getGameProfile().getName();
        if (SLStorage.instance().storageProvider.checkPassword(username, msg.original)) {
            SLStorage.instance().storageProvider.changePassword(username, msg.to);
            context.getSender().sendStatusMessage(new TranslationTextComponent(
                    "simplelogin.info.password_change_successful"), false);
            NetworkLoader.INSTANCE.sendToServer(new MessageChangePasswordResponse(true));
        } else {
            // Should never happen though
            context.getSender().sendStatusMessage(new StringTextComponent(
                    "simplelogin.info.password_change_fail"), false);
            NetworkLoader.INSTANCE.sendToServer(new MessageChangePasswordResponse(false));
            SimpleLogin.logger.warn("Player " + username + " tried to change password with a wrong password.");
        }
        context.setPacketHandled(true);
    }
}
