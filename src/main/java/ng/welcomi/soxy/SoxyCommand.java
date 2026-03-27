package ng.welcomi.soxy;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.cortex.voxy.commonImpl.VoxyCommon;
import me.cortex.voxy.commonImpl.WorldIdentifier;
import me.cortex.voxy.commonImpl.importers.WorldImporter;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;

public class SoxyCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register(SoxyCommand::registerCommand);
    }

    private static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("soxy")
                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_OWNER))
                .then(Commands.literal("generate")
                        .executes(SoxyCommand::generateAll)
                        .then(Commands.argument("dim", StringArgumentType.string())
                                .executes(SoxyCommand::generateDim)));
//                .then(Commands.literal("status")
//                        .executes(SoxyCommand::status));
        dispatcher.register(command);
    }

    private static int generateAll(CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().getServer().getAllLevels().forEach(level -> generateForLevel(ctx, level));
        return 1;
    }

    private static int generateDim(CommandContext<CommandSourceStack> ctx) {
        String dimStr = StringArgumentType.getString(ctx, "dim");
        ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, Identifier.parse(dimStr));
        ServerLevel level = ctx.getSource().getServer().getLevel(key);
        
        if (level == null) {
            ctx.getSource().sendFailure(Component.literal("Unknown dimension: " + dimStr));
            return 0;
        }
        return generateForLevel(ctx, level);
    }

    private static int generateForLevel(CommandContext<CommandSourceStack> ctx, ServerLevel level) {
        var instance = VoxyCommon.getInstance();
        if (instance == null) {
            ctx.getSource().sendFailure(Component.literal("Voxy instance is not enabled on server"));
            return 0;
        }

        var engine = WorldIdentifier.ofEngine(level);
        if (engine == null) {
            ctx.getSource().sendFailure(Component.literal("Could not identify world engine"));
            return 0;
        }

        MinecraftServer server = ctx.getSource().getServer();
        ctx.getSource().sendSuccess(() -> Component.literal("Flushing world chunks to disk for " + level.dimension().identifier() + "..."), true);
        
        server.execute(() -> {
            level.save(null, true, false);
            File regionPath = (level.dimension() == Level.OVERWORLD ? 
                server.getWorldPath(LevelResource.ROOT) : 
                DimensionType.getStorageFolder(level.dimension(), server.getWorldPath(LevelResource.ROOT)))
                .resolve("region").toFile();

            if (!regionPath.exists() || !regionPath.isDirectory()) {
                ctx.getSource().sendFailure(Component.literal("Cannot find region folder for dimension"));
                return;
            }

            if (instance.getImportManager().makeAndRunIfNone(engine, () -> {
                var importer = new WorldImporter(engine, level, instance.getServiceManager(), instance.savingServiceRateLimiter);
                importer.importRegionDirectoryAsync(regionPath);
                return importer;
            })) {
                ctx.getSource().sendSuccess(() -> Component.literal("Started Voxy cache generation for " + level.dimension().identifier()), true);
            } else {
                ctx.getSource().sendFailure(Component.literal("Generation already in progress for this world"));
            }
        });

        return 1;
    }
}
