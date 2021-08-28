package su226.orimod.capabilities;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public interface ICooldown {
  static class Storage implements IStorage<ICooldown> {
    @Override
    public NBTBase writeNBT(Capability<ICooldown> capability, ICooldown instance, EnumFacing side) {
      return null;
    }

    @Override
    public void readNBT(Capability<ICooldown> capability, ICooldown instance, EnumFacing side, NBTBase nbt) {}
  }

  static class Implementation implements ICooldown {
    private static class Data {
      public int maxCount;
      public int countLeft;
      public int cooldown;
      public int lastAction;
    }
    private Map<String, Data> datas = new HashMap<>();
    private Entity entity;

    public Implementation(Entity entity) {
      this.entity = entity;
    }

    @Override
    public boolean doAction(String identifer) {
      Data data = datas.get(identifer);
      if (data == null) {
        return false;
      }
      int tick = this.entity == null ? 0 : this.entity.ticksExisted;
      if (data.cooldown == -1 ? data.lastAction != -1 : data.lastAction + data.cooldown < tick) {
        if (data.countLeft > 0) { // If it's in cooldown, decrease countLeft
          data.countLeft--;
          return true;
        }
        return false;
      } else { // Otherwise, reset countLeft
        data.countLeft = data.maxCount - 1;
        data.lastAction = tick;
        return true;
      }
    }

    public int getLastAction(String identifer) {
      Data data = datas.get(identifer);
      if (data == null) {
        return 0;
      }
      return data.lastAction;
    }
    
    @Override
    public boolean isInCooldown(String identifer) {
      Data data = datas.get(identifer);
      if (data == null) {
        return false;
      }
      int tick = this.entity == null ? 0 : this.entity.ticksExisted;
      return data.cooldown == -1 ? data.lastAction != -1 : data.lastAction + data.cooldown < tick;
    }

    @Override
    public void setCooldown(String identifer, int maxCount, int cooldown) {
      Data data;
      if (datas.containsKey(identifer)) {
        data = datas.get(identifer);
      } else {
        data = new Data();
        datas.put(identifer, data);
      }
      data.maxCount = maxCount;
      data.countLeft = maxCount;
      data.cooldown = cooldown;
      data.lastAction = -1;
    }

    @Override
    public boolean isSet(String identifer) {
      return datas.containsKey(identifer);
    }
  }

  static class Factory implements Callable<ICooldown> {
    @Override
    public ICooldown call() {
      return new Implementation(null);
    }
  }

  public static class Provider implements ICapabilityProvider {
    private ICooldown cooldown;
  
    public Provider(Entity entity) {
      this.cooldown = new Implementation(entity);
    }
  
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return capability == Capabilities.COOLDOWN ? (T)this.cooldown : null;
    }
  
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == Capabilities.COOLDOWN;
    }
  }

  boolean doAction(String identifer);
  int getLastAction(String identifer);
  boolean isInCooldown(String identifer);
  void setCooldown(String identifer, int maxCount, int cooldown);
  boolean isSet(String identifer);

  public static void register() {
    CapabilityManager.INSTANCE.register(ICooldown.class, new Storage(), new Factory());
  }
}
