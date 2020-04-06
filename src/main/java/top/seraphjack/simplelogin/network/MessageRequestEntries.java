package top.seraphjack.simplelogin.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import top.seraphjack.simplelogin.server.storage.SLStorage;

import java.util.Objects;
import java.util.function.Supplier;

public class MessageRequestEntries {

    public static void encode(MessageRequestEntries msg, PacketBuffer buffer) {
        // NO-OP
    }

    public static MessageRequestEntries decode(PacketBuffer buffer) {
        return new MessageRequestEntries();
    }

    public static void handle(MessageRequestEntries message, Supplier<NetworkEvent.Context> ctx) {
        if (Objects.requireNonNull(ctx.get().getSender()).getCommandSource().hasPermissionLevel(3)) {
            NetworkLoader.INSTANCE.send(PacketDistributor.PLAYER.with(ctx.get()::getSender), new MessageEntries(SLStorage.instance().storageProvider.getAllRegisteredUsername()));
        }
        ctx.get().setPacketHandled(true);
    }
}
