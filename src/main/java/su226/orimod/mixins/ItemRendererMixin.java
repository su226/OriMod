package su226.orimod.mixins;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su226.orimod.others.ICustomRender;
import su226.orimod.others.IDurability;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
  @Inject(at = @At("HEAD"), cancellable = true, method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V")
  public void renderItem(ItemStack stack, ModelTransformation.Mode mode, boolean left, MatrixStack mat, VertexConsumerProvider consumers, int light, int overlay, BakedModel model, CallbackInfo info) {
    Item item = stack.getItem();
    if (item instanceof ICustomRender render && render.applyCustomRender()) {
      mat.push();
      if (render.applyDefaultTransform()) {
        model.getTransformation().getTransformation(mode).apply(left, mat);
      }
      mat.translate(-0.5, -0.5, -0.5);
      render.render(stack, mode, left, mat, consumers, light, overlay, model);
      mat.pop();
      info.cancel();
    }
  }

  @Inject(at = @At("HEAD"), method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
  public void renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo info) {
    Item item = stack.getItem();
    if (item instanceof IDurability durability && durability.showDurability(stack)) {
      int color = durability.getDurabilityColor(stack);
      int a = color >>> 24;
      int r = color >> 16 & 0xff;
      int g = color >> 8 & 0xff;
      int b = color & 0xff;
      float fraction = durability.getDurability(stack);
      double maxx = 14 * fraction + 1;
      GlStateManager.disableTexture();
      GlStateManager.disableDepthTest();
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferBuilder = tessellator.getBuffer();
      Tessellator tess = Tessellator.getInstance();
      BufferBuilder buf = tess.getBuffer();
      buf.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
      buf.vertex(x + 1, y + 13, 100).color(0, 0, 0, a).next();
      buf.vertex(x + 1, y + 15, 100).color(0, 0, 0, a).next();
      buf.vertex(x + 15, y + 15, 100).color(0, 0, 0, a).next();
      buf.vertex(x + 15, y + 13, 100).color(0, 0, 0, a).next();
      buf.vertex(x + 1, y + 13, 100).color(r, g, b, a).next();
      buf.vertex(x + 1, y + 14, 100).color(r, g, b, a).next();
      buf.vertex(x + maxx, y + 14, 100).color(r, g, b, a).next();
      buf.vertex(x + maxx, y + 13, 100).color(r, g, b, a).next();
      tess.draw();
      GlStateManager.enableDepthTest();
      GlStateManager.enableTexture();
    }
  }
}
