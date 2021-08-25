package su226.orimod.components;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import su226.orimod.items.Items;
import su226.orimod.others.Util;

public class Components implements ItemComponentInitializer, EntityComponentInitializer {
  public static final ComponentKey<IBash> BASH = ComponentRegistryV3.INSTANCE.getOrCreate(Util.getIdentifier("bash"), IBash.class);
  public static final ComponentKey<IChargeable> CHARGEABLE = ComponentRegistryV3.INSTANCE.getOrCreate(Util.getIdentifier("chargeable"), IChargeable.class);
  public static final ComponentKey<ICooldown> COOLDOWN = ComponentRegistryV3.INSTANCE.getOrCreate(Util.getIdentifier("cooldown"), ICooldown.class);
  public static final ComponentKey<IKurosFeather> KUROS_FEATHER = ComponentRegistryV3.INSTANCE.getOrCreate(Util.getIdentifier("kuros_feather"), IKurosFeather.class);
  public static final ComponentKey<ISpiritLight> SPIRIT_LIGHT = ComponentRegistryV3.INSTANCE.getOrCreate(Util.getIdentifier("spirit_light"), ISpiritLight.class);

  @Override
  public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
    registry.register(Items.BASH, BASH, IBash.Item::new);
    registry.register(Items.LIGHT_BURST, CHARGEABLE, IChargeable.Item::new);
    registry.register(Items.SPIRIT_ARC, CHARGEABLE, IChargeable.Item::new);
    registry.register(Items.SPIRIT_FLAME, CHARGEABLE, IChargeable.Item::new);
  }

  @Override
  public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
    registry.registerForPlayers(COOLDOWN, ICooldown.Player::new);
    registry.registerForPlayers(KUROS_FEATHER, IKurosFeather.Player::new);
    registry.registerForPlayers(SPIRIT_LIGHT, ISpiritLight.Player::new);
  }
}
