package top.seraphjack.simplelogin.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.GameType;
import top.seraphjack.simplelogin.server.storage.SLStorage;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SLCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("simplelogin")
                        .then(
                                Commands.literal("save").requires((c) -> c.hasPermissionLevel(3)).executes((c) -> {
                                    try {
                                        long start = System.currentTimeMillis();
                                        SLStorage.instance().storageProvider.save();
                                        long cost = System.currentTimeMillis() - start;
                                        c.getSource().sendFeedback(new StringTextComponent("Done. Took " + cost + " ms."), true);
                                    } catch (IOException e) {
                                        c.getSource().sendFeedback(new StringTextComponent("Error during saving entries, see log for details"), false);
                                        return 0;
                                    }
                                    return 1;
                                })
                        )
                        .then(
                                Commands.literal("unregister").requires((c) -> c.hasPermissionLevel(3)).then(
                                        Commands.argument("entry", new ArgumentTypeEntryName()).executes((c) -> {
                                            SLStorage.instance().storageProvider.unregister(c.getArgument("entry", String.class));
                                            c.getSource().sendFeedback(new StringTextComponent("Successfully unregistered."), false);
                                            return 1;
                                        })
                                )
                        )
                        .then(
                                Commands.literal("setDefaultGameType").requires((c) -> c.hasPermissionLevel(3)).then(
                                        Commands.argument("entry", new ArgumentTypeEntryName()).then(
                                                Commands.argument("mode", IntegerArgumentType.integer(0, 3)).executes((c) -> {
                                                    GameType gameType = GameType.values()[c.getArgument("mode", Integer.class) + 1];
                                                    SLStorage.instance().storageProvider.setGameType(c.getArgument("entry", String.class), gameType);
                                                    c.getSource().sendFeedback(new StringTextComponent("Successfully set entry default game type to " + gameType.getName() + "."), true);
                                                    return 1;
                                                })
                                        )
                                )
                        )
        );
    }

    public static final class ArgumentTypeEntryName implements ArgumentType<String> {
        private final Collection<String> suggestions;

        public ArgumentTypeEntryName() {
            suggestions = SLStorage.instance().storageProvider.getAllRegisteredUsername();
        }

        public ArgumentTypeEntryName(Collection<String> suggestions) {
            this.suggestions = suggestions;
        }

        public Collection<String> getSuggestions() {
            return suggestions;
        }

        @Override
        public String parse(StringReader reader) throws CommandSyntaxException {
            String name = reader.readString();
            if (!SLStorage.instance().storageProvider.getAllRegisteredUsername().contains(name)) {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(new StringReader("Entry doesn't exist"));
            }
            return name;
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            return ISuggestionProvider.suggest(suggestions, builder);
        }

        @ParametersAreNonnullByDefault
        public static class Serializer implements IArgumentSerializer<ArgumentTypeEntryName> {
            @Override
            public void write(SLCommand.ArgumentTypeEntryName argument, PacketBuffer buffer) {
            }

            @Override
            @Nonnull
            public SLCommand.ArgumentTypeEntryName read(PacketBuffer buffer) {
                return new SLCommand.ArgumentTypeEntryName(Collections.emptySet());
            }

            @Override
            public void write(SLCommand.ArgumentTypeEntryName argument, JsonObject json) {
            }
        }
    }
}
