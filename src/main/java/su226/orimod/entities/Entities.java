package su226.orimod.entities;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.registry.Registry;
import su226.orimod.others.Util;

public class Entities {
  public static final EntityType<Arrow> ARROW = FabricEntityTypeBuilder.<Arrow>create(SpawnGroup.MISC, Arrow::new).dimensions(EntityDimensions.fixed(0.5f, 0.5f)).trackRangeChunks(4).trackedUpdateRate(20).build();
  public static final EntityType<LightBurstEntity> LIGHT_BURST = FabricEntityTypeBuilder.<LightBurstEntity>create(SpawnGroup.MISC, LightBurstEntity::new).dimensions(EntityDimensions.fixed(0.25f, 0.25f)).trackRangeChunks(4).trackedUpdateRate(10).build();
  public static final EntityType<SpiritLightOrb> SPIRIT_LIGHT_ORB = FabricEntityTypeBuilder.<SpiritLightOrb>create(SpawnGroup.MISC, SpiritLightOrb::new).dimensions(EntityDimensions.fixed(0.5f, 0.5f)).trackRangeChunks(6).trackedUpdateRate(20).build();

  public static void register() {
    Registry.register(Registry.ENTITY_TYPE, Util.getIdentifier("arrow"), ARROW);
    Registry.register(Registry.ENTITY_TYPE, Util.getIdentifier("light_burst"), LIGHT_BURST);
    Registry.register(Registry.ENTITY_TYPE, Util.getIdentifier("spirit_light"), SPIRIT_LIGHT_ORB);
  }

  public static void registerRender() {
    EntityRendererRegistry.INSTANCE.register(ARROW, Arrow.Render::new);
    EntityRendererRegistry.INSTANCE.register(LIGHT_BURST, LightBurstEntity.Render::new);
    EntityRendererRegistry.INSTANCE.register(SPIRIT_LIGHT_ORB, SpiritLightOrb.Render::new);
  }
}
