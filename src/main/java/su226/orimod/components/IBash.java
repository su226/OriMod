package su226.orimod.components;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public interface IBash extends ComponentV3 {
  class Item extends ItemComponent implements IBash {
    private Entity entity;

    public Item(ItemStack stack) {
      super(stack);
    }

    @Override
    public Entity getEntity() {
      return this.entity;
    }

    @Override
    public void setEntity(Entity value) {
      this.entity = value;
    }
  }
  Entity getEntity();
  void setEntity(Entity value);
}
