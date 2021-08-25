package su226.orimod.mixins;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su226.orimod.items.Items;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
  @Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V")
  public void render(AbstractClientPlayerEntity player, float yaw, float tickDelta, MatrixStack mat, VertexConsumerProvider consumers, int light, CallbackInfo info) {
    Items.KUROS_FEATHER.renderPlayerPre(player, (PlayerEntityRenderer)(Object)this);
    Items.STOMP.renderPlayerPre(player, tickDelta, mat);
  }

  @Inject(at = @At("RETURN"), method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V")
  public void renderPost(AbstractClientPlayerEntity player, float yaw, float tickDelta, MatrixStack mat, VertexConsumerProvider consumers, int light, CallbackInfo info) {
    Items.STOMP.renderPlayerPost(player, tickDelta, mat);
  }
}
