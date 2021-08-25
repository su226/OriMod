package su226.orimod.packets;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Box;
import su226.orimod.Mod;
import su226.orimod.others.Sounds;

public class WallJumpPacket extends Packet {
  public static class Handler extends Packet.ServerHandler<WallJumpPacket> {
    @Override
    public void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, WallJumpPacket packet, PacketSender responseSender) {
      server.execute(() -> {
        Box cling = player.getBoundingBox().expand(Mod.CONFIG.jump_and_climb.wall_threshold, 0, Mod.CONFIG.jump_and_climb.wall_threshold);
        if (!player.world.isSpaceEmpty(cling)) {
          player.jump();
          player.setVelocity(player.getVelocity().multiply(1, Mod.CONFIG.jump_and_climb.wall_jump_multiplier, 1));
          player.fallDistance = 0;
          player.velocityModified = true;
          player.world.playSound(null, player.getX(), player.getY(), player.getZ(), Sounds.WALL_JUMP, SoundCategory.PLAYERS, 1, 1);
        }
      });
    }
  }

  @Override
  public String getName() {
    return "wall_jump";
  }

  @Override
  public void deserialize(PacketByteBuf buf) {}

  @Override
  public void serialize(PacketByteBuf buf) {}
}
