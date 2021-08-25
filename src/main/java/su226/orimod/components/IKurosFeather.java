package su226.orimod.components;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import dev.onyxstudios.cca.api.v3.entity.PlayerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public interface IKurosFeather extends ComponentV3, CommonTickingComponent {
  class Player implements PlayerComponent<IKurosFeather>, IKurosFeather {
    private boolean prevGliding;
    private boolean shouldUpdate;
    private boolean updated;
    private boolean prevUpdated;

    public Player(PlayerEntity entity) {}

    @Override
    public boolean isPrevGliding() {
      return this.prevGliding;
    }

    @Override
    public void setPrevGliding(boolean value) {
      this.prevGliding = value;
    }

    @Override
    public boolean shouldUpdate() {
      return this.shouldUpdate;
    }

    @Override
    public void setShouldUpdate(boolean value) {
      this.shouldUpdate = value;
    }

    @Override
    public boolean flagUpdated() {
      boolean ret = !this.updated;
      this.updated = true;
      return ret;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {}
    @Override
    public void writeToNbt(NbtCompound tag) {}

    @Override
    public void tick() {
      if (!this.updated && this.prevUpdated) {
        this.shouldUpdate = true;
        this.prevGliding = false;
      }
      this.prevUpdated = this.updated;
      this.updated = false;
    }
  }

  boolean shouldUpdate();
  boolean isPrevGliding();
  void setPrevGliding(boolean value);
  void setShouldUpdate(boolean value);
  boolean flagUpdated();
}
