package su226.orimod.others;

import net.minecraft.item.ItemStack;

public interface IDurability {
  int getDurabilityColor(ItemStack stack);
  float getDurability(ItemStack stack);
  boolean showDurability(ItemStack stack);
}
