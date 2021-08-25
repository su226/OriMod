package su226.orimod.components;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.entity.PlayerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.HashMap;
import java.util.Map;

public interface ICooldown extends ComponentV3 {
  class Player implements PlayerComponent<ICooldown>, ICooldown {
    private static class Data {
      public int maxCount;
      public int countLeft;
      public int cooldown;
      public int lastAction;
    }
    private final Map<String, Data> datas = new HashMap<>();
    private final PlayerEntity entity;

    public Player(PlayerEntity entity) {
      this.entity = entity;
    }

    @Override
    public boolean doAction(String identifier) {
      Data data = datas.get(identifier);
      if (data == null) {
        return false;
      }
      int tick = this.entity == null ? 0 : this.entity.age;
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

    public int getLastAction(String identifier) {
      Data data = datas.get(identifier);
      if (data == null) {
        return 0;
      }
      return data.lastAction;
    }
    
    @Override
    public boolean isInCooldown(String identifier) {
      Data data = datas.get(identifier);
      if (data == null) {
        return false;
      }
      int tick = this.entity == null ? 0 : this.entity.age;
      return data.cooldown == -1 ? data.lastAction != -1 : data.lastAction + data.cooldown < tick;
    }

    @Override
    public void setCooldown(String identifier, int maxCount, int cooldown) {
      Data data;
      if (datas.containsKey(identifier)) {
        data = datas.get(identifier);
      } else {
        data = new Data();
        datas.put(identifier, data);
      }
      data.maxCount = maxCount;
      data.countLeft = maxCount;
      data.cooldown = cooldown;
      data.lastAction = -1;
    }

    @Override
    public boolean isSet(String identifier) {
      return datas.containsKey(identifier);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {}

    @Override
    public void writeToNbt(NbtCompound tag) {}
  }

  boolean doAction(String identifier);
  int getLastAction(String identifier);
  boolean isInCooldown(String identifier);
  void setCooldown(String identifier, int maxCount, int cooldown);
  boolean isSet(String identifier);
}
