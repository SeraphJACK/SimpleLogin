package top.seraphjack.simplelogin.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.PacketDistributor;
import top.seraphjack.simplelogin.SimpleLogin;
import top.seraphjack.simplelogin.server.storage.SLStorage;
import top.seraphjack.simplelogin.utils.SHA256;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class MessageChangePassword {
    private final String original, to;

    public MessageChangePassword(String original, String to) {
        this.original = SHA256.getSHA256(original);
        this.to = SHA256.getSHA256(to);
    }

    public static void encode(MessageChangePassword msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.original.length());
        buf.writeCharSequence(msg.original, StandardCharsets.UTF_8);
        buf.writeInt(msg.to.length());
        buf.writeCharSequence(msg.to, StandardCharsets.UTF_8);
    }

    public static MessageChangePassword decode(FriendlyByteBuf buf) {
        String original = buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8).toString();
        String to = buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8).toString();
        return new MessageChangePassword(original, to);
    }

    public static void handle(MessageChangePassword msg, CustomPayloadEvent.Context ctx) {
        CustomPayloadEvent.Context context = ctx;
        assert context.getSender() != null;
        String username = Objects.requireNonNull(ctx.getSender()).getGameProfile().getName();
        if (SLStorage.instance().storageProvider.checkPassword(username, msg.original)) {
            SLStorage.instance().storageProvider.changePassword(username, msg.to);
            context.getSender().displayClientMessage(
                    Component.translatable("simplelogin.info.password_change_successful"),
                    false
            );
            NetworkLoader.INSTANCE.send(new MessageChangePasswordResponse(true),
                    PacketDistributor.PLAYER.with(context.getSender()));
        } else {
            // Should never happen though
            context.getSender().displayClientMessage(
                    Component.translatable("simplelogin.info.password_change_fail"),
                    false
            );
            NetworkLoader.INSTANCE.send(new MessageChangePasswordResponse(false),
                    PacketDistributor.PLAYER.with(context.getSender()));
            SimpleLogin.logger.warn("Player " + username + " tried to change password with a wrong password.");
        }
        context.setPacketHandled(true);
    }
}
