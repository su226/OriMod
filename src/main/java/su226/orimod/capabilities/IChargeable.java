package su226.orimod.capabilities;

import java.util.concurrent.Callable;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import su226.orimod.ClientProxy;

public interface IChargeable {
  static class Storage implements IStorage<IChargeable> {
    @Override
    public NBTBase writeNBT(Capability<IChargeable> capability, IChargeable instance, EnumFacing side) {
      return null;
    }

    @Override
    public void readNBT(Capability<IChargeable> capability, IChargeable instance, EnumFacing side, NBTBase nbt) {}
  }

  static class Implementation implements IChargeable {
    int beginTick;
    int lastFlash;
    int lastParticle;
    int duration;

    @Override
    public float getCharge() {
      if (this.beginTick == 0) {
        return 0;
      }
      return Math.min((ClientProxy.tick + Minecraft.getMinecraft().getRenderPartialTicks() - this.beginTick) / this.duration, 1);
    }

    @Override
    public boolean blink(int interval) {
      if (ClientProxy.tick - lastFlash >= 4) {
        lastFlash = ClientProxy.tick;
      }
      return ClientProxy.tick - lastFlash < 2;
    }

    @Override
    public boolean blink() {
      return blink(2);
    }

    @Override
    public boolean particle(double minInterval, double maxInterval) {
      double charge = getCharge();
      double interval = minInterval * charge + maxInterval * (1 - charge);
      if (ClientProxy.tick - lastParticle > interval) {
        lastParticle = ClientProxy.tick;
        return true;
      }
      return false;
    }

    @Override
    public boolean particle() {
      return particle(2, 5);
    }

    @Override
    public void beginCharge() {
      this.beginTick = ClientProxy.tick;
    }

    @Override
    public void endCharge() {
      this.beginTick = 0;
    }

    @Override
    public void setDuration(int duration) {
      this.duration = duration;      
    }
  }

  static class Factory implements Callable<IChargeable> {
    @Override
    public IChargeable call() {
      return new Implementation();
    }
  }

  public static class Provider implements ICapabilityProvider {
    private IChargeable chargeable;
  
    public Provider() {
      this.chargeable = new Implementation();
    }
  
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return capability == Capabilities.CHARGEABLE ? (T)this.chargeable : null;
    }
  
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == Capabilities.CHARGEABLE;
    }
  }

  float getCharge();
  boolean blink();
  boolean blink(int interval);
  boolean particle();
  boolean particle(double minInterval, double maxInterval);
  void beginCharge();
  void endCharge();
  void setDuration(int duration);

  public static void register() {
    CapabilityManager.INSTANCE.register(IChargeable.class, new Storage(), new Factory());
  }
}
