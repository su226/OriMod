package su226.orimod;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import su226.orimod.blocks.Blocks;
import su226.orimod.commands.Commands;
import su226.orimod.entities.Entities;
import su226.orimod.items.Items;
import su226.orimod.others.Interfaces;
import su226.orimod.others.Sounds;
import su226.orimod.others.Stats;
import su226.orimod.others.Trinkets;
import su226.orimod.packets.Packets;

public class Mod implements ModInitializer {
	public static final Logger LOG = LogManager.getLogger("OriMod");
	public static Config CONFIG;
	public static int tick;

	@Override
	public void onInitialize() {
		AutoConfig.register(Config.class, JanksonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(Config.class).getConfig();
		Blocks.register();
		Entities.register();
		Interfaces.register();
		Items.register();
		Packets.registerAllOnServer();
		Sounds.register();
		Stats.register();
		Trinkets.register();
		ServerTickEvents.START_SERVER_TICK.register(e -> tick++);
		CommandRegistrationCallback.EVENT.register(Commands::register);
	}
}
