package su226.orimod.items;

import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import su226.orimod.Config;
import su226.orimod.Mod;
import su226.orimod.blocks.SpiritSmithingTable;
import su226.orimod.capabilities.Capabilities;
import su226.orimod.capabilities.IChargeable;
import su226.orimod.messages.SpiritFlameMessage;
import su226.orimod.messages.ChargeFlameMessage;
import su226.orimod.others.Models;
import su226.orimod.others.Util;

public class SpiritFlame extends Item {
  private static class ChargeFlame extends Explosion {
    private Entity owner;
    private int dimension;
  
    public ChargeFlame(Entity owner, Vec3d pos) {
      super(owner.world, owner, pos.x, pos.y, pos.z, (float)Config.SPIRIT_FLAME.EXPLOSION_FORCE, false, false);
      this.owner = owner;
      this.dimension = owner.dimension;
    }

    public void explode() {
      this.doExplosionA();
      Mod.NETWORK.sendToDimension(new ChargeFlameMessage(owner, this.getPosition()), this.dimension);
    }
  }

  private static class Render extends TileEntityItemStackRenderer {
    @Override
    public void renderByItem(ItemStack stack, float unused) {
      GlStateManager.pushMatrix();
      GlStateManager.enableCull();
      GlStateManager.disableLighting();
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
      Models.renderItemModel(MODEL);
      IChargeable cap = stack.getCapability(Capabilities.CHARGEABLE, null);
      double scale = 1.1 + 0.4 * (cap == null ? 0 : cap.getCharge());
      double translate = (1 - scale) / 2;
      GlStateManager.translate(translate, translate, translate);
      GlStateManager.scale(scale, scale, scale);
      Models.renderItemModel(MODEL_OVERLAY, Config.GLOW_COLOR);
      GlStateManager.enableLighting();
      GlStateManager.popMatrix();
    }
  }

  private static final float ANGLE = (float)Math.PI * 0.2f;
  private static final int MAX_TIME = 72000;

  public static List<BakedQuad> MODEL;
  public static List<BakedQuad> MODEL_OVERLAY;

  public SpiritFlame() {
    super();
    this.setRegistryName(Util.getLocation("spirit_flame"));
    this.setUnlocalizedName(Util.getI18nKey("spirit_flame"));
    this.setCreativeTab(Items.CREATIVE_TAB);
    this.setMaxStackSize(1);
    SpiritSmithingTable.registerRecipe(new SpiritSmithingTable.Recipe(
      new ItemStack(net.minecraft.init.Items.BLAZE_POWDER),
      new ItemStack(this),
      300
    ));
  }

  @Override
  public boolean showDurabilityBar(ItemStack stack) {
    IChargeable cap = stack.getCapability(Capabilities.CHARGEABLE, null);
    return cap == null ? false : cap.getCharge() != 0;
  }

  @Override
  public double getDurabilityForDisplay(ItemStack stack) {
    IChargeable cap = stack.getCapability(Capabilities.CHARGEABLE, null);
    return cap == null ? 1 : 1 - cap.getCharge();
  }
  
  @Override
  public int getRGBDurabilityForDisplay(ItemStack stack) {
    IChargeable cap = stack.getCapability(Capabilities.CHARGEABLE, null);
    if (cap != null && cap.getCharge() == 1 && cap.blink()) {
      return 0xffffff;
    }
    return super.getRGBDurabilityForDisplay(stack);
  }

  @SideOnly(Side.CLIENT)
  public void setModel() {
    if (Config.ENABLE_3D) {
      Models.setItemModel(this, "placeholder");
      this.setTileEntityItemStackRenderer(new Render());
    } else {
      Models.setItemModel(this, "spirit_flame");
    }
  }

  @SideOnly(Side.CLIENT)
  public void loadModel() {
    MODEL = Models.loadItemModel("item/3d/spirit_flame.obj");
    MODEL_OVERLAY = Models.loadItemModel("item/3d/spirit_flame_overlay.obj");
  }

  @Override
  public int getMaxItemUseDuration(ItemStack stack) {
    return MAX_TIME;
  }

  public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer owner, EnumHand hand) {
    ItemStack stack = owner.getHeldItem(hand);
    if (world.isRemote) {
      IChargeable cap = stack.getCapability(Capabilities.CHARGEABLE, null);
      cap.setDuration(Config.SPIRIT_FLAME.CHARGE_DURATION);
      cap.beginCharge();
    } else {
      doSpiritFlame(owner);
    }
    owner.setActiveHand(hand);
    return new ActionResult<>(EnumActionResult.SUCCESS, stack);
  }

  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase owner, int timeLeft) {
    int duration = MAX_TIME - timeLeft;
    if (world.isRemote) {
      stack.getCapability(Capabilities.CHARGEABLE, null).endCharge();
    } else if (duration > Config.SPIRIT_FLAME.CHARGE_DURATION) {
      new ChargeFlame(owner, owner.getPositionEyes(1)).explode();
    }
  }

  private void doSpiritFlame(EntityPlayer owner) {
    Vec3d center = owner.getPositionEyes(1);
    Vec3d offset = new Vec3d(Config.SPIRIT_FLAME.RADIUS, Config.SPIRIT_FLAME.RADIUS, Config.SPIRIT_FLAME.RADIUS);
    Vec3d start = center.add(offset);
    Vec3d end = center.subtract(offset);
    List<EntityLivingBase> ents = owner.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(start.x, start.y, start.z, end.x, end.y, end.z));
    ents.sort((a, b) -> (int)Math.signum(a.getDistance(owner) - b.getDistance(owner)));
    int damaged = 0;
    for (int i = 0; i < ents.size() && damaged < Config.SPIRIT_FLAME.TARGETS; i++) {
      EntityLivingBase ent = ents.get(i);
      if (ent.isDead || ent == owner || (ent instanceof EntityTameable && ((EntityTameable)ent).getOwner() == owner && Config.SPIRIT_FLAME.IGNORE_PETS)) {
        continue;
      }
      this.attack(owner, ent);
      damaged++;
    }
    for (; damaged < Config.SPIRIT_FLAME.TARGETS; damaged++) {
      this.attackNothing(owner);
    }
  }

  public void attack(EntityPlayer owner, EntityLivingBase ent) {
    ent.attackEntityFrom(DamageSource.causePlayerDamage(owner), (float)Config.SPIRIT_FLAME.DAMAGE);
    Mod.NETWORK.sendToDimension(new SpiritFlameMessage(owner, ent), owner.dimension);
  }

  public void attackNothing(EntityPlayer owner) {
    Mod.NETWORK.sendToDimension(new SpiritFlameMessage(owner, owner.getLookVec()
      .rotateYaw(Util.rand(-ANGLE, ANGLE))
      .rotatePitch(Util.rand(-ANGLE, ANGLE))
      .scale(Util.rand(0, 0.5) * Config.SPIRIT_FLAME.RADIUS)
    ), owner.dimension);
  }
}
