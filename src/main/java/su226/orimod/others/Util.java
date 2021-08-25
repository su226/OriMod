package su226.orimod.others;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Util {

  // ========== 基础数学 ==========

  /** 返回PI(180度)的mul倍，float */
  public static float randAngle(float mul) {
    return (float)(Math.random() * Math.PI) * mul;
  }

  /** 返回PI(180度)的mul倍，double */
  public static double randAngle(double mul) {
    return Math.random() * Math.PI * mul;
  }

  /** 返回min和max之间的随机数，float */
  public static float rand(float min, float max) {
    return ((float)Math.random()) * (max - min) + min;
  }

  /** 返回min和max之间的随机数，double */
  public static double rand(double min, double max) {
    return Math.random() * (max - min) + min;
  }

  /** 返回0和max之间的随机数，float */
  public static float rand(float max) {
    return ((float)Math.random()) * max;
  }

  /** 返回0和max之间的随机数，double */
  public static double rand(double max) {
    return Math.random() * max;
  }

  public static int ceil(double v) {
    int f = (int)v;
    return v % 1 > 0 ? f + 1 : f;
  }

  // ========== 命名空间 ==========

  /** 返回本mod命名空间内的Identifier */
  public static Identifier getIdentifier(String name) {
    return new Identifier("orimod", name);
  }

  // ========== 实体 ==========

  /** 返回ent的坐标，partialTicks为渲染用的“小数点”刻 */
  public static Vec3d pos(Entity ent, double partialTicks) {
    return new Vec3d(
      ent.prevX + (ent.getX() - ent.prevX) * partialTicks,
      ent.prevY + (ent.getY() - ent.prevY) * partialTicks,
      ent.prevZ + (ent.getZ() - ent.prevZ) * partialTicks
    );
  }


  /** 返回owner直视且未被方块遮挡的生物，找不到返回null，最大距离为dis */
  public static Entity rayTraceEntity(Entity owner, double dis, Predicate<Entity> predicate) {
    Vec3d start = owner.getCameraPosVec(1);
    Vec3d end = owner.getRotationVec(1).multiply(dis).add(start);
    BlockHitResult hit = owner.world.raycast(new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, owner));
    if (hit != null) {
      end = hit.getPos();
    }
    Box bb = new Box(start.x, start.y, start.z, end.x, end.y, end.z);
    List<Entity> entities = owner.world.getOtherEntities(owner, bb, predicate);
    double minDistance = end.distanceTo(start);
    Entity minEntity = null;
    for (Entity ent : entities) {
      Optional<Vec3d> intercept = ent.getBoundingBox().raycast(start, end);
      if (intercept.isPresent()) {
        double distance = intercept.get().distanceTo(start);
        if (distance < minDistance) {
          minDistance = distance;
          minEntity = ent;
        }
      }
    }
    return minEntity;
  }

  public static List<Entity> entityAroundLine(Entity owner, Vec3d start, Vec3d end, double range) {
    List<Entity> result = new ArrayList<>();
    BlockHitResult hit = owner.world.raycast(new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, owner));
    if (hit != null) {
      end = hit.getPos();
    }
    double minX = Math.min(start.x, end.x) - range;
    double minY = Math.min(start.y, end.y) - range;
    double minZ = Math.min(start.z, end.z) - range;
    double maxX = Math.max(start.x, end.x) + range;
    double maxY = Math.max(start.y, end.y) + range;
    double maxZ = Math.max(start.z, end.z) + range;
    Line line = new Segment(start, end);
    for (Entity ent : owner.world.getOtherEntities(owner, new Box(minX, minY, minZ, maxX, maxY, maxZ))) {
      if (line.distance(ent.getBoundingBox()) < range) {
        result.add(ent);
      }
    }
    return result;
  }

  private static boolean prevPressed;
  private static boolean prevFlying;

  @Environment(EnvType.CLIENT)
  public static boolean canAirJump(ClientPlayerEntity ent) {
    boolean ret = isAirBorne(ent) && ent.input.jumping && !prevPressed && !ent.abilities.flying && !prevFlying && !ent.isFallFlying();
    prevPressed = ent.input.jumping;
    prevFlying = ent.abilities.flying;
    return ret;
  }

  public static boolean isAirBorne(LivingEntity ent) {
    return !ent.isOnGround() && !ent.world.containsFluid(ent.getBoundingBox()) && !ent.isClimbing();
  }

  public static void setVelocity(Entity ent, double x, double y, double z, boolean setModified) {
    Vec3d velocity = ent.getVelocity();
    if (velocity.x != x || velocity.y != y || velocity.z != z) {
      ent.setVelocity(x, y, z);
      ent.velocityDirty = true;
      if (setModified) {
        ent.velocityModified = true;
      }
    }
  }

  // ========== 向量 ==========

  /** 直线 */
  public static class Line implements Comparable<Line> {
    public final Vec3d start;
    public final Vec3d end;

    public Line(Vec3d start, Vec3d end) {
      this.start = start;
      this.end = end;
    }

    public Line(double startX, double startY, double startZ, double endX, double endY, double endZ) {
      this.start = new Vec3d(startX, startY, startZ);
      this.end = new Vec3d(endX, endY, endZ);
    }

    /** 到点的最短距离 */
    public double distance(Vec3d point) {
      return point.distanceTo(this.closest(point));
    }

    /** 到线的最短距离 */
    public double distance(Line that) {
      Vec3d[] c = this.closest(that);
      return c[0].distanceTo(c[1]);
    }

    private class CubeDistance implements Comparable<CubeDistance> {
      public final Vec3d pos;
      public final Vec3d foot;
      public final double dist;

      public CubeDistance(double x, double y, double z) {
        this.pos = new Vec3d(x, y, z);
        this.foot = closest(this.pos);
        this.dist = this.foot.distanceTo(this.pos);
      }

      public boolean isAdjacent(CubeDistance o) {
        int count = 0;
        if (pos.x == o.pos.x) {
          count++;
        }
        if (pos.y == o.pos.y) {
          count++;
        }
        if (pos.z == o.pos.z) {
          count++;
        }
        return count > 1;
      }

      @Override
      public int compareTo(CubeDistance o) {
        return (int)Math.signum(dist - o.dist);
      }

      @Override
      public boolean equals(Object obj) {
        return obj instanceof CubeDistance && ((CubeDistance)obj).dist == dist;
      }
    }

    /** 到立方体的最短距离 */
    public double distance(Box box) {
      CubeDistance[] points = new CubeDistance[] {
        new CubeDistance(box.minX, box.minY, box.minZ),
        new CubeDistance(box.maxX, box.minY, box.minZ),
        new CubeDistance(box.minX, box.maxY, box.minZ),
        new CubeDistance(box.maxX, box.maxY, box.minZ),
        new CubeDistance(box.minX, box.minY, box.maxZ),
        new CubeDistance(box.maxX, box.minY, box.maxZ),
        new CubeDistance(box.minX, box.maxY, box.maxZ),
        new CubeDistance(box.maxX, box.maxY, box.maxZ),
      };
      Arrays.sort(points);
      int i = 1;
      if (points[0].equals(points[3])) {
        while (i < 3 && points[i].isAdjacent(points[0])) {
          i++;
        }
      } else if (points[0].equals(points[2])) {
        while (i < 2 && !points[i].isAdjacent(points[0])) {
          i++;
        }
      }
      return this.distance(new Segment(points[0].pos, points[i].pos));
    }

    /** 点到线的最短点 */
    public Vec3d closest(Vec3d point) {
      Vec3d delta = this.end.subtract(this.start).normalize();
      return this.correct(this.start.add(delta.multiply(point.subtract(this.start).dotProduct(delta))));
    }

    private static class LineClosest implements Comparable<LineClosest> {
      final Vec3d[] points;
      final double distance;

      public LineClosest(Vec3d p0, Vec3d p1) {
        this.points = new Vec3d[] { p0, p1 };
        this.distance = p0.distanceTo(p1);
      }

      @Override
      public int compareTo(LineClosest o) {
        return (int)Math.signum(this.distance - o.distance);
      }
    }

    /** 直线到直线的最短点，为长度等于2的数组，分别是两条线上的点 */
    public Vec3d[] closest(Line that) {
      Vec3d cross = this.end.subtract(this.start).crossProduct(that.end.subtract(that.start));
      Vec3d c0 = this.correct(that.intersect(that.normal(that.start.add(cross)), this));
      Vec3d c1 = that.correct(this.intersect(this.normal(this.start.add(cross)), that));
      Vec3d f0 = that.correct(that.closest(c0));
      Vec3d f1 = this.correct(this.closest(c1));
      LineClosest[] results = new LineClosest[] { new LineClosest(c0, c1), new LineClosest(c0, f0), new LineClosest(c1, f1) };
      Arrays.sort(results);
      return results[0].points;
    }

    /** 点线平面的法向量 */
    public Vec3d normal(Vec3d point) {
      return new Vec3d(
        (this.end.y - this.start.y) * (point.z - this.start.z) - (this.end.z - this.start.z) * (point.y - this.start.y),
        (this.end.z - this.start.z) * (point.x - this.start.x) - (this.end.x - this.start.x) * (point.z - this.start.z),
        (this.end.x - this.start.x) * (point.y - this.start.y) - (this.end.y - this.start.y) * (point.x - this.start.x)
      );
    }

    /** 点线平面与另一条线的交点，平行返回null */
    public Vec3d intersect(Vec3d normal, Line that) {
      Vec3d delta = that.end.subtract(that.start);
      double m = delta.x * normal.x + delta.y * normal.y + delta.z * normal.z;
      if (m == 0) {
        return null;
      } else {
        double n = ((this.start.x - that.start.x) * normal.x + (this.start.y - that.start.y) * normal.y + (this.start.z - that.start.z) * normal.z) / m;
        return new Vec3d(
          that.start.x + delta.x * n,
          that.start.y + delta.y * n,
          that.start.z + delta.z * n
        );
      }
    }

    /** 矫正错误的坐标 */
    protected Vec3d correct(Vec3d before) {
      return before;
    }

    /** 开始点到结束点的距离（也就是对应线段的长度） */
    public double length() {
      return this.end.distanceTo(this.start);
    }

    @Override
    public int compareTo(Line o) {
      return (int)Math.signum(this.length() - o.length());
    }
  }

  /** 线段 */
  public static class Segment extends Line {
    public Segment(Vec3d start, Vec3d end) {
      super(start, end);
    }

    public Segment(double startX, double startY, double startZ, double endX, double endY, double endZ) {
      super(startX, startY, startZ, endX, endY, endZ);
    }

    @Override
    protected Vec3d correct(Vec3d before) {
      double length = this.end.distanceTo(this.start);
      if (before.distanceTo(this.start) > length) {
        return this.end;
      } else if (before.distanceTo(this.end) > length) {
        return this.start;
      }
      return before;
    }
  }

  /** 返回向量的其中一个垂直向量 */
  public static Vec3d perpendicular(Vec3d src) {
    if (src.z == 0) {
      return new Vec3d(src.y, -src.x, 0);
    }
    return new Vec3d(0, src.z, -src.y);
  }

  /** 返回向量的其中一个垂直向量 */
  public static void perpendicular(Vec3f src) {
    if (src.getZ() == 0) {
      src.set(src.getY(), -src.getX(), 0);
    } else {
      src.set(0, src.getZ(), -src.getY());
    }
  }

  /** 绕axis向量旋转vec向量angle度，以弧度为单位 */
  public static Vec3d rotate(Vec3d axis, Vec3d vec, double angle) {
    // 从CodeChickenLib复制的代码，什么意思我也看不懂
    angle *= 0.5;
    double d4 = Math.sin(angle);
    double qs = Math.cos(angle);
    double qx = axis.x * d4;
    double qy = axis.y * d4;
    double qz = axis.z * d4;
    double d = -qx * vec.x - qy * vec.y - qz * vec.z;
    double d1 = qs * vec.x + qy * vec.z - qz * vec.y;
    double d2 = qs * vec.y - qx * vec.z + qz * vec.x;
    double d3 = qs * vec.z + qx * vec.y - qy * vec.x;
    return new Vec3d(
      d1 * qs - d * qx - d2 * qz + d3 * qy,
      d2 * qs - d * qy + d1 * qz - d3 * qx,
      d3 * qs - d * qz - d1 * qy + d2 * qx
    );
  }

  /** 绕axis向量旋转vec向量angle度，以弧度为单位 */
  public static void rotate(Vec3f axis, Vec3f vec, float angle) {
    // 从CodeChickenLib复制的代码，什么意思我也看不懂
    angle *= 0.5;
    float d4 = MathHelper.sin(angle);
    float qs = MathHelper.cos(angle);
    float qx = axis.getX() * d4;
    float qy = axis.getY() * d4;
    float qz = axis.getZ() * d4;
    float d = -qx * vec.getX() - qy * vec.getY() - qz * vec.getZ();
    float d1 = qs * vec.getX() + qy * vec.getZ() - qz * vec.getY();
    float d2 = qs * vec.getY() - qx * vec.getZ() + qz * vec.getX();
    float d3 = qs * vec.getZ() + qx * vec.getY() - qy * vec.getX();
    vec.set(
      d1 * qs - d * qx - d2 * qz + d3 * qy,
      d2 * qs - d * qy + d1 * qz - d3 * qx,
      d3 * qs - d * qz - d1 * qy + d2 * qx
    );
  }

  public static void playSound(Entity entity, SoundEvent sound) {
    playSound(entity, entity.getPos(), sound);
  }

  @Environment(EnvType.CLIENT)
  public static void playSound(Entity entity, Vec3d pos, SoundEvent sound) {
    MinecraftClient mc = MinecraftClient.getInstance();
    if (pos.squaredDistanceTo(mc.player.getPos()) > 18 * 18) {
      return;
    }
    SoundCategory category = SoundCategory.MASTER;
    if (entity instanceof PlayerEntity) {
      category = SoundCategory.PLAYERS;
    } else if (entity instanceof AnimalEntity) {
      category = SoundCategory.NEUTRAL;
    } else if (entity instanceof HostileEntity) {
      category = SoundCategory.HOSTILE;
    }
    mc.world.playSound(mc.player, pos.x, pos.y, pos.z, sound, category, 1, 1);
  }

  // ========== 向量 ==========

  public static Field getFleid(Class<?> clazz, String deobf, String obf) {
    try {
      return clazz.getDeclaredField(deobf);
    } catch (Exception e) {}
    try {
      return clazz.getDeclaredField(obf);
    } catch (Exception e) {}
    return null;
  }
}
