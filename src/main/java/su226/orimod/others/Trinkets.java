package su226.orimod.others;

import dev.emi.trinkets.api.SlotGroups;
import dev.emi.trinkets.api.TrinketSlots;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

public class Trinkets {
  private static final Identifier TEXTURE = Util.getIdentifier("textures/item/empty_trinket_slot_spirit_shard.png");
  public static final Set<String> SHARD_SLOTS = new HashSet<>();
  private static int id;

  private static String next() {
    int curId = id++;
    StringBuilder builder = new StringBuilder();
    if (curId == 0) {
      builder.append('a');
    } else {
      while (curId > 0) {
        builder.append((char)('a' + curId % 26));
        curId /= 26;
      }
    }
    String ret = String.format("spiritshard%s", builder.reverse());
    SHARD_SLOTS.add(ret);
    return ret;
  }

  public static void register() {
    TrinketSlots.addSlot(SlotGroups.HEAD, next(), TEXTURE);
    TrinketSlots.addSlot(SlotGroups.HEAD, next(), TEXTURE);
    TrinketSlots.addSlot(SlotGroups.CHEST, next(), TEXTURE);
    TrinketSlots.addSlot(SlotGroups.CHEST, next(), TEXTURE);
    TrinketSlots.addSlot(SlotGroups.LEGS, next(), TEXTURE);
    TrinketSlots.addSlot(SlotGroups.LEGS, next(), TEXTURE);
    TrinketSlots.addSlot(SlotGroups.FEET, next(), TEXTURE);
    TrinketSlots.addSlot(SlotGroups.FEET, next(), TEXTURE);
  }
}
