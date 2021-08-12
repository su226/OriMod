package su226.orimod.capabilities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public interface IMultiJump {
  static class Storage implements IStorage<IMultiJump> {
    @Override
    public NBTBase writeNBT(Capability<IMultiJump> capability, IMultiJump instance, EnumFacing side) {
      return null;
    }

    @Override
    public void readNBT(Capability<IMultiJump> capability, IMultiJump instance, EnumFacing side, NBTBase nbt) {}
  }

  static class Implementation implements IMultiJump {
    List<Integer> counts = new ArrayList<>();
    int maxCount;
    int countLeft;

    @Override
    public void addCount(int count) {
      counts.add(count);
      if (count > maxCount) {
        maxCount = count;
      }
    }

    @Override
    public void removeCount(int count) {
      counts.remove(new Integer(count));
      if (count == maxCount) {
        maxCount = 0;
        for (int i : counts) {
          if (i > maxCount) {
            maxCount = i;
          }
        }
      }
    }

    @Override
    public int getMaxCount() {
      return this.maxCount;
    }

    @Override
    public boolean doJump() {
      if (this.countLeft > 0) {
        this.countLeft--;
        return true;
      }
      return false;
    }

    @Override
    public void resetJump() {
      this.countLeft = this.maxCount;
    }
  }

  static class Factory implements Callable<IMultiJump> {
    @Override
    public IMultiJump call() {
      return new Implementation();
    }
  }

  public static class Provider implements ICapabilityProvider {
    private IMultiJump multiJump;
  
    public Provider() {
      this.multiJump = new Implementation();
    }
  
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return capability == Capabilities.MULTI_JUMP ? (T)this.multiJump : null;
    }
  
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == Capabilities.MULTI_JUMP;
    }
  }

  void addCount(int count);
  void removeCount(int count);
  int getMaxCount();
  boolean doJump();
  void resetJump();

  public static void register() {
    CapabilityManager.INSTANCE.register(IMultiJump.class, new Storage(), new Factory());
  }
}
