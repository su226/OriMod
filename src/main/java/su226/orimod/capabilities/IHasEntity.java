package su226.orimod.capabilities;

import java.util.concurrent.Callable;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public interface IHasEntity {
  static class Storage implements IStorage<IHasEntity> {
    @Override
    public NBTBase writeNBT(Capability<IHasEntity> capability, IHasEntity instance, EnumFacing side) {
      return null;
    }

    @Override
    public void readNBT(Capability<IHasEntity> capability, IHasEntity instance, EnumFacing side, NBTBase nbt) {}
  }

  static class Implementation implements IHasEntity {
    Entity entity;

    @Override
    public Entity getEntity() {
      return this.entity;
    }

    @Override
    public void setEntity(Entity entity) {
      this.entity = entity;
    }
  }

  static class Factory implements Callable<IHasEntity> {
    @Override
    public IHasEntity call() {
      return new Implementation();
    }
  }

  public static class Provider implements ICapabilityProvider {
    private IHasEntity hasEntity;
  
    public Provider() {
      this.hasEntity = new Implementation();
    }
  
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return capability == Capabilities.HAS_ENTITY ? (T)this.hasEntity : null;
    }
  
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == Capabilities.HAS_ENTITY;
    }
  }

  Entity getEntity();
  void setEntity(Entity entity);

  public static void register() {
    CapabilityManager.INSTANCE.register(IHasEntity.class, new Storage(), new Factory());
  }
}
