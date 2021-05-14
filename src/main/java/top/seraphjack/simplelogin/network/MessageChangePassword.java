package top.seraphjack.simplelogin.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
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
        String username = Objects.requireNonNull(ctx.get().getSender()).getGameProfile().getName();
        if (SLStorage.instance().storageProvider.checkPassword(username, msg.original)) {
            SLStorage.instance().storageProvider.changePassword(username, msg.to);
            Objects.requireNonNull(ctx.get().getSender()).sendStatusMessage(new StringTextComponent("Password changed successfully."), false);
            NetworkLoader.INSTANCE.sendToServer(new MessageChangePasswordResponse(true));
        } else {
            // Should never happen though
            Objects.requireNonNull(ctx.get().getSender()).sendStatusMessage(new StringTextComponent("Wrong original password."), false);
            NetworkLoader.INSTANCE.sendToServer(new MessageChangePasswordResponse(false));
            SimpleLogin.logger.warn("Player " + username + " tried to change password with a wrong password.");
        }
        ctx.get().setPacketHandled(true);
    }
}
