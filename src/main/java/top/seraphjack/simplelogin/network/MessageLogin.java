package top.seraphjack.simplelogin.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import top.seraphjack.simplelogin.server.handler.PlayerLoginHandler;
import top.seraphjack.simplelogin.utils.SHA256;

import java.util.function.Supplier;

public class MessageLogin {
    private final String pwd;

    public MessageLogin(String pwd) {
        this.pwd = SHA256.getSHA256(pwd);
    }

    public static void encode(MessageLogin packet, PacketBuffer buf) {
        buf.writeString(packet.pwd, 200);
    }

    public static MessageLogin decode(PacketBuffer buffer) {
        return new MessageLogin(buffer.readString(200));
    }

    public static void handle(MessageLogin message, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity player = ctx.get().getSender();
        if (player != null) {
            PlayerLoginHandler.instance().login(player.getGameProfile().getName(), message.pwd);
        }
        ctx.get().setPacketHandled(true);
    }
}
