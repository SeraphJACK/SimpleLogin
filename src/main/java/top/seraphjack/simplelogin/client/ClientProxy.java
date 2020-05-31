package top.seraphjack.simplelogin.client;

import net.minecraftforge.client.ClientCommandHandler;
import top.seraphjack.simplelogin.CommonProxy;
import top.seraphjack.simplelogin.client.command.CommandChangePassword;
import top.seraphjack.simplelogin.client.command.CommandLogin;
import top.seraphjack.simplelogin.client.command.CommandRegister;

public class ClientProxy extends CommonProxy {
    @Override
    public void init() {
        super.init();
        ClientCommandHandler.instance.registerCommand(new CommandChangePassword());
        ClientCommandHandler.instance.registerCommand(new CommandLogin());
        ClientCommandHandler.instance.registerCommand(new CommandRegister());
        PasswordStorage.init();
    }

}
