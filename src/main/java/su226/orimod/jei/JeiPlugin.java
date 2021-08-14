package su226.orimod.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.item.ItemStack;
import su226.orimod.blocks.SpiritSmithingTable;
import su226.orimod.items.Items;

@JEIPlugin
public class JeiPlugin implements IModPlugin {
  @Override
  public void registerCategories(IRecipeCategoryRegistration registry) {
    IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
    registry.addRecipeCategories(
      new SpiritSmithingCategory(guiHelper)
    );
  }

  @Override
  public void register(IModRegistry registry) {
    IRecipeTransferRegistry transfer = registry.getRecipeTransferRegistry();

    registry.handleRecipes(SpiritSmithingTable.Recipe.class, SpiritSmithingRecipe::new, SpiritSmithingCategory.UID);
    registry.addRecipes(SpiritSmithingTable.RECIPES, SpiritSmithingCategory.UID);
    registry.addRecipeCatalyst(new ItemStack(Items.SPIRIT_SMITHING_TABLE), SpiritSmithingCategory.UID);
    registry.addRecipeClickArea(SpiritSmithingTable.Gui.class, 99, 45, 28, 21, SpiritSmithingCategory.UID);
    transfer.addRecipeTransferHandler(SpiritSmithingTable.Container.class, SpiritSmithingCategory.UID, 0, 1, 2, 36);
  }
}
