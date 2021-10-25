package top.seraphjack.simplelogin.command;

import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import top.seraphjack.simplelogin.SLConstants;
import top.seraphjack.simplelogin.command.arguments.ArgumentTypeEntryName;
import top.seraphjack.simplelogin.command.arguments.ArgumentTypeHandlerPlugin;

@Mod.EventBusSubscriber(modid = SLConstants.MODID)
public final class CommandLoader {

    public static void commonSetup(@SuppressWarnings("unused") FMLCommonSetupEvent event) {
        ArgumentTypes.register("simplelogin:entry", ArgumentTypeEntryName.class,
                new EmptyArgumentSerializer<>(ArgumentTypeEntryName::entryName));
        ArgumentTypes.register("simplelogin:plugin", ArgumentTypeHandlerPlugin.class,
                new EmptyArgumentSerializer<>(ArgumentTypeHandlerPlugin::allPlugins));
    }

    @SubscribeEvent
    public static void commandRegister(RegisterCommandsEvent event) {
        SLCommand.register(event.getDispatcher());
    }

}
