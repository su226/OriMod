package su226.orimod.others;

import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import su226.orimod.blocks.SpiritSmithingTable;

public class Interfaces {
  public static ScreenHandlerType<SpiritSmithingTable.Container> SPIRIT_SMITHING_TABLE;

  public static void register() {
    SPIRIT_SMITHING_TABLE = ScreenHandlerRegistry.registerSimple(Util.getIdentifier("spirit_smithing_table"), SpiritSmithingTable.Container::new);
  }

  public static void registerScreen() {
    ScreenRegistry.register(SPIRIT_SMITHING_TABLE, SpiritSmithingTable.Screen::new);
  }
}
