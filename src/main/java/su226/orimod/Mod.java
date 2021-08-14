package su226.orimod;

import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import su226.orimod.commands.Commands;

import org.apache.logging.log4j.Logger;

@net.minecraftforge.fml.common.Mod(modid = Mod.MODID, name = Mod.NAME, version = Mod.VERSION)
public class Mod {
  public static final String MODID = "orimod";
  public static final String NAME = "OriMod";
  public static final String VERSION = "1.0.0";

  public static Logger LOG;
  @SidedProxy(clientSide = "su226.orimod.ClientProxy", serverSide = "su226.orimod.CommonProxy")
  public static CommonProxy PROXY;
  @net.minecraftforge.fml.common.Mod.Instance(Mod.MODID)
  public static Mod INSTANCE;
  public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(Mod.MODID);

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    LOG = event.getModLog();
    PROXY.preInit();
  }

  @EventHandler
  public void init(FMLInitializationEvent event) {
    PROXY.init();
  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent event) {
    PROXY.postInit();
  }
  
  @EventHandler
  public static void onServerStarting(FMLServerStartingEvent event) {
    Commands.register(event);
  }
}
