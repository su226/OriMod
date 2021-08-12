package su226.orimod.items;

import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import su226.orimod.others.Models;
import su226.orimod.others.Util;

public class Icon extends Item {
  public Icon() {
    super();
    this.setRegistryName(Util.getLocation("icon"));
    this.setUnlocalizedName(Util.getI18nKey("icon"));
  }

  @SideOnly(Side.CLIENT)
  public void setModel() {
    Models.setItemModel(this, "icon");
  }
}
