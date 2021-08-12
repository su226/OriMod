package su226.orimod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import su226.orimod.items.Items;

@Mixin(ModelPlayer.class)
public class MixinModelPlayer {
  @Inject(at = @At("RETURN"), method = "setRotationAngles(FFFFFFLnet/minecraft/entity/Entity;)V")
  public void setRotationAngles(float swing, float swingAmount, float age, float yaw, float pitch, float scale, Entity entity, CallbackInfo info) {
    Items.KUROS_FEATHER.setRotationAngles((ModelPlayer)(Object)this, (EntityPlayer)entity);
  }
}
