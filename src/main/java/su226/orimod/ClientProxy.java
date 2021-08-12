package su226.orimod;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import su226.orimod.capabilities.IChargeable;
import su226.orimod.entities.Entities;
import su226.orimod.items.Items;
import su226.orimod.others.Models;
import su226.orimod.others.Util;

@EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {
  public static int tick = 0;

  @Override
  public void preInit() {
    super.preInit();
    Models.initModels();
    Entities.registerRender();
  }

  @Override
  public void init() {
    super.init();
  }

  @Override
  public void postInit() {
    super.postInit();
  }

  @SubscribeEvent
  public static void setModels(ModelRegistryEvent event) {
    Models.setModels();
  }

  @SubscribeEvent
  public static void loadModels(ModelBakeEvent event) {
    Models.loadModels();
  }

  @SubscribeEvent
  public static void registerTexture(TextureStitchEvent.Pre event) {
    Models.setTexture(Minecraft.getMinecraft().getTextureMapBlocks());
  }

  @SubscribeEvent
  public static void attachItemClientCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
    if (event.getObject().getItem() == Items.SPIRIT_FLAME) {
      event.addCapability(Util.getLocation("chargeable"), new IChargeable.Provider());
    } else if (event.getObject().getItem() == Items.SPIRIT_ARC) {
      event.addCapability(Util.getLocation("chargeable"), new IChargeable.Provider());
    } else if (event.getObject().getItem() == Items.LIGHT_BURST) {
      event.addCapability(Util.getLocation("chargeable"), new IChargeable.Provider());
    }
  }

  @SubscribeEvent
	public static void renderPlayerPre(RenderPlayerEvent.Pre event) {
    Items.KUROS_FEATHER.renderPlayerPre(event.getEntityPlayer(), event.getRenderer());
	}

  @SubscribeEvent
  public static void clientTick(ClientTickEvent event) {
    tick++;
  }
}
