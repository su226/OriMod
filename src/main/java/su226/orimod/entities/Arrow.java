package su226.orimod.entities;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Quaternion;
import net.minecraft.world.World;
import su226.orimod.Mod;
import su226.orimod.others.IEntitySync;
import su226.orimod.others.Sounds;
import su226.orimod.packets.SpiritArcPacket;

public class Arrow extends PersistentProjectileEntity implements IEntitySync {
  public static class Render extends EntityRenderer<Arrow> {
    public static final String MODEL = "misc/arrow";
    public static final String MODEL_OVERLAY = "misc/arrow_overlay";

    public Render(EntityRenderDispatcher dispatcher, EntityRendererRegistry.Context context) {
      super(dispatcher);
    }

    @Override
    public Identifier getTexture(Arrow entity) {
      return null;
    }

    @Override
    public void render(Arrow entity, float yaw, float tickDelta, MatrixStack mat, VertexConsumerProvider consumers, int light) {
      VertexConsumer comsumer = consumers.getBuffer(RenderLayer.getTranslucent());
      mat.multiply(new Quaternion(0, entity.yaw + (entity.yaw - entity.prevYaw) * tickDelta - 90, entity.pitch + (entity.pitch - entity.prevPitch) * tickDelta, true));
      mat.translate(-0.5, 0, 0);
      su226.orimod.others.Render.model(mat, comsumer, MODEL, 0xffffffff, 0, 15728880);
      su226.orimod.others.Render.model(mat, comsumer, MODEL_OVERLAY, Mod.CONFIG.glow_color, 0, 15728880);
    }
  }

  public Arrow(EntityType<? extends Arrow> type, World world) {
    super(type, world);
  }

  public Arrow(World world, LivingEntity owner) {
    super(Entities.ARROW, owner, world);
    this.setDamage(Mod.CONFIG.spirit_arc.base_damage);
    this.setPunch(Mod.CONFIG.spirit_arc.knockback);
    this.setSilent(true);
  }

  @Override
  protected ItemStack asItemStack() {
    return ItemStack.EMPTY;
  }

  @Override
  protected void onCollision(HitResult hit) {
    super.onCollision(hit);
    if (this.world.isClient || hit.getType() == HitResult.Type.MISS) {
      return;
    }
    Entity ent = hit.getType() == HitResult.Type.ENTITY ? ((EntityHitResult)hit).getEntity() : null;
    new SpiritArcPacket(this.getOwner(), ent, this.getPos(), hit.getPos()).sendToAround(this, 32);
  }

  @Override
  protected void onBlockHit(BlockHitResult blockHitResult) {
    this.kill();
  }

  @Override
  protected SoundEvent getHitSound() {
    return Sounds.ARROW_HIT_ENTITY;
  }

  @Override
  public Packet<?> createSpawnPacket() {
    return new EntitySpawnS2CPacket(this, this.getOwner().getEntityId());
  }

  @Override
  public void applySyncData(int data) {
    this.setOwner(this.world.getEntityById(data));
  }
}
