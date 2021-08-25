package su226.orimod.items;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Quaternion;
import net.minecraft.world.World;
import su226.orimod.Mod;
import su226.orimod.blocks.SpiritSmithingTable;
import su226.orimod.components.Components;
import su226.orimod.components.IChargeable;
import su226.orimod.entities.Arrow;
import su226.orimod.others.ICustomRender;
import su226.orimod.others.Render;
import su226.orimod.others.Sounds;

public class SpiritArc extends BowItem implements ICustomRender {
  private static final int MAX_TIME = 72000;

  private static final String MODEL = "item/3d/spirit_arc";
  private static final String MODEL_OVERLAY = "item/3d/spirit_arc_overlay";

  public SpiritArc() {
    super(new Settings().group(Items.GROUP).maxDamage(0));
    SpiritSmithingTable.registerRecipe(new SpiritSmithingTable.Recipe(
      new ItemStack(net.minecraft.item.Items.BOW),
      new ItemStack(this),
      300
    ));
  }

  public void registerModelPredicate() {
    FabricModelPredicateProviderRegistry.register(this, new Identifier("charge"), (stack, world, owner) -> Components.CHARGEABLE.get(stack).getCharge());
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    ItemStack stack = user.getStackInHand(hand);
    if (world.isClient) {
      IChargeable component = Components.CHARGEABLE.get(stack);
      component.beginCharge(Mod.CONFIG.spirit_arc.charge_duration);
    } else {
      world.playSound(null, user.getX(), user.getY(), user.getZ(), Sounds.SPIRIT_ARC_DRAW, SoundCategory.PLAYERS, 1, 1);
    }
    user.setCurrentHand(hand);
    return TypedActionResult.success(stack);
  }

  @Override
  public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
    if (world.isClient) {
      Components.CHARGEABLE.get(stack).endCharge();
    }
    this.shoot(stack, world, user, remainingUseTicks);
  }

  @Override
  public int getEnchantability() {
    return Mod.CONFIG.tools.enchantability;
  }

  public float getVelocity(int tick) {
    float f = 1f * tick / Mod.CONFIG.spirit_arc.charge_duration;
    return Math.min((f * f + f * 2) / 3 * Mod.CONFIG.spirit_arc.velocity_multiplier, Mod.CONFIG.spirit_arc.velocity_multiplier);
  }

  public void shoot(ItemStack stack, World world, LivingEntity owner, int timeLeft) {
    if (!(owner instanceof PlayerEntity player)) {
      return;
    }
    int i = this.getMaxUseTime(stack) - timeLeft;
    float f = getVelocity(i);
    if (f < 0.1f * Mod.CONFIG.spirit_arc.velocity_multiplier) {
      return;
    }
    if (!world.isClient) {
      Arrow arrow = new Arrow(world, player);
      arrow.setProperties(player, player.pitch, player.yaw, 0.0F, f, 1.0F);
      if (f == 1.0F) {
        arrow.setCritical(true);
      }
      int power = EnchantmentHelper.getLevel(Enchantments.POWER, stack);
      if (power > 0) {
        arrow.setDamage(arrow.getDamage() + power * 0.5 + 0.5);
      }
      int punch = EnchantmentHelper.getLevel(Enchantments.PUNCH, stack);
      if (punch > 0) {
        arrow.setPunch(Mod.CONFIG.spirit_arc.knockback + punch);
      }
      if (EnchantmentHelper.getLevel(Enchantments.FLAME, stack) > 0) {
        arrow.setOnFireFor(100);
      }
      world.spawnEntity(arrow);
      world.playSound(null, player.getX(), player.getY(), player.getZ(), Sounds.SPIRIT_ARC_SHOOT, SoundCategory.PLAYERS, 1, 1);
    }
    player.incrementStat(Stats.USED.getOrCreateStat(this));
  }

  @Override
  public void render(ItemStack stack, ModelTransformation.Mode mode, boolean left, MatrixStack mat, VertexConsumerProvider consumers, int light, int overlay, BakedModel model) {
    boolean shouldRotate = mode == ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND || mode == ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND || mode == ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND || mode == ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND;
    if (shouldRotate) {
      mat.multiply(new Quaternion(0, 90, 0, true));
      mat.translate(-1, 0, 0);
    }
    float charge = Components.CHARGEABLE.get(stack).getCharge();
    float x = 0.45f * (1 - charge);
    mat.push();
    mat.scale(1 + 0.5f * charge, 1, 1);
    VertexConsumer consumer = consumers.getBuffer(RenderLayer.getTranslucent());
    Render.model(mat, consumer, MODEL, 0xffffffff, 0, Render.FULL_BRIGHT);
    Render.model(mat, consumer, MODEL_OVERLAY, Mod.CONFIG.glow_color, 0, Render.FULL_BRIGHT);
    if (charge == 0) {
      Render.glowingLine(mat, consumer, 0.45f, 0.9f, 0.5f, 0.45f, 0.1f, 0.5f, Mod.CONFIG.glow_color);
    } else {
      Render.glowingLine(mat, consumer, 0.45f, 0.9f, 0.5f, x, 0.5f, 0.5f, Mod.CONFIG.glow_color);
      Render.glowingLine(mat, consumer, 0.45f, 0.1f, 0.5f, x, 0.5f, 0.5f, Mod.CONFIG.glow_color);
    }
    mat.pop();
    if (charge != 0) {
      mat.translate(x + 0.5, 0.5, 0.5);
      Render.model(mat, consumer, Arrow.Render.MODEL, 0xffffffff, 0, Render.FULL_BRIGHT);
      Render.model(mat, consumer, Arrow.Render.MODEL_OVERLAY, Mod.CONFIG.glow_color, 0, Render.FULL_BRIGHT);
    }
    GlStateManager.disableDepthTest();
    GlStateManager.enableTexture();
    GlStateManager.disableBlend();
  }
}
