package su226.orimod.mixins;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su226.orimod.items.Items;

@Mixin(PlayerEntityModel.class)
public class PlayerEntityModelMixin {
  @SuppressWarnings({"unchecked"})
  @Inject(at = @At("RETURN"), method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V")
  private void setAngles(LivingEntity player, float f, float g, float h, float i, float j, CallbackInfo info) {
    Items.KUROS_FEATHER.setModelAngles((AbstractClientPlayerEntity)player, (PlayerEntityModel<AbstractClientPlayerEntity>)(Object)this);
  }
}
