package su226.orimod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import su226.orimod.components.Components;
import su226.orimod.components.ISpiritLight;

import java.util.Collection;

public class SpiritLightCommand {
  public static ArgumentBuilder<ServerCommandSource, LiteralArgumentBuilder<ServerCommandSource>> subCommandGet() {
    return CommandManager.literal("get")
      .requires(src -> src.hasPermissionLevel(2))
      .then(CommandManager.argument("players", EntityArgumentType.players())
      .executes(ctx -> {
        int ret = -1;
        for (PlayerEntity player : EntityArgumentType.getPlayers(ctx, "players")) {
          int value = Components.SPIRIT_LIGHT.get(player).get();
          if (ret == -1) {
            ret = value;
          }
          ctx.getSource().sendFeedback(new TranslatableText("commands.orimod.spiritlight.success_get", player.getName(), value), false);
        }
        return ret;
      }));
  }

  public static ArgumentBuilder<ServerCommandSource, LiteralArgumentBuilder<ServerCommandSource>> subCommandSet() {
    return CommandManager.literal("set")
      .requires(src -> src.hasPermissionLevel(2))
      .then(CommandManager.argument("players", EntityArgumentType.players())
      .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
      .executes(ctx -> {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(ctx, "players");
        int value = IntegerArgumentType.getInteger(ctx, "value");
        for (PlayerEntity player : players) {
          Components.SPIRIT_LIGHT.get(player).set(value);
          ctx.getSource().sendFeedback(new TranslatableText("commands.orimod.spiritlight.success_set", player.getName(), value), false);
        }
        return players.size();
      })));
  }

  public static ArgumentBuilder<ServerCommandSource, LiteralArgumentBuilder<ServerCommandSource>> subCommandAdd() {
    return CommandManager.literal("add")
      .requires(src -> src.hasPermissionLevel(2))
      .then(CommandManager.argument("players", EntityArgumentType.players())
      .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
      .executes(ctx -> {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(ctx, "players");
        int value = IntegerArgumentType.getInteger(ctx, "value");
        for (PlayerEntity player : players) {
          ISpiritLight component = Components.SPIRIT_LIGHT.get(player);
          component.set(component.get() + value);
          ctx.getSource().sendFeedback(new TranslatableText("commands.orimod.spiritlight.success_add", player.getName(), value), false);
        }
        return players.size();
      })));
  }

  public static ArgumentBuilder<ServerCommandSource, LiteralArgumentBuilder<ServerCommandSource>> subCommandMe() {
    return CommandManager.literal("me")
      .executes(ctx -> {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        int value = Components.SPIRIT_LIGHT.get(player).get();
        ctx.getSource().sendFeedback(new TranslatableText("commands.orimod.spiritlight.success_me", value), false);
        return value;
      });
  }

  public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
    dispatcher.register(CommandManager.literal("spiritlight")
      .then(subCommandGet())
      .then(subCommandSet())
      .then(subCommandAdd())
      .then(subCommandMe())
    );
  }
}
