package su226.orimod.items;

import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import su226.orimod.Config;
import su226.orimod.blocks.SpiritSmithingTable;
import su226.orimod.capabilities.Capabilities;
import su226.orimod.capabilities.IChargeable;
import su226.orimod.entities.Arrow;
import su226.orimod.messages.SoundMessage;
import su226.orimod.others.Models;
import su226.orimod.others.Sounds;
import su226.orimod.others.Util;

public class SpiritArc extends ItemBow {
  private static class Render extends TileEntityItemStackRenderer {
    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
      boolean shouldRotate = Models.transform == TransformType.THIRD_PERSON_LEFT_HAND || Models.transform == TransformType.THIRD_PERSON_RIGHT_HAND || Models.transform == TransformType.FIRST_PERSON_LEFT_HAND || Models.transform == TransformType.FIRST_PERSON_RIGHT_HAND;
      if (shouldRotate) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(90, 0, 1, 0);
        GlStateManager.translate(-1, 0, 0);
      }
      GlStateManager.disableLighting();
      GlStateManager.enableCull();
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
      IChargeable cap = stack.getCapability(Capabilities.CHARGEABLE, null);
      double charge = cap == null ? 0 : cap.getCharge();
      double x = 0.45 * (1 - charge);
      GlStateManager.pushMatrix();
      GlStateManager.scale(1 + 0.5 * charge, 1, 1);
      Models.renderItemModel(MODEL);
      if (charge == 0) {
        Models.drawGlowingLine(0.45, 0.9, 0.5, 0.45, 0.1, 0.5, Config.GLOW_COLOR);
      } else {
        Models.drawGlowingLine(0.45, 0.9, 0.5, x, 0.5, 0.5, Config.GLOW_COLOR);
        Models.drawGlowingLine(0.45, 0.1, 0.5, x, 0.5, 0.5, Config.GLOW_COLOR);
      }
      Models.renderItemModel(MODEL_OVERLAY, Config.GLOW_COLOR);
      GlStateManager.popMatrix();
      if (charge != 0) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, 0.5, 0.5);
        Models.renderItemModel(Arrow.Render.MODEL, 0xffffffff);
        Models.renderItemModel(Arrow.Render.MODEL_OVERLAY, Config.GLOW_COLOR);
        GlStateManager.popMatrix();
      }
      GlStateManager.enableLighting();
      if (shouldRotate) {
        GlStateManager.popMatrix();
      }
    }
  }

  private static final int MAX_TIME = 72000;

  private static List<BakedQuad> MODEL;
  private static List<BakedQuad> MODEL_OVERLAY;

  public SpiritArc() {
    super();
    this.setRegistryName(Util.getLocation("spirit_arc"));
    this.setUnlocalizedName(Util.getI18nKey("spirit_arc"));
    this.setCreativeTab(Items.CREATIVE_TAB);
    this.setMaxStackSize(1);
    this.addPropertyOverride(new ResourceLocation("charge"), new IItemPropertyGetter() {
      @Override
      public float apply(ItemStack stack, World world, EntityLivingBase owner) {
        IChargeable cap = stack.getCapability(Capabilities.CHARGEABLE, null);
        return cap == null ? 0 : cap.getCharge();
      }
    });
    SpiritSmithingTable.registerRecipe(new SpiritSmithingTable.Recipe(
      new ItemStack(net.minecraft.init.Items.BOW),
      new ItemStack(this),
      300
    ));
  }

  @SideOnly(Side.CLIENT)
  public void setModel() {
    if (Config.ENABLE_3D) {
      Models.setItemModel(this, "placeholder");
      this.setTileEntityItemStackRenderer(new Render());
    } else {
      Models.setItemModel(this, "spirit_arc");
    }
  }

  @Override
  public EnumAction getItemUseAction(ItemStack stack) {
    return EnumAction.BOW;
  }

  @SideOnly(Side.CLIENT)
  public void loadModel() {
    MODEL = Models.loadItemModel("item/3d/spirit_arc.obj");
    MODEL_OVERLAY = Models.loadItemModel("item/3d/spirit_arc_overlay.obj");
  }

  @Override
  public int getMaxItemUseDuration(ItemStack stack) {
    return MAX_TIME;
  }

  public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer owner, EnumHand hand) {
    ItemStack stack = owner.getHeldItem(hand);
    if (world.isRemote) {
      IChargeable cap = stack.getCapability(Capabilities.CHARGEABLE, null);
      cap.setDuration(Config.SPIRIT_ARC.CHARGE_DURATION);
      cap.beginCharge();
    } else {
      SoundMessage.play(owner, Sounds.SPIRIT_ARC_DRAW);
    }
    owner.setActiveHand(hand);
    return new ActionResult<>(EnumActionResult.SUCCESS, stack);
  }

  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase owner, int timeLeft) {
    if (world.isRemote) {
      stack.getCapability(Capabilities.CHARGEABLE, null).endCharge();
    }
    this.shoot(stack, world, owner, timeLeft);
  }

  @Override
  public int getMaxDamage(ItemStack stack) {
    return 0;
  }

  @Override
  public int getItemEnchantability() {
    return Config.TOOLS.ENCHANTABILITY;
  }

  public float getVelocity(int tick) {
    float f = tick / Config.SPIRIT_ARC.CHARGE_DURATION;
    return Math.min(f * f, 1);
  }

  public void shoot(ItemStack stack, World world, EntityLivingBase owner, int timeLeft) {
    if (!(owner instanceof EntityPlayer)) {
      return;
    }
    EntityPlayer player = (EntityPlayer)owner;
    int ticks = this.getMaxItemUseDuration(stack) - timeLeft;
    ticks = ForgeEventFactory.onArrowLoose(stack, world, player, ticks, true);
    if (ticks < 0) {
      return;
    }
    float f = getVelocity(ticks);
    if (f < 0.1f) {
      return;
    }
    player.addStat(StatList.getObjectUseStats(this));
    if (world.isRemote) {
      return;
    }
    SoundMessage.play(player, Sounds.SPIRIT_ARC_SHOOT);
    Arrow arrow = new Arrow(world, owner);
    if (f == 1.0F) {
      arrow.setIsCritical(true);
    }
    arrow.shoot(player, player.rotationPitch, player.rotationYaw, 0, f * (float)Config.SPIRIT_ARC.VELOCITY_MULTIPLIER, 1);
    int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
    if (power > 0) {
      arrow.damage += power * 0.5 + 0.5;
    }
    int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
    if (punch > 0) {
      arrow.knockbackStrength += punch;
    }
    if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) {
      arrow.setFire(100);
    }
    world.spawnEntity(arrow);
  }
}
