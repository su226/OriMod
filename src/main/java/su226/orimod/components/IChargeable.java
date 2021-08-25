package su226.orimod.components;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import su226.orimod.ModClient;

import java.util.HashMap;
import java.util.Map;

public interface IChargeable extends ComponentV3 {
  class Item extends ItemComponent implements IChargeable {
    private int duration;
    private int beginTick;
    private final Map<String, Integer> TIMERS = new HashMap<>();

    public Item(ItemStack stack) {
      super(stack);
    }

    @Override
    public void beginCharge(int duration) {
      this.duration = duration;
      this.beginTick = ModClient.tick;
    }

    @Override
    public void endCharge() {
      this.beginTick = 0;
    }

    @Override
    public boolean isCharging() {
      return this.beginTick != 0;
    }

    @Override
    public float getCharge() {
      return this.beginTick == 0 ? 0 : Math.min(1, (ModClient.tick + MinecraftClient.getInstance().getTickDelta() - this.beginTick) / this.duration);
    }

    @Override
    public boolean activeTimer(String identifier, int minInterval, int maxInterval) {
      int lastActive = TIMERS.getOrDefault(identifier, 0);
      float charge = this.getCharge();
      float interval = minInterval * charge + maxInterval * (1 - charge);
      if (lastActive + interval <= ModClient.tick) {
        TIMERS.put(identifier, ModClient.tick);
        return true;
      }
      return false;
    }
  }

  void beginCharge(int duration);
  void endCharge();
  boolean isCharging();
  float getCharge();
  boolean activeTimer(String identifier, int minInterval, int maxInterval);
}
