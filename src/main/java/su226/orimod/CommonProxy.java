package su226.orimod;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import su226.orimod.blocks.Blocks;
import su226.orimod.capabilities.Capabilities;
import su226.orimod.capabilities.ICooldown;
import su226.orimod.capabilities.IEquipper;
import su226.orimod.capabilities.IHasEntity;
import su226.orimod.capabilities.IKurosFeather;
import su226.orimod.capabilities.ISpiritLight;
import su226.orimod.entities.Entities;
import su226.orimod.items.Items;
import su226.orimod.messages.Messages;
import su226.orimod.others.GuiHandler;
import su226.orimod.others.Sounds;
import su226.orimod.others.Util;

@EventBusSubscriber
public class CommonProxy {
  public void preInit() {
    Capabilities.register();
    Messages.register();
    GuiHandler.register();
  }
  
  public void init() {}

  public void postInit() {}

  @SubscribeEvent
  public static void registerBlocks(Register<Block> event) {
    Blocks.register(event.getRegistry());
  }

  @SubscribeEvent
  public static void registerItems(Register<Item> event) {
    Items.register(event.getRegistry());
  }

  @SubscribeEvent
  public static void registerEntities(Register<EntityEntry> event) {
    Entities.register(event.getRegistry());
  }

  @SubscribeEvent
  public static void registerSounds(Register<SoundEvent> event) {
    Sounds.register(event.getRegistry());
  }

  @SubscribeEvent
  public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
    if (event.getObject() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer)event.getObject();
      event.addCapability(Util.getLocation("equiper"), new IEquipper.Provider());
      event.addCapability(Util.getLocation("kuros_feather"), new IKurosFeather.Provider());
      event.addCapability(Util.getLocation("cooldown"), new ICooldown.Provider(player));
      event.addCapability(Util.getLocation("spirit_light"), new ISpiritLight.Provider(player));
    }
  }

  @SubscribeEvent
  public static void attachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
    if (event.getObject().getItem() == Items.BASH) {
      event.addCapability(Util.getLocation("has_entity"), new IHasEntity.Provider());
    }
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public static void playerTick(PlayerTickEvent event) {
    IEquipper.playerTick(event.player);
    ISpiritLight.playerTick(event.player);
  }

  @SubscribeEvent
  public static void livingAttack(LivingAttackEvent event) {
    Items.KUROS_FEATHER.livingAttack(event);
    Items.STOMP.livingAttack(event);
  }

  @SubscribeEvent
  public static void livingDeath(LivingDeathEvent event) {
    ISpiritLight.livingDeath(event);
  }

  @SubscribeEvent
  public static void playerClone(PlayerEvent.Clone event) {
    ISpiritLight.playerClone(event);
  }

  @SubscribeEvent
  public static void onConfigChanged(OnConfigChangedEvent event) {
    if (event.getModID().equals(Mod.MODID)) {
      ConfigManager.sync(Mod.MODID, net.minecraftforge.common.config.Config.Type.INSTANCE);
    }
  }
}
