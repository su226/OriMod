package su226.orimod.others;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import su226.orimod.Mod;
import su226.orimod.blocks.SpiritSmithingTable;

public class GuiHandler implements IGuiHandler {
  public abstract static class Factory {
    public abstract Gui createGui(EntityPlayer player, World world, int x, int y, int z);
    public abstract Container createContainer(EntityPlayer player, World world, int x, int y, int z);
  }

  private static List<Factory> REGISTRY = new ArrayList<>();

  private GuiHandler() {}
  
  public static void register() {
    NetworkRegistry.INSTANCE.registerGuiHandler(Mod.INSTANCE, new GuiHandler());
    SpiritSmithingTable.registerGui();
  }

  public static int registerGui(Factory factory) {
    REGISTRY.add(factory);
    return REGISTRY.size() - 1;
  }

  @Override
  public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
    if (id >= 0 && id < REGISTRY.size()) {
      return REGISTRY.get(id).createGui(player, world, x, y, z);
    }
    return null;
  }

  @Override
  public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
    if (id >= 0 && id < REGISTRY.size()) {
      return REGISTRY.get(id).createContainer(player, world, x, y, z);
    }
    return null;
  }
}
