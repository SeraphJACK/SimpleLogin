package top.seraphjack.simplelogin.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.network.MessageChangePassword;
import top.seraphjack.simplelogin.network.NetworkLoader;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class ChangePasswordCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("sl_changepassword")
                        .then(Commands.argument("original", new ArgumentTypeOriginalPassword())
                                .then(Commands.argument("to", StringArgumentType.string()).requires((c) -> true)
                                        .executes((c) -> {
                                            NetworkLoader.INSTANCE.sendToServer(new MessageChangePassword(
                                                    c.getArgument("original", String.class),
                                                    c.getArgument("to", String.class)
                                            ));
                                            return 1;
                                        })
                                )
                        )
        );
    }

    public static class ArgumentTypeOriginalPassword implements ArgumentType<String> {

        @Override
        public String parse(StringReader reader) throws CommandSyntaxException {
            return reader.readString();
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            return ISuggestionProvider.suggest(Collections.singleton(SLConfig.CLIENT.password.get()), builder);
        }
    }
}
