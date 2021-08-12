package su226.orimod.capabilities;

import java.util.concurrent.Callable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public interface IKurosFeather {
  static class Storage implements IStorage<IKurosFeather> {
    @Override
    public NBTBase writeNBT(Capability<IKurosFeather> capability, IKurosFeather instance, EnumFacing side) {
      return null;
    }

    @Override
    public void readNBT(Capability<IKurosFeather> capability, IKurosFeather instance, EnumFacing side, NBTBase nbt) {}
  }

  static class Implementation implements IKurosFeather {
    boolean prevGliding;
    boolean shouldUpdate;

    @Override
    public boolean isPrevGliding() {
      return this.prevGliding;
    }

    @Override
    public void setPrevGliding(boolean prevGliding) {
      this.prevGliding = prevGliding;
    }

    @Override
    public boolean shouldUpdate() {
      return this.shouldUpdate;
    }

    @Override
    public void setShouldUpdate(boolean shouldUpdate) {
      this.shouldUpdate = shouldUpdate;
    }
  }

  static class Factory implements Callable<IKurosFeather> {
    @Override
    public IKurosFeather call() {
      return new Implementation();
    }
  }

  public static class Provider implements ICapabilityProvider {
    private IKurosFeather kurosFeather;
  
    public Provider() {
      this.kurosFeather = new Implementation();
    }
  
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return capability == Capabilities.KUROS_FEATHER ? (T)this.kurosFeather : null;
    }
  
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == Capabilities.KUROS_FEATHER;
    }
  }

  boolean isPrevGliding();
  void setPrevGliding(boolean prevGliding);
  boolean shouldUpdate();
  void setShouldUpdate(boolean shouldUpdate);

  public static void register() {
    CapabilityManager.INSTANCE.register(IKurosFeather.class, new Storage(), new Factory());
  }
}
