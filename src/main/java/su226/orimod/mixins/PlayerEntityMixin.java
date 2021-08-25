package su226.orimod.mixins;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su226.orimod.items.Items;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
  @Inject(at = @At("HEAD"), cancellable = true, method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z")
  void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
    if (Items.KUROS_FEATHER.onDamage((PlayerEntity)(Object)this, source)) {
      info.setReturnValue(false);
    }
  }
}
