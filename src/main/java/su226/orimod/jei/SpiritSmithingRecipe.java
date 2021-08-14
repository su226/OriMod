package su226.orimod.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;
import su226.orimod.ClientProxy;
import su226.orimod.blocks.SpiritSmithingTable;
import su226.orimod.entities.SpiritLightOrb;
import su226.orimod.others.Models;

public class SpiritSmithingRecipe implements IRecipeWrapper {
  private SpiritSmithingTable.Recipe recipe;
  
  public SpiritSmithingRecipe(SpiritSmithingTable.Recipe recipe) {
    this.recipe = recipe;
  }

  @Override
  public void getIngredients(IIngredients ingredients) {
    ingredients.setInput(VanillaTypes.ITEM, recipe.getInput());
    ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
  }

  @Override
  public void drawInfo(Minecraft mc, int width, int height, int x, int y) {
    double rot = (ClientProxy.tick + mc.getRenderPartialTicks()) * 0.05;
    GlStateManager.enableBlend();
    Models.drawSquare(
      new Vec3d(58 + Math.cos(rot + Math.PI / 2) * 16, 9 + Math.sin(rot + Math.PI / 2) * 16, 0),
      new Vec3d(58 + Math.cos(rot) * 16, 9 + Math.sin(rot) * 16, 0),
      new Vec3d(58 + Math.cos(rot - Math.PI / 2) * 16, 9 + Math.sin(rot - Math.PI / 2) * 16, 0),
    SpiritLightOrb.Render.TEXTURE);
    GlStateManager.disableBlend();

    String str = Integer.toString(this.recipe.getCost());
    mc.fontRenderer.drawString(str, 58 - mc.fontRenderer.getStringWidth(str) / 2, 18, 0x404040);
  }
}
