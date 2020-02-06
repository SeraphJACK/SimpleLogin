package top.seraphjack.simplelogin.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import top.seraphjack.simplelogin.SLConstants;

public class NetworkLoader {
    public static SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            //TODO
            new ResourceLocation(SLConstants.MODID, "test"),
            () -> "*",
            (version) -> true,
            (version) -> true
    );
    private static int msgId = 0;

    private NetworkLoader() {
        throw new UnsupportedOperationException("No instance");
    }

    public static void registerPackets() {
        // TODO
    }
}
