package su226.orimod.items;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import su226.orimod.Mod;
import su226.orimod.blocks.SpiritSmithingTable;
import su226.orimod.components.Components;
import su226.orimod.components.IChargeable;
import su226.orimod.entities.LightBurstEntity;
import su226.orimod.others.ICustomRender;
import su226.orimod.others.Render;
import su226.orimod.others.Sounds;
import su226.orimod.others.Util;

public class LightBurst extends Item implements ICustomRender {
  public static final int GLOW_COLOR = 0x88ff8866;
  private static final int MAX_TIME = 72000;

  public static final String MODEL = "item/3d/light_burst";

  public LightBurst() {
    super(new Settings().group(Items.GROUP).maxCount(1));
    SpiritSmithingTable.registerRecipe(new SpiritSmithingTable.Recipe(
      new ItemStack(net.minecraft.item.Items.FIRE_CHARGE),
      new ItemStack(this),
      300
    ));
  }

  @Override
  public int getMaxUseTime(ItemStack stack) {
    return MAX_TIME;
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    ItemStack stack = user.getStackInHand(hand);
    if (world.isClient) {
      Components.CHARGEABLE.get(stack).beginCharge(Mod.CONFIG.light_burst.charge_duration);
    } else {
      user.world.playSound(null, user.getX(), user.getY(), user.getZ(), Sounds.LIGHT_BURST_START, SoundCategory.PLAYERS, 1, 1);
    }
    user.setCurrentHand(hand);
    return TypedActionResult.success(stack);
  }

  @Override
  public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
    if (world.isClient && Components.CHARGEABLE.get(stack).activeTimer("particle", 2, 5)) {
      Vec3d velocity = new Vec3d(1, 0, 0).rotateX(Util.randAngle(0.5f)).rotateY(Util.randAngle(2f)).multiply(0.1);
      world.addParticle(ParticleTypes.FLAME, user.getX(), user.getY() + user.getHeight() / 2, user.getZ(), velocity.x, velocity.y, velocity.z);
    }
  }

  @Override
  public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
    int duration = MAX_TIME - remainingUseTicks;
    if (world.isClient) {
      Components.CHARGEABLE.get(stack).endCharge();
    } else if (duration > Mod.CONFIG.light_burst.charge_duration) {
      user.world.playSound(null, user.getX(), user.getY(), user.getZ(), Sounds.LIGHT_BURST_THROW, SoundCategory.PLAYERS, 1, 1);
      LightBurstEntity ent = new LightBurstEntity(user);
      ent.setProperties(user, user.pitch, user.yaw, 0.0F, 1.5F, 1.0F);
      world.spawnEntity(ent);
      if (user instanceof PlayerEntity) {
        ((PlayerEntity)user).incrementStat(Stats.USED.getOrCreateStat(this));
      }
    }
  }

  @Override
  public void render(ItemStack stack, ModelTransformation.Mode mode, boolean left, MatrixStack mat, VertexConsumerProvider consumers, int light, int overlay, BakedModel model) {
    VertexConsumer consumer = consumers.getBuffer(RenderLayer.getTranslucent());
    IChargeable component = Components.CHARGEABLE.get(stack);
    float charge = component.getCharge();
    int lightness = (int)(Math.pow(charge, 3) * 176 + 64);
    Render.model(mat, consumer, MODEL, 0xffffffff, 0, lightness);
    float scale = 1.1f + 0.1f * charge;
    float translate = (1 - scale) / 2;
    mat.translate(translate, translate, translate);
    mat.scale(scale, scale, scale);
    Render.model(mat, consumer, SpiritFlame.MODEL_OVERLAY, GLOW_COLOR, 0, lightness);
  }
}
