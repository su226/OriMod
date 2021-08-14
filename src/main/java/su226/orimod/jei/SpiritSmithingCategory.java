package su226.orimod.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import su226.orimod.blocks.SpiritSmithingTable;

public class SpiritSmithingCategory implements IRecipeCategory<SpiritSmithingRecipe> {
  public static final String UID = "orimod:spirit_smithing_table";
  private IDrawable background;

  public SpiritSmithingCategory(IGuiHelper guiHelper) {
    this.background = guiHelper.createDrawable(SpiritSmithingTable.Gui.TEXTURE, 26, 46, 125, 24);
  }

  @Override
  public String getUid() {
    return UID;
  }

  @Override
  public String getTitle() {
    return I18n.format("jei.orimod.recipe.spirit_smithing");
  }

  @Override
  public String getModName() {
    return "OriMod";
  }

  @Override
  public IDrawable getBackground() {
    return this.background;
  }

  @Override
  public void setRecipe(IRecipeLayout recipeLayout, SpiritSmithingRecipe recipeWrapper, IIngredients ingredients) {
    IGuiItemStackGroup group = recipeLayout.getItemStacks();
    group.init(0, true, 0, 0);
    group.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));

    group.init(1, false, 107, 0);
    group.set(1, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
  }
}
