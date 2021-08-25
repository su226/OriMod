package su226.orimod.mixins;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su226.orimod.others.IEntitySync;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
  @Shadow
  private ClientWorld world;

  @Inject(at = @At("RETURN"), method = "onEntitySpawn")
  private void onEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo ci) {
    double x = packet.getX();
    double y = packet.getY();
    double z = packet.getZ();
    EntityType<?> type = packet.getEntityTypeId();
    if (Registry.ENTITY_TYPE.getId(type).getNamespace().equals("orimod")) {
      Entity ent = type.create(this.world);
      int id = packet.getId();
      ent.setPosition(x, y, z);
      ent.updateTrackedPosition(x, y, z);
      ent.refreshPositionAfterTeleport(x, y, z);
      ent.setVelocity(packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityZ());
      ent.pitch = packet.getPitch() * 360 / 256f;
      ent.yaw = packet.getYaw() * 360 / 256f;
      ent.setEntityId(id);
      ent.setUuid(packet.getUuid());
      ((IEntitySync)ent).applySyncData(packet.getEntityData());
      this.world.addEntity(id, ent);
    }
  }
}
