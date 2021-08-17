package su226.orimod;

import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

@net.minecraftforge.common.config.Config(modid = Mod.MODID)
public class Config {
  @RequiresMcRestart
  @LangKey("config.orimod.3d_model")
  public static boolean ENABLE_3D = true;

  @LangKey("config.orimod.glow_color")
  public static int GLOW_COLOR = 0x8899ccff;

  @LangKey("config.orimod.tools")
  public static Tools TOOLS = new Tools();

  @LangKey("config.orimod.double_click_max_interval")
  public static int DOUBLE_CLICK_MAX_INTERVAL = 5;

  public static class Tools {
    @RequiresMcRestart
    @LangKey("config.orimod.tools.harvest_level")
    @RangeInt(min = 0)
    public int HARVEST_LEVEL = 4;
  
    @RequiresMcRestart
    @LangKey("config.orimod.tools.efficiency")
    @RangeDouble(min = 0)
    public double EFFICIENCY = 12;
  
    @RequiresMcRestart
    @LangKey("config.orimod.tools.base_damage")
    @RangeDouble(min = 0)
    public double DAMAGE = 4;
  
    @RequiresMcRestart
    @LangKey("config.orimod.tools.enchantability")
    @RangeInt(min = 0)
    public int ENCHANTABILITY = 22;
  }

  @LangKey("config.orimod.spirit_flame")
  public static SpiritFlame SPIRIT_FLAME = new SpiritFlame();

  public static class SpiritFlame {
    @LangKey("config.orimod.spirit_flame.radius")
    @RangeDouble(min = 0)
    public double RADIUS = 5;
  
    @LangKey("config.orimod.spirit_flame.targets")
    @RangeInt(min = 1)
    public int TARGETS = 1;

    @LangKey("config.orimod.spirit_flame.damage")
    @RangeDouble(min = 0)
    public double DAMAGE = 6;
  
    @LangKey("config.orimod.spirit_flame.ignore_pets")
    public boolean IGNORE_PETS = true;    

    @LangKey("config.orimod.spirit_flame.explosion_force")
    @RangeDouble(min = 0)
    public double EXPLOSION_FORCE = 4;

    @LangKey("config.orimod.spirit_flame.charge_duration")
    @RangeInt(min = 0)
    public int CHARGE_DURATION = 30;
  }

  @LangKey("config.orimod.bash")
  public static Bash BASH = new Bash();

  public static class Bash {
    @LangKey("config.orimod.bash.multiplier")
    @RangeDouble(min = 0)
    public double MULTIPLIER = 1;

    @LangKey("config.orimod.bash.range")
    @RangeDouble(min = 0)
    public double RANGE = 3;

    @LangKey("config.orimod.bash.timeout")
    @RangeInt(min = 0)
    public int TIMEOUT = 40;
  }

  @LangKey("config.orimod.kuros_feather")
  public static KurosFeather KUROS_FEATHER = new KurosFeather();

  public static class KurosFeather {
    @LangKey("config.orimod.kuros_feather.fall_multiplier")
    @RangeDouble(min = 0, max = 1)
    public double FALL_MULTIPLIER = 0.8;

    @LangKey("config.orimod.kuros_feather.speed_compensation")
    @RangeDouble(min = 0)
    public double SPEED_COMPENSATION = 0.1;

    @LangKey("config.orimod.kuros_feather.length")
    @RangeDouble(min = 0)
    public double LENGTH = 5;

    @LangKey("config.orimod.kuros_feather.range")
    @RangeDouble(min = 0)
    public double RANGE = 1;

    @LangKey("config.orimod.kuros_feather.force")
    @RangeDouble(min = 0)
    public double FORCE = 1;

    @LangKey("config.orimod.kuros_feather.cooldown")
    @RangeInt(min = 0)
    public int COOLDOWN = 20;
  }

  @LangKey("config.orimod.spirit_arc")
  public static SpiritArc SPIRIT_ARC = new SpiritArc();

  public static class SpiritArc {
    @LangKey("config.orimod.spirit_arc.base_damage")
    @RangeDouble(min = 0)
    public double BASE_DAMAGE = 2;

    @LangKey("config.orimod.spirit_arc.knockback")
    @RangeInt(min = 0)
    public int KNOCKBACK = 0;

    @LangKey("config.orimod.spirit_arc.charge_duration")
    @RangeInt(min = 0)
    public int CHARGE_DURATION = 10;
  
    @LangKey("config.orimod.spirit_arc.velocity_multiplier")
    @RangeDouble(min = 0)
    public double VELOCITY_MULTIPLIER = 3;
  }

  @LangKey("config.orimod.light_burst")
  public static LightBurst LIGHT_BURST = new LightBurst();

  public static class LightBurst {
    @LangKey("config.orimod.light_burst.damage")
    @RangeDouble(min = 0)
    public double DAMAGE = 5;

    @LangKey("config.orimod.light_burst.charge_duration")
    @RangeInt(min = 0)
    public int CHARGE_DURATION = 15;

    @LangKey("config.orimod.light_burst.explosion_force")
    @RangeDouble(min = 0)
    public double EXPLOSION_FORCE = 3;
  }

  @LangKey("config.orimod.spirit_light")
  public static SpiritLight SPIRIT_LIGHT = new SpiritLight();

  public static class SpiritLight {
    @LangKey("config.orimod.spirit_light.drop_multiplier")
    @RangeDouble(min = 0)
    public double DROP_MULITPLIER = 0.25;

    @LangKey("config.orimod.spirit_light.death_penalty")
    @RangeDouble(min = 0, max = 1)
    public double DEATH_PENALTY = 0.5;

    @LangKey("config.orimod.spirit_light.death_drop")
    @RangeDouble(min = 0, max = 1)
    public double DEATH_DROP = 0.5;
  }

  @LangKey("config.orimod.jump_and_climb")
  public static JumpAndClimb JUMP_AND_CLIMB = new JumpAndClimb();

  public static class JumpAndClimb {
    @LangKey("config.orimod.jump_and_climb.wall_velocity_fraction")
    @RangeDouble(min = 0, max = 1)
    public double WALL_VELOCITY_FRACTION = 0.75;

    @LangKey("config.orimod.jump_and_climb.wall_fall_fraction")
    @RangeDouble(min = 0, max = 1)
    public double WALL_FALL_FRACTION = 0.75;

    @LangKey("config.orimod.jump_and_climb.wall_thresold")
    @RangeDouble(min = 0, max = 1)
    public double WALL_THRESOLD = 0.01;

    @LangKey("config.orimod.jump_and_climb.wall_jump_multiplier")
    @RangeDouble(min = 0)
    public double WALL_JUMP_MULTIPLIER = 1;

    @LangKey("config.orimod.jump_and_climb.multi_jump_multiplier")
    @RangeDouble(min = 0)
    public double MULTI_JUMP_MULTIPLIER = 1;

    @LangKey("config.orimod.jump_and_climb.climb_muliplier")
    @RangeDouble(min = 0)
    public double CLIMB_MULTIPLIER = 1;
  }

  @LangKey("config.orimod.stomp")
  public static Stomp STOMP = new Stomp();

  public static class Stomp {
    @LangKey("config.orimod.stomp.force")
    @RangeInt(min = 0)
    public int FORCE = 3;

    @LangKey("config.orimod.stomp.charge_time")
    @RangeInt(min = 0)
    public int CHARGE_TIME = 8;

    @LangKey("config.orimod.stomp.velocity")
    @RangeDouble(min = 0)
    public int VELOCITY = 1;
  }
}
