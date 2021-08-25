package su226.orimod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import su226.orimod.components.ISpiritLight;
import su226.orimod.entities.Entities;
import su226.orimod.items.Items;
import su226.orimod.items.shards.Shard;
import su226.orimod.others.Interfaces;
import su226.orimod.packets.Packets;
import su226.orimod.particles.ChargeFlameParticle;
import su226.orimod.particles.SpiritArcParticle;

public class ModClient implements ClientModInitializer {
  public static int tick;
  public static SpriteAtlasTexture PARTICLE_ATLAS;
  public static final Identifier BLOCK_ATLAS_TEXTURE = PlayerScreenHandler.BLOCK_ATLAS_TEXTURE;
  @SuppressWarnings("deprecation")
  public static final Identifier PARTICLE_ATLAS_TEXTURE = SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE;

  @Override
  public void onInitializeClient() {
    ClientTickEvents.START_CLIENT_TICK.register(e -> tick++);
    ClientSpriteRegistryCallback.event(BLOCK_ATLAS_TEXTURE).register((atlas, registry) -> {
      for (Identifier texture : Shard.TEXTURES) {
        registry.register(texture);
      }
    });
    ClientSpriteRegistryCallback.event(PARTICLE_ATLAS_TEXTURE).register((atlas, registry) -> {
      PARTICLE_ATLAS = atlas;
      registry.register(ChargeFlameParticle.TEXTURE);
      registry.register(SpiritArcParticle.TEXTURE);
    });
    HudRenderCallback.EVENT.register(ISpiritLight::renderHud);
    Entities.registerRender();
    Interfaces.registerScreen();
    Items.registerModelPredicate();
    Packets.registerAllOnClient();
  }
}
