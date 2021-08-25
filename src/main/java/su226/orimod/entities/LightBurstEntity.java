package su226.orimod.entities;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import su226.orimod.Mod;
import su226.orimod.items.Items;
import su226.orimod.items.LightBurst;
import su226.orimod.items.SpiritFlame;
import su226.orimod.others.IEntitySync;
import su226.orimod.others.PureExplosion;
import su226.orimod.packets.LightBurstPacket;

public class LightBurstEntity extends ThrownItemEntity implements IEntitySync {
  public static class Render extends EntityRenderer<LightBurstEntity> {
    public static final ItemStack STACK = new ItemStack(Items.LIGHT_BURST);

    protected Render(EntityRenderDispatcher dispatcher, EntityRendererRegistry.Context context) {
      super(dispatcher);
    }

    @Override
    public Identifier getTexture(LightBurstEntity entity) {
      return null;
    }

    @Override
    public void render(LightBurstEntity entity, float yaw, float tickDelta, MatrixStack mat, VertexConsumerProvider consumers, int light) {
      VertexConsumer consumer = consumers.getBuffer(RenderLayer.getTranslucent());
      mat.push();
      if (Mod.CONFIG.enable_3d) {
        mat.translate(-0.5, -0.5, -0.5);
        su226.orimod.others.Render.model(mat, consumer, LightBurst.MODEL, 0xffffffff, 0, su226.orimod.others.Render.FULL_BRIGHT);
        mat.translate(-0.05, -0.05, -0.05);
        mat.scale(1.1f, 1.1f, 1.1f);
        su226.orimod.others.Render.model(mat, consumer, SpiritFlame.MODEL_OVERLAY, LightBurst.GLOW_COLOR, 0, su226.orimod.others.Render.FULL_BRIGHT);
      } else {
        mat.multiply(this.dispatcher.getRotation());
        mat.multiply(new Quaternion(0, 180, 0, true));
        MinecraftClient.getInstance().getItemRenderer().renderItem(STACK, ModelTransformation.Mode.GROUND, su226.orimod.others.Render.FULL_BRIGHT, 0, mat, consumers);
      }
      mat.pop();
    }
  }

  public LightBurstEntity(EntityType<? extends LightBurstEntity> type, World world) {
    super(type, world);
  }

  public LightBurstEntity(LivingEntity owner) {
    super(Entities.LIGHT_BURST, owner, owner.world);
  }

  @Override
  protected void onCollision(HitResult hit) {
    if (!this.world.isClient) {
      Vec3d pos = this.getPos();
      Entity owner = this.getOwner();
      Entity hitEnt = null;
      if (hit.getType() == HitResult.Type.ENTITY) {
        EntityHitResult ent = (EntityHitResult)hit;
        hitEnt = ent.getEntity();
        if (hitEnt != owner) {
          ent.getEntity().damage(DamageSource.explosion((LivingEntity)(owner instanceof LivingEntity ? owner : null)), Mod.CONFIG.light_burst.damage);
        }
      }
      new PureExplosion(this, owner, Mod.CONFIG.light_burst.explosion_force).explode();
      new LightBurstPacket(owner, hitEnt, pos, hit.getPos()).sendToAround(this, 32);
    }
    this.kill();
  }

  @Override
  protected Item getDefaultItem() {
    return Items.LIGHT_BURST;
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
