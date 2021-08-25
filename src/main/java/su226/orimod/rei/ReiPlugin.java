package su226.orimod.rei;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.minecraft.util.Identifier;
import su226.orimod.blocks.SpiritSmithingTable;
import su226.orimod.items.Items;
import su226.orimod.others.Util;

public class ReiPlugin implements REIPluginV0 {
  @Override
  public Identifier getPluginIdentifier() {
    return Util.getIdentifier("orimod");
  }

  @Override
  public void registerPluginCategories(RecipeHelper helper) {
    helper.registerCategory(new SpiritSmithingCategory());
  }

  @Override
  public void registerRecipeDisplays(RecipeHelper helper) {
    for (SpiritSmithingTable.Recipe recipe : SpiritSmithingTable.RECIPES) {
      helper.registerDisplay(new SpiritSmithingCategory.Display(recipe));
    }
  }

  @Override
  public void registerOthers(RecipeHelper helper) {
    helper.registerContainerClickArea(new Rectangle(99, 45, 28, 21), SpiritSmithingTable.Screen.class, SpiritSmithingCategory.ID);
    helper.registerWorkingStations(SpiritSmithingCategory.ID, EntryStack.create(Items.SPIRIT_SMITHING_TABLE));
    // REI's auto transfer is kind of hard to code... I give up.
  }
}
