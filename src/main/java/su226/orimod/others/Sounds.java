package su226.orimod.others;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class Sounds {
  private static final List<SoundEvent> SOUNDS = new ArrayList<>();

  public static final SoundEvent BASH_START = create("bash_start");
  public static final SoundEvent BASH_NO_TARGET = create("bash_no_target");
  public static final SoundEvent BASH_TIMEOUT = create("bash_timeout");
  public static final SoundEvent BASH_END = create("bash_end");
  public static final SoundEvent CHARGE_FLAME_END = create("charge_flame_end");
  public static final SoundEvent CHARGE_FLAME_LOOP = create("charge_flame_loop");
  public static final SoundEvent SPIRIT_FLAME_HIT = create("spirit_flame_hit");
  public static final SoundEvent SPIRIT_FLAME_THROW = create("spirit_flame_throw");
  public static final SoundEvent GLIDE_START = create("glide_start");
  public static final SoundEvent GLIDE_END = create("glide_end");
  public static final SoundEvent SPIRIT_ARC_DRAW = create("spirit_arc_draw");
  public static final SoundEvent SPIRIT_ARC_SHOOT = create("spirit_arc_shoot");
  public static final SoundEvent ARROW_HIT_ENTITY = create("arrow_hit_entity");
  public static final SoundEvent ARROW_HIT_GROUND = create("arrow_hit_ground");
  public static final SoundEvent LIGHT_BURST_START = create("light_burst_start");
  public static final SoundEvent LIGHT_BURST_THROW = create("light_burst_throw");
  public static final SoundEvent LIGHT_BURST_HIT_ENTITY = create("light_burst_hit_entity");
  public static final SoundEvent LIGHT_BURST_HIT_GROUND = create("light_burst_hit_ground");
  public static final SoundEvent MULTI_JUMP = create("multi_jump");
  public static final SoundEvent WALL_JUMP = create("wall_jump");
  public static final SoundEvent STOMP_START = create("stomp_start");
  public static final SoundEvent STOMP_FALL = create("stomp_fall");
  public static final SoundEvent STOMP_HIT = create("stomp_hit");
  public static final SoundEvent FLAP_START = create("flap_start");
  public static final SoundEvent FLAP_END = create("flap_end");

  private static SoundEvent create(String name) {
    ResourceLocation loc = Util.getLocation(name);
    SoundEvent sound = new SoundEvent(loc).setRegistryName(loc);
    SOUNDS.add(sound);
    return sound;
  }

  public static void register(IForgeRegistry<SoundEvent> registry) {
    for (SoundEvent sound : SOUNDS) {
      registry.register(sound);
    }
  }
}
