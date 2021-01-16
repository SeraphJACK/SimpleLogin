package top.seraphjack.simplelogin.command;

import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import top.seraphjack.simplelogin.SLConstants;

@Mod.EventBusSubscriber(modid = SLConstants.MODID)
public final class CommandLoader {

    public static void commonSetup(FMLCommonSetupEvent event) {
        ArgumentTypes.register("simplelogin:entry", ArgumentTypeEntryName.class,
                new ArgumentSerializer<>(ArgumentTypeEntryName::entryName));
    }

    @SubscribeEvent
    public static void commandRegister(RegisterCommandsEvent event) {
        SLCommand.register(event.getDispatcher());
    }

}
