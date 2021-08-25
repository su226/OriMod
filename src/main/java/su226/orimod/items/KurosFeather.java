package su226.orimod.items;

import java.lang.reflect.Field;
import java.util.List;

import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import su226.orimod.Mod;
import su226.orimod.blocks.SpiritSmithingTable;
import su226.orimod.components.Components;
import su226.orimod.components.IKurosFeather;
import su226.orimod.others.Render;
import su226.orimod.others.Sounds;
import su226.orimod.others.Util;
import su226.orimod.packets.FlapPacket;

public class KurosFeather extends Item {
  static class Feature extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public final FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> prev;

    public Feature(FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> prev) {
      super(getContext(prev));
      this.prev = prev;
    }

    @SuppressWarnings("unchecked")
    private static FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> getContext(FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> prev) {
      try {
        Field field = Util.getFleid(FeatureRenderer.class, "context", "field_17155");
        field.setAccessible(true);
        return (FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>)field.get(prev);
      } catch (Exception e) {
        Mod.LOG.error("Failed to get FeatureRendererContext!", e);
        return null;
      }
    }

    @Override
    public void render(MatrixStack mat, VertexConsumerProvider consumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
      VertexConsumer consumer = consumers.getBuffer(RenderLayer.getCutout());
      mat.multiply(new Quaternion(180, 0, 0, true));
      mat.translate(0, 0.5, 0);
      Render.model(mat, consumer, MODEL, 0xffffffff, 0, light);
    }
  }

  private static final float ANGLE = (float)Math.PI * 0.9f;
  private static final String MODEL = "misc/kuros_feather";

  public KurosFeather() {
    super(new Settings().maxCount(1).group(Items.GROUP));
    SpiritSmithingTable.registerRecipe(new SpiritSmithingTable.Recipe(
      new ItemStack(net.minecraft.item.Items.FEATHER),
      new ItemStack(this),
      300
    ));
  }

  public void setModelAngles(AbstractClientPlayerEntity owner, PlayerEntityModel<AbstractClientPlayerEntity> model) {
    if (Components.KUROS_FEATHER.get(owner).isPrevGliding() && model.body.visible) {
      model.jacket.pitch = model.body.pitch = 0.0F;
      model.rightPants.pivotZ =  model.rightLeg.pivotZ = 0.1F;
      model.leftPants.pivotZ =  model.leftLeg.pivotZ = 0.1F;
      model.rightPants.pivotY =  model.rightLeg.pivotY = 12.0F;
      model.leftPants.pivotY =  model.leftLeg.pivotY = 12.0F;
      model.hat.pivotY = model.head.pivotY = 0.0F;
      model.jacket.pivotY = model.body.pivotY = 0.0F;
      model.leftSleeve.pivotY = model.leftArm.pivotY = 2.0F;
      model.rightSleeve.pivotY = model.rightArm.pivotY = 2.0F;
      model.rightPants.pitch = model.rightLeg.pitch = 0;
      model.leftPants.pitch = model.leftLeg.pitch = 0;
      model.leftSleeve.yaw = model.leftArm.yaw = 0;
      model.leftSleeve.pitch = model.leftArm.pitch = 0;
      model.leftSleeve.roll = model.leftArm.roll = -ANGLE;
      model.rightSleeve.yaw = model.rightArm.yaw = 0;
      model.rightSleeve.pitch = model.rightArm.pitch = 0;
      model.rightSleeve.roll = model.rightArm.roll = ANGLE;
    }
  }

  public boolean onDamage(PlayerEntity player, DamageSource source) {
    return source.name.equals("fall") && TrinketsApi.getTrinketsInventory(player).count(this) != 0;
  }

  @SuppressWarnings("unchecked")
  public void renderPlayerPre(AbstractClientPlayerEntity owner, PlayerEntityRenderer render) {
    IKurosFeather component = Components.KUROS_FEATHER.get(owner);
    if (component.shouldUpdate()) {
      boolean gliding = component.isPrevGliding();
      try {
        Field field = Util.getFleid(LivingEntityRenderer.class, "features", "field_4738");
        field.setAccessible(true);
        List<FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>> features = (List<FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>>)field.get(render);
        for (int i = 0; i < features.size(); i++) {
          FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> layer = features.get(i);
          if (gliding && layer instanceof HeldItemFeatureRenderer) {
            features.set(i, new Feature(layer));
          } else if (!gliding && layer instanceof Feature) {
            features.set(i, ((Feature)layer).prev);
          }
        }
      } catch (Exception e) {
        Mod.LOG.warn("Failed to override player render!", e);
      }
      component.setShouldUpdate(false);
    }
  }

  @Override
  public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
    if (!(entity instanceof PlayerEntity player)) {
      return;
    }
    IKurosFeather component = Components.KUROS_FEATHER.get(player);
    if (!component.flagUpdated()) {
      return;
    }
    boolean gliding = this.canGlide(player);
    if (component.isPrevGliding() != gliding) {
      if (!player.world.isClient) {
        player.world.playSound(null, player.getX(), player.getY(), player.getZ(), gliding ? Sounds.GLIDE_START : Sounds.GLIDE_END, SoundCategory.PLAYERS, 1, 1);
      }
      component.setPrevGliding(gliding);
      component.setShouldUpdate(true);
    }
    if (gliding) {
      double speed = player.forwardSpeed * Mod.CONFIG.kuros_feather.speed_compensation;
      Vec3d velocity = player.getVelocity();
      if (velocity.y < 0) {
        velocity = velocity.multiply(1, Mod.CONFIG.kuros_feather.fall_multiplier, 1);
      }
      if (speed != 0) {
        double radian = Math.toRadians(player.yaw);
        velocity = velocity.add(speed * -Math.sin(radian), 0, speed * Math.cos(radian));
      }
      Util.setVelocity(player, velocity.x, velocity.y, velocity.z, false);
    }
  }

  public boolean canGlide(PlayerEntity owner) {
    return owner.isSneaking() && Util.isAirBorne(owner) && !owner.isFallFlying() && !owner.abilities.flying;
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity owner, Hand hand) {
    ItemCooldownManager cd = owner.getItemCooldownManager();
    if (cd.getCooldownProgress(this, 0) > 0) {
      return TypedActionResult.fail(owner.getStackInHand(hand));
    }
    cd.set(this, Mod.CONFIG.kuros_feather.cooldown);
    if (!world.isClient) {
      Vec3d start = owner.getCameraPosVec(1);
      Vec3d end = start.add(owner.getRotationVec(1).multiply(Mod.CONFIG.kuros_feather.length));
      double dist = end.distanceTo(start);
      Vec3d velocity = end.subtract(start).multiply(Mod.CONFIG.kuros_feather.force);
      List<Entity> ents = Util.entityAroundLine(owner, start, end, Mod.CONFIG.kuros_feather.range);
      for (Entity ent : ents) {
        double ratio = 1 - Math.min(Math.sqrt(ent.squaredDistanceTo(start.x, start.y, start.z)) / dist, 1);
        ent.addVelocity(velocity.x * ratio, velocity.y * ratio, velocity.z * ratio);
      }
      new FlapPacket(owner, end).sendToAround(owner, 32);
    }
    return TypedActionResult.success(owner.getStackInHand(hand));
  }
}
