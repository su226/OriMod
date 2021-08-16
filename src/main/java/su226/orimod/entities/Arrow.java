package su226.orimod.entities;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.registries.IForgeRegistry;
import su226.orimod.Config;
import su226.orimod.Mod;
import su226.orimod.messages.SpiritArcMessage;
import su226.orimod.others.Models;
import su226.orimod.others.Util;

public class Arrow extends EntityArrow {
  public static class Render extends net.minecraft.client.renderer.entity.Render<Arrow> {
    public static class Factory implements IRenderFactory<Arrow> {
      @Override
      public net.minecraft.client.renderer.entity.Render<? super Arrow> createRenderFor(RenderManager manager) {
        return new Render(manager);
      }
    }

    public static final Factory FACTORY = new Factory();
    public static List<BakedQuad> MODEL;
    public static List<BakedQuad> MODEL_OVERLAY;

    public Render(RenderManager manager) {
      super(manager);
    }

    @Override
    protected ResourceLocation getEntityTexture(Arrow arg0) {
      return null;
    }

    @Override
    public void doRender(Arrow entity, double x, double y, double z, float yaw, float partialTicks) {
      GlStateManager.pushMatrix();
      GlStateManager.disableLighting();
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
      GlStateManager.translate(x, y, z);
      GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
      GlStateManager.translate(-0.5, 0, 0);
      Models.renderItemModel(MODEL, 0xffffffff);
      Models.renderItemModel(MODEL_OVERLAY, Config.GLOW_COLOR);
      GlStateManager.enableLighting();
      GlStateManager.popMatrix();
    }

    public static void loadModel() {
      MODEL = Models.loadItemModel("entity/arrow.obj");
      MODEL_OVERLAY = Models.loadItemModel("entity/arrow_overlay.obj");
    }
  }


  public Arrow(World world) {
    super(world);
  }

  public Arrow(World world, EntityLivingBase owner) {
    super(world, owner);
  }

  @Override
  protected ItemStack getArrowStack() {
    return ItemStack.EMPTY;
  }
  
  public static void register(IForgeRegistry<EntityEntry> registry) {
    registry.register(EntityEntryBuilder.create()
      .entity(Arrow.class)
      .id(Util.getLocation("arrow"), Entities.nextId())
      .name(Util.getI18nKey("arrow"))
      .tracker(128, 1, true)
      .build());
  }

  public static void registerRender() {
    RenderingRegistry.registerEntityRenderingHandler(Arrow.class, Render.FACTORY);
  }

  private int ticksInAir;
  public double damage = Config.SPIRIT_ARC.BASE_DAMAGE;
  public int knockbackStrength = Config.SPIRIT_ARC.KNOCKBACK;

  @Override
  public void onUpdate() {
    this.ticksInAir++;
    Vec3d start = new Vec3d(this.posX, this.posY, this.posZ);
    Vec3d end = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

    Entity entity = Util.rayTraceEntity(world, start, end, e -> 
      e != this &&
      e.canBeCollidedWith() &&
      (e != this.shootingEntity || this.ticksInAir >= 5) &&
      EntitySelectors.IS_ALIVE.test(e) &&
      EntitySelectors.NOT_SPECTATING.test(e)
    );

    if (entity instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayer && !((EntityPlayer)this.shootingEntity).canAttackPlayer((EntityPlayer)entity)) {
      entity = null;
    }

    if (entity == null) {
      RayTraceResult block = this.world.rayTraceBlocks(start, end, false, true, false);
      if (block != null && !ForgeEventFactory.onProjectileImpact(this, block)) {
        this.hitBlock(block, start, end);
      }
    } else {
      if (!ForgeEventFactory.onProjectileImpact(this, new RayTraceResult(entity))) {
        this.hitEntity(entity, start, end);
      }
    }

    this.posX += this.motionX;
    this.posY += this.motionY;
    this.posZ += this.motionZ;
    float f4 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
    float newYaw = (float)MathHelper.atan2(this.motionX, this.motionZ) * 57.29577951308232f;
    float newPitch = (float)MathHelper.atan2(this.motionY, f4) * 57.29577951308232f;

    while (newPitch < this.prevRotationPitch - 180) {
      this.prevRotationPitch -= 360;
    }
    while (newPitch >= this.prevRotationPitch + 180) {
      this.prevRotationPitch += 360;
    }
    while (newYaw < this.prevRotationYaw - 180) {
      this.prevRotationYaw -= 360;
    }
    while (newYaw >= this.prevRotationYaw + 180) {
      this.prevRotationYaw += 360;
    }

    this.rotationPitch = this.prevRotationPitch + (newPitch - this.prevRotationPitch) * 0.2F;
    this.rotationYaw = this.prevRotationYaw + (newYaw - this.prevRotationYaw) * 0.2F;

    double fraction = 0.99;
    if (this.isInWater()) {
      for(int i = 0; i < 4; i++) {
        this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * 0.25, this.posY - this.motionY * 0.25, this.posZ - this.motionZ * 0.25, this.motionX, this.motionY, this.motionZ);
      }
      fraction = 0.6;
    }

    if (this.isWet()) {
      this.extinguish();
    }

    this.motionX *= fraction;
    this.motionY *= fraction;
    this.motionZ *= fraction;
    if (!this.hasNoGravity()) {
      this.motionY -= 0.05;
    }

    this.setPosition(this.posX, this.posY, this.posZ);
    this.doBlockCollisions();
  }
  
  protected void hitEntity(Entity entity, Vec3d start, Vec3d end) {
    float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
    int damage = MathHelper.ceil(f * this.damage);
    if (this.getIsCritical()) {
      damage += this.rand.nextInt(damage / 2 + 2);
    }

    if (this.isBurning() && !(entity instanceof EntityEnderman)) {
      entity.setFire(5);
    }

    if (entity.attackEntityFrom(DamageSource.causeArrowDamage(this, this.shootingEntity), damage)) {
      if (entity instanceof EntityLivingBase) {
        EntityLivingBase living = (EntityLivingBase)entity;
        if (this.knockbackStrength > 0) {
          float knockback = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
          if (knockback > 0.0F) {
            living.addVelocity(this.motionX * Config.SPIRIT_ARC.KNOCKBACK * 0.6 / knockback, 0.1D, this.motionZ * Config.SPIRIT_ARC.KNOCKBACK * 0.6 / knockback);
          }
        }

        if (this.shootingEntity instanceof EntityLivingBase) {
          EnchantmentHelper.applyThornEnchantments(living, this.shootingEntity);
          EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase)this.shootingEntity, living);
        }

        this.arrowHit(living);
        if (this.shootingEntity != null && living != this.shootingEntity && living instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP) {
          ((EntityPlayerMP)this.shootingEntity).connection.sendPacket(new SPacketChangeGameState(6, 0.0F));
        }
      }

      if (!this.world.isRemote) {
        Mod.NETWORK.sendToAllAround(new SpiritArcMessage(this.shootingEntity, entity, entity.getEntityBoundingBox().calculateIntercept(start, end).hitVec, start), Util.getTargetPoint(this, 32));
      }
      this.prevPosY = -1000;
      this.posY = -1000;
      this.setDead();
    } else {
      this.motionX *= -0.1;
      this.motionY *= -0.1;
      this.motionZ *= -0.1;
      this.rotationYaw += 180;
      this.prevRotationYaw += 180;
    }
  }
  
  protected void hitBlock(RayTraceResult result, Vec3d start, Vec3d end) {
    BlockPos pos = result.getBlockPos();
    IBlockState state = this.world.getBlockState(pos);
    if (state.getMaterial() != Material.AIR) {
      state.getBlock().onEntityCollidedWithBlock(this.world, pos, state, this);
    }
    if (!this.world.isRemote) {
      Mod.NETWORK.sendToAllAround(new SpiritArcMessage(this.shootingEntity, null, result.hitVec, start), Util.getTargetPoint(this, 32));
    }
    this.prevPosY = -1000;
    this.posY = -1000;
    this.setDead();
  }
}
