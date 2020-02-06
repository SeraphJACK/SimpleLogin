package top.seraphjack.simplelogin.client;

import net.minecraft.command.Commands;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.lang.reflect.Field;

public final class ClientLoader {

    public static void clientSetup(FMLClientSetupEvent event) throws Exception {
        // ClientCommandHandler.instance.registerCommand(new CommandChangePassword()); TODO
    }
}
