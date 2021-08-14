package su226.orimod.capabilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.BiPredicate;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import su226.orimod.others.Util;

public interface IEquipper {
  @Optional.Interface(modid = "baubles", iface = "baubles.api.IBauble")
  public static interface IEquipable extends IBauble {
    static final BiPredicate<ItemStack, EntityPlayer> ANY_HAND = (stack, owner) -> {
      return owner.getHeldItemMainhand() == stack || owner.getHeldItemOffhand() == stack;
    };
    static final BiPredicate<ItemStack, EntityPlayer> HOTBAR_AND_OFF_HAND = (stack, owner) -> {
      return owner.getHeldItemOffhand() == stack || owner.inventory.mainInventory.indexOf(stack) < 9;
    };

    default void updateEquipable(ItemStack stack, Entity owner) {
      if (!(owner instanceof EntityPlayer)) {
        return;
      }
      EntityPlayer player = (EntityPlayer)owner;
      if (!this.getEquipableSlots().test(stack, player)) {
        return;
      }
      IEquipper cap = player.getCapability(Capabilities.EQUIPPER, null);
      Map<ItemStack, Integer> equippeds = cap.getEquippeds();
      if (!equippeds.containsKey(stack)) {
        Multiset<IEquipable> equipables = cap.getEquipables();
        equipables.add(this);
        if (equipables.count(this) == 1) {
          this.onEquipableEquip(player, addPriority(cap, this));
        }
      }
      equippeds.put(stack, player.ticksExisted);
      Set<IEquipable> updated = cap.getUpdated();
      if (!updated.contains(this)) {
        updated.add(this);
        this.onEquipableUpdate(player, checkPriority(cap, this));
      }
    }

    default void onEquipableEquip(EntityPlayer owner, boolean isMaxPriority) {}
    default void onEquipableUpdate(EntityPlayer owner, boolean isMaxPriority) {}
    default void onEquipableUnequip(EntityPlayer owner, boolean isMaxPriority) {}
    default String getEquipableType() { return null; }
    default int getEquipablePriority() { return 0; }

    BiPredicate<ItemStack, EntityPlayer> getEquipableSlots();

    @Override
    default BaubleType getBaubleType(ItemStack stack) {
      return BaubleType.TRINKET;
    }

    @Override
    default void onEquipped(ItemStack stack, EntityLivingBase owner) {
      IEquipper cap = owner.getCapability(Capabilities.EQUIPPER, null);
      Multiset<IEquipable> equipables = cap.getEquipables();
      equipables.add(this);
      if (equipables.count(this) == 1) {
          onEquipableEquip((EntityPlayer)owner, addPriority(cap, this));
      }
    }

    @Override
    default void onWornTick(ItemStack stack, EntityLivingBase owner) {
      IEquipper cap = owner.getCapability(Capabilities.EQUIPPER, null);
      Set<IEquipable> updated = cap.getUpdated();
      if (!updated.contains(this)) {
        updated.add(this);
        this.onEquipableUpdate((EntityPlayer)owner, checkPriority(cap, this));
      }
    }

    @Override
    default void onUnequipped(ItemStack stack, EntityLivingBase owner) {
      IEquipper cap = owner.getCapability(Capabilities.EQUIPPER, null);
      Multiset<IEquipable> equipables = cap.getEquipables();
      equipables.remove(this);
      if (!equipables.contains(this)) {
        onEquipableUnequip((EntityPlayer)owner, removePriority(cap, this));
      }
    }
  }

  static class Storage implements IStorage<IEquipper> {
    @Override
    public NBTBase writeNBT(Capability<IEquipper> capability, IEquipper instance, EnumFacing side) {
      return null;
    }

    @Override
    public void readNBT(Capability<IEquipper> capability, IEquipper instance, EnumFacing side, NBTBase nbt) {}
  }

  static class Priorities {
    private List<Integer> values = new ArrayList<>();
    private int max;
    
    public boolean check(IEquipable equipable) {
      return equipable.getEquipablePriority() == this.max;
    }

    public boolean add(IEquipable equipable) {
      int value = equipable.getEquipablePriority();
      this.max = Math.max(value, this.max);
      values.add(value);
      return value == this.max;
    }

    public boolean remove(IEquipable equipable) {
      int value = equipable.getEquipablePriority();
      values.remove(new Integer(value));
      if (value == this.max) {
        this.max = 0;
        for (int i : values) {
          this.max = Math.max(this.max, i);
        }
        return true;
      }
      return false;
    }

    public boolean isEmpty() {
      return values.isEmpty();
    }
  }

  static class Implementation implements IEquipper {
    Map<ItemStack, Integer> equippeds = new HashMap<>();
    Multiset<IEquipable> equipables = HashMultiset.create();
    Map<String, Priorities> priorities = new HashMap<>();
    Set<IEquipable> updated = new HashSet<>();

    @Override
    public Map<ItemStack, Integer> getEquippeds() {
      return equippeds;
    }

    @Override
    public Multiset<IEquipable> getEquipables() {
      return equipables;
    }

    @Override
    public Set<IEquipable> getUpdated() {
      return updated;
    }

    @Override
    public boolean isEquipped(IEquipable equipable) {
      return equipables.contains(equipable);
    }

    @Override
    public Map<String, Priorities> getPrioritiesMap() {
      return priorities;
    }
  }

  static class Factory implements Callable<IEquipper> {
    @Override
    public IEquipper call() {
      return new Implementation();
    }
  }

  public static class Provider implements ICapabilityProvider {
    private IEquipper equipper;
  
    public Provider() {
      this.equipper = new Implementation();
    }
  
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return capability == Capabilities.EQUIPPER ? (T)this.equipper : null;
    }
  
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == Capabilities.EQUIPPER;
    }
  }

  Map<ItemStack, Integer> getEquippeds();
  Multiset<IEquipable> getEquipables();
  Set<IEquipable> getUpdated();
  Map<String, Priorities> getPrioritiesMap();
  boolean isEquipped(IEquipable equipable);

  public static boolean addPriority(IEquipper cap, IEquipable equipable) {
    String type = equipable.getEquipableType();
    if (type == null) {
      return true;
    }
    Map<String, Priorities> prioritiesMap = cap.getPrioritiesMap();
    Priorities priorities;
    if (prioritiesMap.containsKey(type)) {
      priorities = prioritiesMap.get(type);
    } else {
      priorities = new Priorities();
      prioritiesMap.put(type, priorities);
    }
    return priorities.add(equipable);
  }

  public static boolean checkPriority(IEquipper cap, IEquipable equipable) {
    String type = equipable.getEquipableType();
    if (type == null) {
      return true;
    }
    return cap.getPrioritiesMap().get(type).check(equipable);
  }

  public static boolean removePriority(IEquipper cap, IEquipable equipable) {
    String type = equipable.getEquipableType();
    if (type == null) {
      return true;
    }
    Map<String, Priorities> prioritiesMap = cap.getPrioritiesMap();
    Priorities priorities = prioritiesMap.get(type);
    boolean ret = priorities.remove(equipable);
    if (priorities.isEmpty()) {
      prioritiesMap.remove(type);
    }
    return ret;
  }
  
  public static void playerTick(EntityPlayer player) {
    IEquipper cap = player.getCapability(Capabilities.EQUIPPER, null);
    Multiset<IEquipable> equipables = cap.getEquipables();
    cap.getUpdated().clear();
    Iterator<Map.Entry<ItemStack, Integer>> it = cap.getEquippeds().entrySet().iterator();
    int tick = player.ticksExisted - 1;
    while (it.hasNext()) {
      Map.Entry<ItemStack, Integer> entry = it.next();
      if (entry.getValue() < tick) {
        IEquipable equipable = (IEquipable)Util.getItem(entry.getKey());
        equipables.remove(equipable);
        it.remove();
        if (!equipables.contains(equipable)) {
          equipable.onEquipableUnequip(player, removePriority(cap, equipable));
        }
      }
    }
  }

  public static void register() {
    CapabilityManager.INSTANCE.register(IEquipper.class, new Storage(), new Factory());
  }
}
