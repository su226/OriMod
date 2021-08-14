package su226.orimod.others;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Predicate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IRegistryDelegate;
import su226.orimod.Mod;

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

  /** 返回本mod命名空间内的ResourceLocation */
  public static ResourceLocation getLocation(String name) {
    return new ResourceLocation(Mod.MODID, name);
  }

  /** 返回本mod命名空间内的i18n key */
  public static String getI18nKey(String name) {
    return Mod.MODID + "." + name;
  }

  // ========== 实体 ==========

  /** 返回ent的坐标，partialTicks为渲染用的“小数点”刻 */
  public static Vec3d pos(Entity ent, double partialTicks) {
    return new Vec3d(
      ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * partialTicks,
      ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * partialTicks,
      ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * partialTicks
    );
  }

  /** 返回owner直视且未被方块遮挡的生物，找不到返回null，最大距离为dis */
  public static Entity rayTraceEntity(Entity owner, double dis) {
    Vec3d start = owner.getPositionEyes(1);
    Vec3d end = owner.getLookVec().scale(dis).add(start);
    return rayTraceEntity(owner.world, start, end, e -> e != owner);
  }

  public static Entity rayTraceEntity(World world, Vec3d start, Vec3d end, Predicate<Entity> predicate) {
    AxisAlignedBB bb = new AxisAlignedBB(start.x, start.y, start.z, end.x, end.y, end.z);
    List<Entity> entities = world.getEntitiesInAABBexcluding(null, bb, predicate);
    RayTraceResult hit = world.rayTraceBlocks(start, end, false, true, false);
    double minDistance = hit != null ? hit.hitVec.distanceTo(start) : end.distanceTo(start);
    Entity minEntity = null;
    for (Entity ent : entities) {    
      RayTraceResult intercept = ent.getEntityBoundingBox().calculateIntercept(start, end);
      if (intercept != null) {
        double distance = intercept.hitVec.distanceTo(start);
        if (distance < minDistance) {
          minDistance = distance;
          minEntity = ent;
        }
      }
    }
    return minEntity;
  }

  public static List<Entity> entityAroundLine(World world, Vec3d start, Vec3d end, double range, Entity exclude) {
    List<Entity> result = new ArrayList<>();
    RayTraceResult hit = world.rayTraceBlocks(start, end, false, true, false);
    if (hit != null) {
      end = hit.hitVec;
    }
    double minX = Math.min(start.x, end.x) - range;
    double minY = Math.min(start.y, end.y) - range;
    double minZ = Math.min(start.z, end.z) - range;
    double maxX = Math.max(start.x, end.x) + range;
    double maxY = Math.max(start.y, end.y) + range;
    double maxZ = Math.max(start.z, end.z) + range;
    Line line = new Segment(start, end);
    for (Entity ent : world.getEntitiesWithinAABBExcludingEntity(exclude, new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ))) {
      if (line.distance(ent.getEntityBoundingBox()) < range) {
        result.add(ent);
      }
    }
    return result;
  }

  private static boolean prevPressed;
  private static boolean prevFlying;
  
  @SideOnly(Side.CLIENT)
  public static boolean canAirJump(EntityPlayerSP ent) {
    boolean ret = isAirBorne(ent) && ent.movementInput.jump && !prevPressed && !ent.capabilities.isFlying && !prevFlying && !ent.isElytraFlying();
    prevPressed = ent.movementInput.jump;
    prevFlying = ent.capabilities.isFlying;
    return ret;
  }

  public static boolean isAirBorne(EntityLivingBase ent) {
    return !ent.onGround && !ent.world.containsAnyLiquid(ent.getEntityBoundingBox()) && !ent.isOnLadder();
  }

  // ========== 向量 ==========

  /** 直线 */
  public static class Line implements Comparable<Line> {
    public Vec3d start;
    public Vec3d end;

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
      public Vec3d pos;
      public Vec3d foot;
      public double dist;
  
      public CubeDistance(double x, double y, double z) {
        this.pos = new Vec3d(x, y, z);
        this.foot = closest(this.pos);
        this.dist = this.foot.distanceTo(this.pos);
      }
  
      public boolean isAdjancent(CubeDistance o) {
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
    public double distance(AxisAlignedBB box) {
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
        for (; i < 3 && points[i].isAdjancent(points[0]); i++) {}
      } else if (points[0].equals(points[2])) {
        for (; i < 2 && !points[i].isAdjancent(points[0]); i++) {}
      }
      return this.distance(new Segment(points[0].pos, points[i].pos));
    }

    /** 点到线的最短点 */
    public Vec3d closest(Vec3d point) {
      Vec3d delta = this.end.subtract(this.start).normalize();
      return this.correct(this.start.add(delta.scale(point.subtract(this.start).dotProduct(delta))));
    }

    private static class LineClosest implements Comparable<LineClosest> {
      Vec3d[] points;
      double distance;

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
  
  // ========== 声音 ==========

  public static void playSound(Entity entity, SoundEvent sound) {
    playSound(entity, entity.getPosition(), sound);
  }

  public static void playSound(Entity entity, Vec3d pos, SoundEvent sound) {
    playSound(entity, new BlockPos(pos), sound);
  }

  @SideOnly(Side.CLIENT)
  public static void playSound(Entity entity, BlockPos pos, SoundEvent sound) {
    Minecraft mc = Minecraft.getMinecraft();
    SoundCategory category = SoundCategory.MASTER;
    if (entity instanceof EntityPlayer) {
      category = SoundCategory.PLAYERS;
    } else if (entity instanceof EntityAnimal) {
      category = SoundCategory.NEUTRAL;
    } else if (entity instanceof EntityMob) {
      category = SoundCategory.HOSTILE;
    }
    entity.world.playSound(mc.player, pos, sound, category, 1, 1);
  }

  // ========== 物品 ==========
  
  @SuppressWarnings("unchecked")
  public static Item getItem(ItemStack stack) {
    try {
      Field field = ItemStack.class.getDeclaredField("delegate");
      field.setAccessible(true);
      IRegistryDelegate<Item> delegate = (IRegistryDelegate<Item>)field.get(stack);
      if (delegate == null) {
        Mod.LOG.warn("Item registry delegate is null!");
        return Items.AIR;
      }
      return delegate.get();
    } catch (Exception e) {
      Mod.LOG.warn("Failed to get internal item!", e);
      return stack.getItem();
    }
  }
}
