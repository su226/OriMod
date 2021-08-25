package su226.orimod.packets;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import su226.orimod.Mod;
import su226.orimod.components.Components;

public class MultiJumpPacket extends Packet {
  public static class Handler extends Packet.ServerHandler<MultiJumpPacket> {
    @Override
    public void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, MultiJumpPacket packet, PacketSender responseSender) {
      server.execute(() -> {
        if (Components.COOLDOWN.get(player).doAction("multi_jump")) {
          player.jump();
          player.setVelocity(player.getVelocity().multiply(1, Mod.CONFIG.jump_and_climb.multi_jump_multiplier, 1));
          player.fallDistance = 0;
          player.velocityModified = true;
          new MultiJumpEffectPacket(player).sendToAround(player, 32);
        }
      });
    }
  }

  public MultiJumpPacket() {}

  @Override
  public String getName() {
    return "multi_jump";
  }

  @Override
  public void serialize(PacketByteBuf buf) {}

  @Override
  public void deserialize(PacketByteBuf buf) {}
}
