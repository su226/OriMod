package su226.orimod.commands;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import su226.orimod.capabilities.Capabilities;
import su226.orimod.capabilities.ISpiritLight;

public class SpiritLightCommand extends CommandBase {
  @Override
  public String getName() {
    return "spiritlight";
  }

  @Override
  public String getUsage(ICommandSender sender) {
    return "commands.orimod.spiritlight.usage";
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
    if (args.length != 3) {
      sender.sendMessage(new TextComponentTranslation("commands.orimod.spiritlight.help_add"));
      sender.sendMessage(new TextComponentTranslation("commands.orimod.spiritlight.help_set"));
      return;
    }
    boolean isAdd = args[0].equals("add");
    boolean isSet = args[0].equals("set");
    if (!isAdd && !isSet) {
      throw new CommandException("commands.orimod.spiritlight.failed_invaild_command", args[1]);
    }
    int count;
    try {
      count = Integer.parseInt(args[2]);
    } catch (NumberFormatException e) {
      throw new CommandException("commands.orimod.spiritlight.failed_invaild_number", args[2]);
    }
    List<EntityPlayerMP> players = getPlayers(server, sender, args[1]);
    for (EntityPlayerMP player : players) {
      ISpiritLight cap = player.getCapability(Capabilities.SPIRIT_LIGHT, null);
      if (isAdd) {
        cap.set(cap.get() + count);
        sender.sendMessage(new TextComponentTranslation("commands.orimod.spiritlight.success_add", player.getName(), count));
      } else {
        cap.set(count);
        sender.sendMessage(new TextComponentTranslation("commands.orimod.spiritlight.success_set", player.getName(), count));
      }
    }
  }

  @Override
  public int getRequiredPermissionLevel() {
    return 2;
  }
}
