package su226.orimod.items;

import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import su226.orimod.Config;
import su226.orimod.blocks.SpiritSmithingTable;
import su226.orimod.capabilities.Capabilities;
import su226.orimod.capabilities.IChargeable;
import su226.orimod.entities.LightBurstEntity;
import su226.orimod.messages.SoundMessage;
import su226.orimod.others.Models;
import su226.orimod.others.Sounds;
import su226.orimod.others.Util;

public class LightBurst extends Item {
  private static class Render extends TileEntityItemStackRenderer {
    @Override
    public void renderByItem(ItemStack stack, float unused) {
      GlStateManager.pushMatrix();
      GlStateManager.enableCull();
      GlStateManager.disableLighting();
      IChargeable cap = stack.getCapability(Capabilities.CHARGEABLE, null);
      double charge = cap == null ? 0 : cap.getCharge();
      float lightness = (float)Math.pow(charge, 3) * 176 + 64;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightness, lightness);
      Models.renderItemModel(MODEL);
      double scale = 1.1 + 0.1 * charge;
      double translate = (1 - scale) / 2;
      GlStateManager.translate(translate, translate, translate);
      GlStateManager.scale(scale, scale, scale);
      Models.renderItemModel(SpiritFlame.MODEL_OVERLAY, GLOW_COLOR);
      GlStateManager.enableLighting();
      GlStateManager.popMatrix();
    }
  }

  public static final int GLOW_COLOR = 0x88ff8866;
  private static final int MAX_TIME = 72000;

  public static List<BakedQuad> MODEL;

  public LightBurst() {
    super();
    this.setRegistryName(Util.getLocation("light_burst"));
    this.setUnlocalizedName(Util.getI18nKey("light_burst"));
    this.setCreativeTab(Items.CREATIVE_TAB);
    this.setMaxStackSize(1);
    SpiritSmithingTable.registerRecipe(new SpiritSmithingTable.Recipe(
      new ItemStack(net.minecraft.init.Items.FIRE_CHARGE),
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
      Models.setItemModel(this, "light_burst");
    }
  }

  @SideOnly(Side.CLIENT)
  public void loadModel() {
    if (Config.ENABLE_3D) {
      MODEL = Models.loadItemModel("item/3d/light_burst.obj");
    }
  }

  @Override
  public int getMaxItemUseDuration(ItemStack stack) {
    return MAX_TIME;
  }

  public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer owner, EnumHand hand) {
    ItemStack stack = owner.getHeldItem(hand);
    if (world.isRemote) {
      IChargeable cap = stack.getCapability(Capabilities.CHARGEABLE, null);
      cap.setDuration(Config.LIGHT_BURST.CHARGE_DURATION);
      cap.beginCharge();
    } else {
      SoundMessage.play(owner, Sounds.LIGHT_BURST_START);
    }
    owner.setActiveHand(hand);
    return new ActionResult<>(EnumActionResult.SUCCESS, stack);
  }

  @Override
  public void onUsingTick(ItemStack stack, EntityLivingBase owner, int timeLeft) {
    if (owner.world.isRemote && stack.getCapability(Capabilities.CHARGEABLE, null).particle()) {
      Vec3d velocity = new Vec3d(1, 0, 0).rotatePitch(Util.randAngle(0.5f)).rotateYaw(Util.randAngle(2f)).scale(0.1);
      owner.world.spawnParticle(EnumParticleTypes.FLAME, owner.posX, owner.posY + owner.height / 2, owner.posZ, velocity.x, velocity.y, velocity.z);
    }
  }

  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase owner, int timeLeft) {
    int duration = MAX_TIME - timeLeft;
    if (world.isRemote) {
      stack.getCapability(Capabilities.CHARGEABLE, null).endCharge();
    } else if (duration > Config.LIGHT_BURST.CHARGE_DURATION) {
      SoundMessage.play(owner, Sounds.LIGHT_BURST_THROW);
      LightBurstEntity ent = new LightBurstEntity(world, owner);
      ent.shoot(owner, owner.rotationPitch, owner.rotationYaw, 0.0F, 1.5F, 1.0F);
      world.spawnEntity(ent);
      if (owner instanceof EntityPlayer) {
        ((EntityPlayer)owner).addStat(StatList.getObjectUseStats(this));
      }
    }
  }
}
