package top.seraphjack.simplelogin.client;

import net.minecraftforge.client.ClientCommandHandler;
import top.seraphjack.simplelogin.CommonProxy;
import top.seraphjack.simplelogin.client.command.CommandChangePassword;

public class ClientProxy extends CommonProxy {
    @Override
    public void init() {
        super.init();
        ClientCommandHandler.instance.registerCommand(new CommandChangePassword());
    }

}
