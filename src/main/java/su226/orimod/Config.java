package su226.orimod;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@me.shedaniel.autoconfig.annotation.Config(name = "orimod")
public class Config implements ConfigData {
  @Tooltip
  @Comment("Enable full-3d model for supported items")
  public boolean enable_3d = true;
  @Tooltip
  @Comment("Edge glow color for 3d models, 32-bit number in ARGB order")
  public int glow_color = 0x8899ccff;
  @Tooltip
  @Comment("Max interval for double click, in ticks.")
  public int double_click_max_interval = 5;

  @Tooltip
  @CollapsibleObject
  @Comment("Config for tools, affect on all vanilla-like tools")
  public Tools tools = new Tools();
  public static class Tools {
    @Tooltip
    public int harvest_level = 4;
    @Tooltip
    public float efficiency = 12;
    @Tooltip
    public float damage = 4;
    @Tooltip
    public int enchantability = 22;
  }

  @Tooltip
  @CollapsibleObject
  @Comment("Harvest level for tools, Wood/Gold: 0, Stone: 1, Iron: 2, Diamond: 3, Cobalt (from Tinker's Construct): 4")
  public SpiritFlame spirit_flame = new SpiritFlame();
  public static class SpiritFlame {
    @Tooltip
    @Comment("Efficiency for tools, Wood: 2, Stone: 4, Iron: 6, Diamond: 8, Gold: 12")
    public double radius = 5;
    @Tooltip
    @Comment("Base damage for tools, +4 for sword damage, Wood/Gold: 0, Stone: 1, Iron: 2, Diamond: 3")
    public int targets = 1;
    @Tooltip
    @Comment("Enchantability for tools, higher for easier enchanting, Wood: 15, Stone: 5, Iron: 14, Diamond: 10, Gold: 22")
    public float damage = 6;
    @Tooltip
    @Comment("Config for spirit flame")
    public boolean ignore_pets = true;
    @Tooltip
    @Comment("Radius for spirit flame, in blocks")
    public float explosion_force = 4;
    @Tooltip
    @Comment("How many targets can spirit flame attack at the same time")
    public int charge_duration = 30;
  }

  @Tooltip
  @CollapsibleObject
  @Comment("Damage dealt from spirit flame")
  public Bash bash = new Bash();
  public static class Bash {
    @Tooltip
    @Comment("Whether spirit flame should ignore own pets")
    public double multiplier = 1;
    @Tooltip
    @Comment("Explosion force for charge flame")
    public double range = 3;
    @Tooltip
    @Comment("Charge duration for charge flame, in ticks")
    public int timeout = 40;
  }

  @Tooltip
  @CollapsibleObject
  @Comment("Config for bash")
  public KurosFeather kuros_feather = new KurosFeather();
  public static class KurosFeather {
    @Tooltip
    @Comment("Velocity multiplier for bash, affect on both player and target entity")
    public double fall_multiplier = 0.8;
    @Tooltip
    @Comment("How far can bash be acted on, in blocks")
    public double speed_compensation = 0.1f;
    @Tooltip
    @Comment("Max time for bash process, in ticks")
    public double length = 5;
    @Tooltip
    @Comment("Config for kuro's feather")
    public double range = 1;
    @Tooltip
    @Comment("Falling speed multiplier for kuro's feather gliding")
    public double force = 1;
    @Tooltip
    @Comment("Gliding speed compensation for kuro's feather gliding, because sneaking can decrease speed")
    public int cooldown = 20;
  }

  @Tooltip
  @CollapsibleObject
  @Comment("Config for spirit arc")
  public SpiritArc spirit_arc = new SpiritArc();
  public static class SpiritArc {
    @Tooltip
    @Comment("Base damage for spirit arc, when velocity multiplier is 3, max damage will be about 4 times of base damage")
    public double base_damage = 2;
    @Tooltip
    @Comment("Base knockback for spirit arc without enchantments")
    public int knockback = 0;
    @Tooltip
    @Comment("Charge duration for spirit arc, in ticks")
    public int charge_duration = 10;
    @Tooltip
    @Comment("Velocity Multiplier for spirit arc, affect damage along with base damage")
    public float velocity_multiplier = 3;
  }

  @Tooltip
  @CollapsibleObject
  @Comment("Config for light burst")
  public LightBurst light_burst = new LightBurst();
  public static class LightBurst {
    @Tooltip
    @Comment("Damage to DIRECT hit entity for light burst")
    public float damage = 5;
    @Tooltip
    @Comment("Charge duration for light burst, in ticks")
    public int charge_duration = 15;
    @Tooltip
    @Comment("Explosion force for light burst")
    public float explosion_force = 3;
  }

  @Tooltip
  @CollapsibleObject
  @Comment("Config for spirit light")
  public SpiritLight spirit_light = new SpiritLight();
  public static class SpiritLight {
    @Tooltip
    @Comment("Drop multiplier for spirit light, multiply by max health for drop count.")
    public double drop_multiplier = 0.25;
    @Tooltip
    @Comment("Keep ratio for spirit light when player dies.")
    public double death_penalty = 0.5;
    @Tooltip
    @Comment("Drop ratio for the not keeping part of spirit light, others will be disappeared.")
    public double death_drop = 0.5;
  }

  @Tooltip
  @CollapsibleObject
  @Comment("Config for multi jumping, wall jumping and climbing.")
  public JumpAndClimb jump_and_climb = new JumpAndClimb();
  public static class JumpAndClimb {
    @Tooltip
    @Comment("Velocity fraction for wall jump when clinging to wall.")
    public double wall_velocity_fraction = 0.75;
    @Tooltip
    @Comment("Fall distance fraction for wall jump when clinging to wall.")
    public double wall_fall_fraction = 0.75;
    @Tooltip
    @Comment("Max distance to wall when judged as climbing")
    public double wall_threshold = 0.01;
    @Tooltip
    @Comment("Jump multiplier for wall jump.")
    public double wall_jump_multiplier = 1;
    @Tooltip
    @Comment("Jump multiplier for multi jump.")
    public double multi_jump_multiplier = 1;
    @Tooltip
    @Comment("Velocity multiplier for climbing.")
    public double climb_multiplier = 1;
  }

  @Tooltip
  @CollapsibleObject
  @Comment("Config for stomp")
  public Stomp stomp = new Stomp();
  public static class Stomp {
    @Tooltip
    @Comment("Blast froce for stomp.")
    public int force = 3;
    @Tooltip
    @Comment("Charge time for stomp in ticks.")
    public int charge_time = 8;
    @Tooltip
    @Comment("Velocity for stomp.")
    public int velocity = 1;
  }
}
