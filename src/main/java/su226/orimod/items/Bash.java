package su226.orimod.items;

import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import su226.orimod.Config;
import su226.orimod.blocks.SpiritSmithingTable;
import su226.orimod.capabilities.Capabilities;
import su226.orimod.messages.SoundMessage;
import su226.orimod.others.Models;
import su226.orimod.others.Sounds;
import su226.orimod.others.Util;

public class Bash extends Item {
  private static class Render extends TileEntityItemStackRenderer {
    @Override
    public void renderByItem(ItemStack stack, float unused) {
      GlStateManager.disableLighting();
      GlStateManager.enableCull();
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
      Models.renderItemModel(MODEL);
      Models.renderItemModel(MODEL_OVERLAY, Config.GLOW_COLOR);
      GlStateManager.enableLighting();
    }
  }

  private static List<BakedQuad> MODEL;
  private static List<BakedQuad> MODEL_OVERLAY;

  public Bash() {
    super();
    this.setRegistryName(Util.getLocation("bash"));
    this.setUnlocalizedName(Util.getI18nKey("bash"));
    this.setCreativeTab(Items.CREATIVE_TAB);
    this.setMaxStackSize(1);
    SpiritSmithingTable.registerRecipe(new SpiritSmithingTable.Recipe(
      new ItemStack(net.minecraft.init.Blocks.PISTON),
      new ItemStack(this),
      300
    ));
  }

  @Override
  public int getMaxItemUseDuration(ItemStack stack) {
    return Config.BASH.TIMEOUT;
  }

  @Override
  public EnumAction getItemUseAction(ItemStack stack) {
    return EnumAction.BOW;
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer owner, EnumHand hand) {
    ItemStack stack = owner.getHeldItem(hand);
    if (!world.isRemote) {
      Entity ent = Util.rayTraceEntity(owner, Config.BASH.RANGE);
      if (ent != null) {
        beginBash(owner);
        beginBash(ent);
        stack.getCapability(Capabilities.HAS_ENTITY, null).setEntity(ent);
        if (ent instanceof EntityLiving) {
          ((EntityLiving)ent).setNoAI(true);
        }
        owner.setActiveHand(hand);
        SoundMessage.play(owner, Sounds.BASH_START);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
      } else {
        SoundMessage.play(owner, Sounds.BASH_NO_TARGET);
      }
    }
    return new ActionResult<>(EnumActionResult.FAIL, stack);
  }

  @Override
  public void onUsingTick(ItemStack stack, EntityLivingBase owner, int left) {
    if (!owner.world.isRemote && left == 25) {
      SoundMessage.play(owner, Sounds.BASH_TIMEOUT);
    }
  }

  @Override
  public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase owner) {
    this.finishBash(stack, owner);
    return stack;
  }

  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase owner, int timeLeft) {
    this.finishBash(stack, owner);
  }

  private void finishBash(ItemStack stack, Entity owner) {
    if (!owner.world.isRemote) {
      Entity ent = stack.getCapability(Capabilities.HAS_ENTITY, null).getEntity();
      if (ent instanceof EntityLiving) {
        ((EntityLiving)ent).setNoAI(false);
      }
      Vec3d vec = owner.getLookVec().scale(Config.BASH.MULTIPLIER);
      this.endBash(owner, vec.x, vec.y, vec.z);
      this.endBash(ent, -vec.x, -vec.y, -vec.z);
      SoundMessage.play(owner, Sounds.BASH_END);
    }
  }

  private void beginBash(Entity ent) {
    ent.setNoGravity(true);
    setVelocity(ent, 0, 0, 0);
  }

  private void endBash(Entity ent, double x, double y, double z) {
    ent.setNoGravity(false);
    setVelocity(ent, x, y, z);
  }

  private void setVelocity(Entity ent, double x, double y, double z) {
    ent.motionX = x;
    ent.motionY = y;
    ent.motionZ = z;
    ent.velocityChanged = true;
  }

  @SideOnly(Side.CLIENT)
  public void setModel() {
    if (Config.ENABLE_3D) {
      Models.setItemModel(this, "placeholder");
      this.setTileEntityItemStackRenderer(new Render());
    } else {
      Models.setItemModel(this, "bash");
    }
  }

  @SideOnly(Side.CLIENT)
  public void loadModel() {
    if (Config.ENABLE_3D) {
      MODEL = Models.loadItemModel("item/3d/bash.obj");
      MODEL_OVERLAY = Models.loadItemModel("item/3d/bash_overlay.obj");
    }
  }
}
