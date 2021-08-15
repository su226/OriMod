package su226.orimod.capabilities;

import java.util.concurrent.Callable;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.stats.StatBasic;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import su226.orimod.ClientProxy;
import su226.orimod.Config;
import su226.orimod.Mod;
import su226.orimod.entities.SpiritLightOrb;
import su226.orimod.messages.SpiritLightSyncMessage;
import su226.orimod.others.Models;

public interface ISpiritLight {
  static class Storage implements IStorage<ISpiritLight> {
    @Override
    public NBTBase writeNBT(Capability<ISpiritLight> capability, ISpiritLight instance, EnumFacing side) {
      return new NBTTagInt(instance.get());
    }

    @Override
    public void readNBT(Capability<ISpiritLight> capability, ISpiritLight instance, EnumFacing side, NBTBase nbt) {
      instance.set(((NBTTagInt)nbt).getInt());
    }
  }

  static class Implementation implements ISpiritLight {
    int value;
    EntityPlayer player;
    boolean dirty;
    public static int hudTick;

    public Implementation(EntityPlayer player) {
      this.player = player;
    }

    @Override
    public int get() {
      return this.value;
    }

    @Override
    public void set(int value) {
      this.value = value;
      this.dirty = true;
    }

    @Override
    public void collect(int value) {
      if (this.player != null) {
        this.player.addStat(STAT_LIGHT_COLLECT, value);
      }
      this.value += value;
      this.dirty = true;
    }

    @Override
    public void cost(int value) {
      if (this.player != null) {
        this.player.addStat(STAT_LIGHT_COST, value);
      }
      this.value -= value;
      this.dirty = true;
    }

    @Override
    public boolean isDirty() {
      return this.dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
      this.dirty = dirty;
    }
  }

  static class Factory implements Callable<ISpiritLight> {
    @Override
    public ISpiritLight call() {
      return new Implementation(null);
    }
  }

  public static class Provider implements ICapabilitySerializable<NBTTagInt> {
    private ISpiritLight spiritLight;
  
    public Provider(EntityPlayer player) {
      this.spiritLight = new Implementation(player);
    }
  
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return capability == Capabilities.SPIRIT_LIGHT ? (T)this.spiritLight : null;
    }
  
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == Capabilities.SPIRIT_LIGHT;
    }

    @Override
    public void deserializeNBT(NBTTagInt nbt) {
      Capabilities.SPIRIT_LIGHT.getStorage().readNBT(Capabilities.SPIRIT_LIGHT, this.spiritLight, null, nbt);
    }

    @Override
    public NBTTagInt serializeNBT() {
      return (NBTTagInt)Capabilities.SPIRIT_LIGHT.getStorage().writeNBT(Capabilities.SPIRIT_LIGHT, this.spiritLight, null);
    }
  }

  public static final StatBasic STAT_LIGHT_COLLECT = new StatBasic("stat.orimod.light_collect", new TextComponentTranslation("stad.orimod.light_collect"));
  public static final StatBasic STAT_LIGHT_COST = new StatBasic("stat.orimod.light_cost", new TextComponentString("stat.orimod.light_cost"));

  int get();
  void set(int value);
  void collect(int value);
  void cost(int value);
  boolean isDirty();
  void setDirty(boolean dirty);

  public static void register() {
    CapabilityManager.INSTANCE.register(ISpiritLight.class, new Storage(), new Factory());
    STAT_LIGHT_COLLECT.registerStat();
    STAT_LIGHT_COST.registerStat();
  }

  static void livingDeath(LivingDeathEvent event) {
    EntityLivingBase ent = event.getEntityLiving();
    if (!ent.world.isRemote) {
      if (ent instanceof EntityPlayer) {
        ISpiritLight cap = ent.getCapability(Capabilities.SPIRIT_LIGHT, null);
        int keep = (int)(cap.get() * Config.SPIRIT_LIGHT.DEATH_PENALTY);
        int drop = (int)((cap.get() - keep) * Config.SPIRIT_LIGHT.DEATH_DROP);
        cap.set(keep);
        if (drop != 0) {
          ent.world.spawnEntity(new SpiritLightOrb(ent.world, ent.posX, ent.posY, ent.posZ, drop));
        }
      } else if (ent.getAttackingEntity() instanceof EntityPlayer) {
        ent.world.spawnEntity(new SpiritLightOrb(ent.world, ent.posX, ent.posY, ent.posZ, (int)(ent.getMaxHealth() * Config.SPIRIT_LIGHT.DROP_MULITPLIER)));
      }
    }
  }

  static void playerTick(EntityPlayer player) {
    ISpiritLight cap = player.getCapability(Capabilities.SPIRIT_LIGHT, null);
    if (cap.isDirty()) {
      if (player.world.isRemote) {
        Implementation.hudTick = ClientProxy.tick;
      } else {
        Mod.NETWORK.sendTo(new SpiritLightSyncMessage(cap.get()), (EntityPlayerMP)player);
      }
      cap.setDirty(false);
    }
  }

  static void renderGameOverlayPost(RenderGameOverlayEvent.Post event) {
    float delta = ClientProxy.tick - Implementation.hudTick + event.getPartialTicks();
    if (delta > 40) {
      return;
    }
    float alpha = 1;
    if (delta < 10) {
      alpha = delta / 10;
    } else if (delta >= 30) {
      alpha = (40 - delta) / 10;
    }
    ScaledResolution resolution = event.getResolution();
    int x = resolution.getScaledWidth() - 40;
    int y = 8;
    GlStateManager.color(1, 1, 1, alpha);
    GlStateManager.enableBlend();
    GlStateManager.alphaFunc(GL11.GL_EQUAL, 1);
    GlStateManager.pushMatrix();
    GlStateManager.translate(x + 16, y + 16, 0);
    GlStateManager.rotate((ClientProxy.tick + event.getPartialTicks()) * 3, 0, 0, 1);
    Models.drawSquare(new Vec3d(-16, -16, 0), new Vec3d(-16, 16, 0), new Vec3d(16, 16, 0), SpiritLightOrb.Render.TEXTURE);
    GlStateManager.popMatrix();
    Minecraft mc = Minecraft.getMinecraft();
    String str = Integer.toString(mc.player.getCapability(Capabilities.SPIRIT_LIGHT, null).get());
    GlStateManager.disableBlend();
    GlStateManager.pushMatrix();
    GlStateManager.translate(x + 16 - mc.fontRenderer.getStringWidth(str), y + 32, 0);
    GlStateManager.scale(2, 2, 1);
    mc.fontRenderer.drawStringWithShadow(str, 0, 0, 0xffff55);
    GlStateManager.popMatrix();
  }

  static void playerClone(PlayerEvent.Clone event) {

    event.getEntityPlayer().getCapability(Capabilities.SPIRIT_LIGHT, null).set(event.getOriginal().getCapability(Capabilities.SPIRIT_LIGHT, null).get());
  }
}
