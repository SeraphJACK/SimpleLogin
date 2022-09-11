package top.seraphjack.simplelogin.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;
import top.seraphjack.simplelogin.server.SLRegistries;
import top.seraphjack.simplelogin.server.handler.PlayerLoginHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class ArgumentTypeHandlerPlugin implements ArgumentType<HandlerPluginInput> {
    /**
     * 0 to suggest loaded plugins
     * 1 to suggest unloaded plugins
     * 2 to suggest all plugins
     * Since suggestion is done on server-side, there's no need to serialize this,
     * so we're using empty argument serializer.
     */
    private final int type;

    private ArgumentTypeHandlerPlugin(int type) {
        this.type = type;
    }

    public static ArgumentTypeHandlerPlugin loadedPlugins() {
        return new ArgumentTypeHandlerPlugin(0);
    }

    public static ArgumentTypeHandlerPlugin unloadedPlugins() {
        return new ArgumentTypeHandlerPlugin(1);
    }

    public static ArgumentTypeHandlerPlugin allPlugins() {
        return new ArgumentTypeHandlerPlugin(2);
    }

    public static <S> ResourceLocation getPlugin(CommandContext<S> ctx, String name) {
        return ctx.getArgument(name, HandlerPluginInput.class).get();
    }

    @Override
    public HandlerPluginInput parse(StringReader reader) throws CommandSyntaxException {
        return HandlerPluginInput.of(ResourceLocation.read(reader));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (context.getSource() instanceof CommandSourceStack) {
            Set<ResourceLocation> plugins = new HashSet<>();
            if (type == 0) {
                plugins.addAll(PlayerLoginHandler.instance().listPlugins());
            } else if (type == 1) {
                plugins.addAll(SLRegistries.PLUGINS.list());
                plugins.removeAll(PlayerLoginHandler.instance().listPlugins());
            } else if (type == 2) {
                plugins.addAll(SLRegistries.PLUGINS.list());
            }
            return SharedSuggestionProvider.suggest(plugins.stream().map(ResourceLocation::toString), builder);
        } else if (context.getSource() instanceof ClientSuggestionProvider src) {
            return src.customSuggestion(context);
        }
        return Suggestions.empty();
    }
}
