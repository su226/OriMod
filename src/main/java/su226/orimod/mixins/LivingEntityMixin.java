package su226.orimod.mixins;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su226.orimod.components.ISpiritLight;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
  @Inject(at = @At("HEAD"), method = "onDeath(Lnet/minecraft/entity/damage/DamageSource;)V")
  void onDeath(DamageSource source, CallbackInfo info) {
    ISpiritLight.livingDeath((LivingEntity)(Object)this);
  }
}
