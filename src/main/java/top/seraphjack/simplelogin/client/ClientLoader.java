package top.seraphjack.simplelogin.client;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import top.seraphjack.simplelogin.client.util.ClientCommandHandler;

public final class ClientLoader {

    public static void clientSetup(@SuppressWarnings("unused") FMLClientSetupEvent event) {
        ClientCommandHandler.registerCommands();
    }
}
