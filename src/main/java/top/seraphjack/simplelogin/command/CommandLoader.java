package top.seraphjack.simplelogin.command;

import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
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
        ArgumentTypeInfos.registerByClass(ArgumentTypeEntryName.class,
                SingletonArgumentInfo.contextFree(ArgumentTypeEntryName::entryName));
        ArgumentTypeInfos.registerByClass(ArgumentTypeHandlerPlugin.class,
                SingletonArgumentInfo.contextFree(ArgumentTypeHandlerPlugin::allPlugins));
    }

    @SubscribeEvent
    public static void commandRegister(RegisterCommandsEvent event) {
        SLCommand.register(event.getDispatcher());
    }

}
