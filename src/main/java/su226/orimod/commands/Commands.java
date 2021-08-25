package su226.orimod.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

public class Commands {
  public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
    SpiritLightCommand.register(dispatcher);
  }
}
