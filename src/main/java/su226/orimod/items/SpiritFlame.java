package su226.orimod.items;

import java.util.List;

import net.minecraft.client.render.*;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import su226.orimod.Mod;
import su226.orimod.blocks.SpiritSmithingTable;
import su226.orimod.components.Components;
import su226.orimod.others.*;
import su226.orimod.packets.ChargeFlamePacket;
import su226.orimod.packets.SpiritFlamePacket;

public class SpiritFlame extends Item implements ICustomRender, IDurability {
  private static final float ANGLE = (float)Math.PI * 0.2f;
  private static final int MAX_TIME = 72000;

  public static final String MODEL = "item/3d/spirit_flame";
  public static final String MODEL_OVERLAY  = "item/3d/spirit_flame_overlay";

  public SpiritFlame() {
    super(new Settings().group(Items.GROUP).maxCount(1));
    SpiritSmithingTable.registerRecipe(new SpiritSmithingTable.Recipe(
      new ItemStack(net.minecraft.item.Items.BLAZE_POWDER),
      new ItemStack(this),
      300
    ));
  }

  @Override
  public void render(ItemStack stack, ModelTransformation.Mode mode, boolean left, MatrixStack mat, VertexConsumerProvider consumers, int light, int overlay, BakedModel model) {
    VertexConsumer consumer = consumers.getBuffer(RenderLayer.getTranslucent());
    Render.model(mat, consumer, MODEL, 0xffffffff, 0, Render.FULL_BRIGHT);
    float scale = 1.1f + 0.4f * Components.CHARGEABLE.get(stack).getCharge();
    double translate = (1 - scale) / 2;
    mat.translate(translate, translate, translate);
    mat.scale(scale, scale, scale);
    Render.model(mat, consumer, MODEL_OVERLAY, Mod.CONFIG.glow_color, 0, Render.FULL_BRIGHT);
  }

  @Override
  public boolean showDurability(ItemStack stack) {
    return Components.CHARGEABLE.get(stack).isCharging();
  }

  @Override
  public float getDurability(ItemStack stack) {
    return Components.CHARGEABLE.get(stack).getCharge();
  }

  @Override
  public int getDurabilityColor(ItemStack stack) {
    return 0xff00ff00;
  }

  @Override
  public int getMaxUseTime(ItemStack stack) {
    return MAX_TIME;
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    ItemStack stack = user.getStackInHand(hand);
    if (world.isClient) {
      Components.CHARGEABLE.get(stack).beginCharge(Mod.CONFIG.spirit_flame.charge_duration);
    } else {
      doSpiritFlame(user);
    }
    user.setCurrentHand(hand);
    return TypedActionResult.fail(stack);
  }

  @Override
  public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
    int duration = MAX_TIME - remainingUseTicks;
    if (world.isClient) {
      Components.CHARGEABLE.get(stack).endCharge();
    } else if (duration > Mod.CONFIG.spirit_flame.charge_duration) {
      new PureExplosion(user, user, Mod.CONFIG.spirit_flame.explosion_force).explode();
      new ChargeFlamePacket(user).sendToAround(user, 32);
    }
  }

  private void doSpiritFlame(PlayerEntity owner) {
    Vec3d center = owner.getCameraPosVec(1);
    Vec3d offset = new Vec3d(Mod.CONFIG.spirit_flame.radius, Mod.CONFIG.spirit_flame.radius, Mod.CONFIG.spirit_flame.radius);
    Vec3d start = center.add(offset);
    Vec3d end = center.subtract(offset);
    List<LivingEntity> ents = owner.world.getEntitiesByClass(LivingEntity.class, new Box(start.x, start.y, start.z, end.x, end.y, end.z), null);
    ents.sort((a, b) -> (int)Math.signum(a.distanceTo(owner) - b.distanceTo(owner)));
    int damaged = 0;
    for (int i = 0; i < ents.size() && damaged < Mod.CONFIG.spirit_flame.targets; i++) {
      LivingEntity ent = ents.get(i);
      if (ent.isDead() || ent == owner || (ent instanceof TameableEntity && ((TameableEntity)ent).getOwner() == owner && Mod.CONFIG.spirit_flame.ignore_pets)) {
        continue;
      }
      this.attack(owner, ent);
      damaged++;
    }
    for (; damaged < Mod.CONFIG.spirit_flame.targets; damaged++) {
      this.attackNothing(owner);
    }
  }

  public void attack(PlayerEntity owner, LivingEntity ent) {
    ent.damage(DamageSource.player(owner), Mod.CONFIG.spirit_flame.damage);
    new SpiritFlamePacket(owner, ent).sendToAround(ent, 32);
  }

  public void attackNothing(PlayerEntity owner) {
    new SpiritFlamePacket(owner, owner.getRotationVec(1)
      .rotateY(Util.rand(-ANGLE, ANGLE))
      .rotateX(Util.rand(-ANGLE, ANGLE))
      .multiply(Util.rand(0, 0.5) * Mod.CONFIG.spirit_flame.radius)
    ).sendToAround(owner, 32);
  }
}
