package su226.orimod.packets;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import su226.orimod.others.Util;

public class Packets {
  private interface Factory<T> {
    T create();
  }

  public static <P extends Packet, H extends Packet.ClientHandler<? super P>> void registerOnClient(Factory<P> factory, H packetHandler) {
    Identifier identifier = Util.getIdentifier(factory.create().getName());
    ClientPlayNetworking.registerGlobalReceiver(identifier, (client, handler, buf, responseSender) -> {
      P packet = factory.create();
      packet.deserialize(buf);
      packetHandler.handle(client, handler, packet, responseSender);
    });
  }

  public static <P extends Packet, H extends Packet.ServerHandler<? super P>> void registerOnServer(Factory<P> factory, H packetHandler) {
    Identifier identifier = Util.getIdentifier(factory.create().getName());
    ServerPlayNetworking.registerGlobalReceiver(identifier, (server, player, handler, buf, responseSender) -> {
      P packet = factory.create();
      packet.deserialize(buf);
      packetHandler.handle(server, player, handler, packet, responseSender);
    });
  }

  public static void registerAllOnClient() {
    registerOnClient(ChargeFlamePacket::new, new ChargeFlamePacket.Handler());
    registerOnClient(DebugPacket::new, new DebugPacket.Handler());
    registerOnClient(FlapPacket::new, new FlapPacket.Handler());
    registerOnClient(LightBurstPacket::new, new ProjectileHitPacket.Handler());
    registerOnClient(MultiJumpEffectPacket::new, new MultiJumpEffectPacket.Handler());
    registerOnClient(SpiritArcPacket::new, new ProjectileHitPacket.Handler());
    registerOnClient(SpiritFlamePacket::new, new SpiritFlamePacket.Handler());
    registerOnClient(SpiritLightSyncPacket::new, new SpiritLightSyncPacket.Handler());
    registerOnClient(StompEffectPacket::new, new StompEffectPacket.Handler());
  }

  public static void registerAllOnServer() {
    registerOnServer(MultiJumpPacket::new, new MultiJumpPacket.Handler());
    registerOnServer(StompPacket::new, new StompPacket.Handler());
    registerOnServer(WallJumpPacket::new, new WallJumpPacket.Handler());
  }
}
