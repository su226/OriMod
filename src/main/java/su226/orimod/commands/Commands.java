package su226.orimod.commands;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class Commands {
  public static void register(FMLServerStartingEvent event) {
    event.registerServerCommand(new SpiritLightCommand());
  }
}
