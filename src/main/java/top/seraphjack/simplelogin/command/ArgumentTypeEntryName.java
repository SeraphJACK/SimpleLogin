package top.seraphjack.simplelogin.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import top.seraphjack.simplelogin.server.storage.SLStorage;

import java.util.concurrent.CompletableFuture;

public final class ArgumentTypeEntryName implements ArgumentType<EntryNameInput> {

    private ArgumentTypeEntryName() {
    }

    public static ArgumentTypeEntryName entryName() {
        return new ArgumentTypeEntryName();
    }

    @Override
    public EntryNameInput parse(StringReader reader) throws CommandSyntaxException {
        String name = reader.readString();

        if (!SLStorage.instance().storageProvider.registered(name)) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(new StringReader(name));
        }
        return EntryNameInput.of(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (context.getSource() instanceof CommandSource) {
            return ISuggestionProvider.suggest(SLStorage.instance().storageProvider.getAllRegisteredUsername(), builder);
        } else if (context.getSource() instanceof ISuggestionProvider) {
            CommandContext<ISuggestionProvider> ctx = (CommandContext<ISuggestionProvider>) context;
            return ((ISuggestionProvider) context.getSource()).getSuggestionsFromServer(ctx, builder);
        }
        return Suggestions.empty();
    }

    public static String getEntryName(CommandContext<CommandSource> ctx, String name) {
        return ctx.getArgument(name, EntryNameInput.class).getName();
    }
}
