package su226.orimod.items;

import net.minecraft.client.render.*;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.math.Quaternion;
import su226.orimod.Mod;
import su226.orimod.blocks.SpiritSmithingTable;
import su226.orimod.others.ICustomRender;
import su226.orimod.others.Render;

public class SpiritEdge extends SwordItem implements ICustomRender {
  public static final ToolMaterial MATERIAL = new ToolMaterial() {
    @Override
    public int getDurability() {
      return 0;
    }

    @Override
    public float getMiningSpeedMultiplier() {
      return Mod.CONFIG.tools.efficiency;
    }

    @Override
    public float getAttackDamage() {
      return Mod.CONFIG.tools.damage;
    }

    @Override
    public int getMiningLevel() {
      return Mod.CONFIG.tools.harvest_level;
    }

    @Override
    public int getEnchantability() {
      return Mod.CONFIG.tools.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
      return null;
    }
  };
  private static final String MODEL = "item/3d/spirit_edge";
  private static final String MODEL_OVERLAY = "item/3d/spirit_edge_overlay";

  public SpiritEdge() {
    super(MATERIAL, 3, -2.4f, new Settings().group(Items.GROUP));
    SpiritSmithingTable.registerRecipe(new SpiritSmithingTable.Recipe(
      new ItemStack(net.minecraft.item.Items.DIAMOND_SWORD),
      new ItemStack(this),
      300
    ));
  }

  @Override
  public void render(ItemStack stack, ModelTransformation.Mode mode, boolean left, MatrixStack mat, VertexConsumerProvider consumers, int light, int overlay, BakedModel model) {
    if (mode == ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND || mode == ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND) {
      mat.multiply(new Quaternion(0, -90, 0, true));
      mat.translate(0, 0, -1);
    }
    VertexConsumer consumer = consumers.getBuffer(RenderLayer.getTranslucent());
    Render.model(mat, consumer, MODEL, 0xffffffff, 0, 15728880);
    Render.model(mat, consumer, MODEL_OVERLAY, Mod.CONFIG.glow_color, 0, 15728880);
  }
}
