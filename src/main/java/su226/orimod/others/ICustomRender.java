package su226.orimod.others;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import su226.orimod.Mod;

public interface ICustomRender {
  enum Mode {
    OVERLAY,
    REPLACE,
    NONE
  }

  default boolean applyCustomRender() { return Mod.CONFIG.enable_3d; }
  default boolean applyDefaultTransform() { return false; }
  @Environment(EnvType.CLIENT)
  void render(ItemStack stack, ModelTransformation.Mode mode, boolean left, MatrixStack mat, VertexConsumerProvider consumers, int light, int overlay, BakedModel model);
}