package su226.orimod.others;

import net.minecraft.entity.Entity;
import net.minecraft.world.Explosion;

public class PureExplosion extends Explosion {
  public PureExplosion(Entity ent, double force) {
    super(ent.world, ent, ent.posX, ent.posY, ent.posZ, (float)force, false, false);
  }

  public PureExplosion(Entity ent, Entity owner, double force) {
    super(ent.world, owner, ent.posX, ent.posY, ent.posZ, (float)force, false, false);
  }
}
