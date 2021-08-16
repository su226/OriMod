package su226.orimod.items;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.BiPredicate;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import su226.orimod.Config;
import su226.orimod.Mod;
import su226.orimod.blocks.SpiritSmithingTable;
import su226.orimod.capabilities.Capabilities;
import su226.orimod.capabilities.IEquipper;
import su226.orimod.capabilities.IKurosFeather;
import su226.orimod.capabilities.IEquipper.IEquipable;
import su226.orimod.messages.FlapMessage;
import su226.orimod.messages.SoundMessage;
import su226.orimod.others.Models;
import su226.orimod.others.Sounds;
import su226.orimod.others.Util;

public class KurosFeather extends Item implements IEquipable {
  static class Layer implements LayerRenderer<EntityLivingBase> {
    public LayerRenderer<EntityLivingBase> prev;
  
    public Layer(LayerRenderer<EntityLivingBase> prev) {
      this.prev = prev;
    }
    
    @Override
    public void doRenderLayer(EntityLivingBase owner, float swing, float swingAmount, float partialTicks, float age, float yaw, float pitch, float scale) {
      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      GlStateManager.rotate(180, 0, 0, 1);
      GlStateManager.translate(0, 0.25, 0);
      Models.renderItemModel(MODEL);
      GlStateManager.enableCull();
      GlStateManager.popMatrix();
    }

    @Override
    public boolean shouldCombineTextures() {
      return false;
    }
  }

  private static final float ANGLE = (float)Math.PI * 0.9f;
  private static List<BakedQuad> MODEL;

  public KurosFeather() {
    super();
    this.setRegistryName(Util.getLocation("kuros_feather"));
    this.setUnlocalizedName(Util.getI18nKey("kuros_feather"));
    this.setCreativeTab(Items.CREATIVE_TAB);
    this.setMaxStackSize(1);
    SpiritSmithingTable.registerRecipe(new SpiritSmithingTable.Recipe(
      new ItemStack(net.minecraft.init.Items.FEATHER),
      new ItemStack(this),
      300
    ));
  }

  public void setRotationAngles(ModelPlayer model, EntityPlayer owner) {
    if (owner.getCapability(Capabilities.KUROS_FEATHER, null).isPrevGliding() && !model.bipedBody.isHidden) {
      model.bipedBody.rotateAngleX = 0.0F;
      model.bipedRightLeg.rotationPointZ = 0.1F;
      model.bipedLeftLeg.rotationPointZ = 0.1F;
      model.bipedRightLeg.rotationPointY = 12.0F;
      model.bipedLeftLeg.rotationPointY = 12.0F;
      model.bipedHead.rotationPointY = 0.0F;
      model.bipedRightLeg.rotateAngleX = 0;
      model.bipedLeftLeg.rotateAngleX = 0;
      model.bipedLeftArmwear.rotateAngleX = model.bipedLeftArm.rotateAngleX = 0;
      model.bipedLeftArmwear.rotateAngleY = model.bipedLeftArm.rotateAngleY = 0;
      model.bipedLeftArmwear.rotateAngleZ = model.bipedLeftArm.rotateAngleZ = -ANGLE;
      model.bipedRightArmwear.rotateAngleX = model.bipedRightArm.rotateAngleX = 0;
      model.bipedRightArmwear.rotateAngleY = model.bipedRightArm.rotateAngleY = 0;
      model.bipedRightArmwear.rotateAngleZ = model.bipedRightArm.rotateAngleZ = ANGLE;
      model.isSneak = false;
    }
  }

  public void livingAttack(LivingAttackEvent event) {
    IEquipper cap = event.getEntity().getCapability(Capabilities.EQUIPPER, null);
    if (cap != null && cap.isEquipped(this) && event.getSource().damageType.equals("fall")) {
      event.setCanceled(true);
    } 
  }

  @Override
  public void onUpdate(ItemStack stack, World world, Entity owner, int slot, boolean held) {
    this.updateEquipable(stack, owner);
  }

  @SuppressWarnings("unchecked")
  public void renderPlayerPre(RenderPlayerEvent.Pre event) {
    EntityPlayer owner = event.getEntityPlayer();
    RenderPlayer render = event.getRenderer();
    IKurosFeather cap = owner.getCapability(Capabilities.KUROS_FEATHER, null);
    if (cap.shouldUpdate()) {
      boolean gliding = cap.isPrevGliding();
      try {
        Field field = ObfuscationReflectionHelper.findField(RenderLivingBase.class, "field_177097_h"); // layerRenderers
        field.setAccessible(true);
        List<LayerRenderer<EntityLivingBase>> layerRenderers = (List<LayerRenderer<EntityLivingBase>>)field.get(render);
        for (int i = 0; i < layerRenderers.size(); i++) {
          LayerRenderer<EntityLivingBase> layer = layerRenderers.get(i);
          if (gliding && layer instanceof LayerHeldItem) {
            layerRenderers.set(i, new Layer(layer));
          } else if (!gliding && layer instanceof Layer) {
            layerRenderers.set(i, ((Layer)layer).prev);
          }
        }
      } catch (Exception e) {
        Mod.LOG.warn("Failed to override player render!", e);
      }
      cap.setShouldUpdate(false);
    }
  }

  @Override
  public BiPredicate<ItemStack, EntityPlayer> getEquipableSlots() {
    return IEquipable.ANY_HAND;
  }

  @Override
  public void onEquipableUpdate(EntityPlayer owner, boolean isMaxPriority) {
    boolean gliding = this.canGlide(owner);
    IKurosFeather cap = owner.getCapability(Capabilities.KUROS_FEATHER, null);
    if (cap.isPrevGliding() != gliding) {
      if (!owner.world.isRemote) {
        SoundMessage.play(owner, gliding ? Sounds.GLIDE_START : Sounds.GLIDE_END);
      }
      cap.setPrevGliding(gliding);
      cap.setShouldUpdate(true);
    }
    if (gliding) {
      owner.moveRelative(0, 0, owner.moveForward, (float)Config.KUROS_FEATHER.SPEED_COMPENSATION);
      if (owner.motionY < 0.0) {
        owner.motionY *= Config.KUROS_FEATHER.FALL_MULTIPLIER;
      }
    }
  }

  @Override
  public void onEquipableUnequip(EntityPlayer owner, boolean isMaxPriority) {
    IKurosFeather cap = owner.getCapability(Capabilities.KUROS_FEATHER, null);
    cap.setShouldUpdate(true);
    cap.setPrevGliding(false);
  }

  public boolean canGlide(EntityPlayer owner) {
    return owner.isSneaking() && Util.isAirBorne(owner) && !owner.isElytraFlying() && !owner.capabilities.isFlying;
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer owner, EnumHand hand) {
    CooldownTracker cd = owner.getCooldownTracker();
    if (cd.getCooldown(this, 0) > 0) {
      return new ActionResult<>(EnumActionResult.FAIL, owner.getHeldItem(hand));
    }
    cd.setCooldown(this, Config.KUROS_FEATHER.COOLDOWN);
    if (!world.isRemote) {
      Vec3d start = owner.getPositionEyes(1);
      Vec3d end = start.add(owner.getLookVec().scale(Config.KUROS_FEATHER.LENGTH));
      double dist = end.distanceTo(start);
      Vec3d velocity = end.subtract(start).scale(Config.KUROS_FEATHER.FORCE);
      List<Entity> ents = Util.entityAroundLine(world, start, end, Config.KUROS_FEATHER.RANGE, owner);
      for (Entity ent : ents) {
        double ratio = 1 - Math.min(ent.getDistance(start.x, start.y, start.z) / dist, 1);
        ent.addVelocity(velocity.x * ratio, velocity.y * ratio, velocity.z * ratio);
        ent.velocityChanged = true;
      }
      Mod.NETWORK.sendToAllAround(new FlapMessage(owner, end), Util.getTargetPoint(owner, 32));
    }
    return new ActionResult<>(EnumActionResult.SUCCESS, owner.getHeldItem(hand));
  }

  @SideOnly(Side.CLIENT)
  public void setModel() {
    Models.setItemModel(this, "kuros_feather");
  }

  @SideOnly(Side.CLIENT)
  public void loadModel() {
    MODEL = Models.loadItemModel("entity/kuros_feather.obj");
  }

  public void setTexture(TextureMap map) {
    map.registerSprite(Util.getLocation("entity/kuros_feather"));
  }
}
