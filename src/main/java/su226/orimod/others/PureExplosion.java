package su226.orimod.others;

import net.minecraft.entity.Entity;
import net.minecraft.world.explosion.Explosion;

public class PureExplosion extends Explosion {
  public PureExplosion(Entity attacker, Entity owner, float force) {
    super(owner.world, owner, null, null, attacker.getX(), attacker.getY(), attacker.getZ(), force, false, DestructionType.NONE);
  }

  public void explode() {
    this.collectBlocksAndDamageEntities();
  }
}
