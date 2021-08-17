package su226.orimod.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import su226.orimod.ClientProxy;
import su226.orimod.Mod;
import su226.orimod.capabilities.Capabilities;
import su226.orimod.entities.SpiritLightOrb;
import su226.orimod.items.Items;
import su226.orimod.others.GuiHandler;
import su226.orimod.others.Models;
import su226.orimod.others.Util;

public class SpiritSmithingTable extends Block {
  public static class Container extends net.minecraft.inventory.Container {
    private ItemStackHandler items = new ItemStackHandler(2);
    private BlockPos pos;
    private EntityPlayer player;
    public Recipe recipe;

    protected Slot inputSlot = new SlotItemHandler(items, 0, 27, 47) {
      public int getSlotStackLimit() {
        return 1;
      }

      public void onSlotChanged() {
        ItemStack stack = this.getStack();
        recipe = findRecipe(stack);
        if (recipe != null && player.getCapability(Capabilities.SPIRIT_LIGHT, null).get() >= recipe.getCost()) {
          outputSlot.putStack(recipe.getOutput(stack));
        } else {
          outputSlot.putStack(ItemStack.EMPTY);
        }
      }
    };
    protected Slot outputSlot = new SlotItemHandler(items, 1, 134, 47) {
      public boolean isItemValid(ItemStack stack) {
        return false;
      }

      public ItemStack onTake(EntityPlayer player, ItemStack stack) {
        if (!player.world.isRemote) {
          player.getCapability(Capabilities.SPIRIT_LIGHT, null).cost(recipe.getCost());
        }
        inputSlot.putStack(ItemStack.EMPTY);
        return stack;
      }
    };

    public Container(EntityPlayer player, BlockPos pos) {
      super();
      this.player = player;
      this.pos = pos;
      this.addSlotToContainer(inputSlot);
      this.addSlotToContainer(outputSlot);

      for (int y = 0; y < 3; ++y) {
        for (int x = 0; x < 9; ++x) {
          this.addSlotToContainer(new Slot(player.inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
        }
      }

      for (int x = 0; x < 9; ++x) {
        this.addSlotToContainer(new Slot(player.inventory, x, 8 + x * 18, 142));
      }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
      if (player.world.getBlockState(this.pos).getBlock() == Blocks.SPIRIT_SMITHING_TABLE) {
        return player.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64;
      }
      return false;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
        ItemStack newStack = slot.getStack();
        ItemStack oldStack = newStack.copy();
        if (index < 2) {
          if (!this.mergeItemStack(newStack, 2, 38, true)) {
            return ItemStack.EMPTY;
          }
        } else {
          if (!this.mergeItemStack(newStack, 0, 1, false)) {
            return ItemStack.EMPTY;
          }
        }
        if (newStack.isEmpty()) {
          slot.putStack(ItemStack.EMPTY);
        } else {
          slot.onSlotChanged();
        }
        if (newStack.getCount() == oldStack.getCount()) {
          return ItemStack.EMPTY;
        }
        slot.onTake(player, newStack);
        return oldStack;
      }
      return ItemStack.EMPTY;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
      super.onContainerClosed(player);
      if (!player.world.isRemote) {
        if (this.inputSlot.getHasStack()) {
          player.dropItem(this.inputSlot.getStack(), false);
          this.inputSlot.putStack(ItemStack.EMPTY);
        }
      }
    }
  }

  public static class Gui extends GuiContainer {
    public static final ResourceLocation TEXTURE = Util.getLocation("textures/gui/spirit_smithing_table.png");

    public Gui(net.minecraft.inventory.Container container) {
      super(container);
      this.xSize = 176;
      this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int x, int y) {
      this.drawDefaultBackground();
      GlStateManager.color(1, 1, 1);

      int offsetX = (this.width - this.xSize) / 2;
      int offsetY = (this.height - this.ySize) / 2;

      this.mc.getTextureManager().bindTexture(TEXTURE);
      this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);

      double rot = (ClientProxy.tick + partialTicks) * 0.05;
      GlStateManager.enableBlend();
      Models.drawSquare(
        new Vec3d(offsetX + 84 + Math.cos(rot + Math.PI / 2) * 16, offsetY + 55 + Math.sin(rot + Math.PI / 2) * 16, 0),
        new Vec3d(offsetX + 84 + Math.cos(rot) * 16, offsetY + 55 + Math.sin(rot) * 16, 0),
        new Vec3d(offsetX + 84 + Math.cos(rot - Math.PI / 2) * 16, offsetY + 55 + Math.sin(rot - Math.PI / 2) * 16, 0),
      SpiritLightOrb.Render.TEXTURE);
      GlStateManager.disableBlend();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
      this.fontRenderer.drawString(I18n.format("gui.orimod.spirit_smithing_table"), 51, 18, 0x404040);
      Container container = (Container)this.inventorySlots;
      int value = Minecraft.getMinecraft().player.getCapability(Capabilities.SPIRIT_LIGHT, null).get();
      String str = container.recipe == null ? Integer.toString(value) : String.format("%d/%d", value, container.recipe.getCost());
      this.fontRenderer.drawString(str, 84 - this.fontRenderer.getStringWidth(str) / 2, 64, 0x404040);
      if (container.recipe != null && value < container.recipe.getCost()) {
        GlStateManager.color(1, 1, 1);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        this.drawTexturedModalRect(99, 45, 176, 0, 32, 32);
      }
    }

    @Override
    public void drawScreen(int x, int y, float partialTicks) {
      super.drawScreen(x, y, partialTicks);
      this.renderHoveredToolTip(x, y);
    }
  }

  public static class Recipe {
    private ItemStack input;
    private ItemStack output;
    private int cost;

    public Recipe(ItemStack input, ItemStack output, int cost) {
      this.input = input;
      this.output = output;
      this.cost = cost;
    }

    public boolean match(ItemStack input) {
      return input.isItemEqualIgnoreDurability(this.input);
    }

    public int getCost() {
      return this.cost;
    }

    public ItemStack getOutput(ItemStack input) {
      ItemStack ret = this.output.copy();
      NBTTagCompound tag = input.getTagCompound();
      if (tag != null) {
        ret.setTagCompound(tag.copy());
      }
      return ret;
    }

    public ItemStack getInput() {
      return this.input;
    }

    public ItemStack getOutput() {
      return this.output;
    }
  }

  public static List<Recipe> RECIPES = new ArrayList<>();
  private static int GUI_ID;

  public SpiritSmithingTable() {
    super(Material.WOOD);
    this.setCreativeTab(Items.CREATIVE_TAB);
    this.setRegistryName(Util.getLocation("spirit_smithing_table"));
    this.setUnlocalizedName(Util.getI18nKey("spirit_smithing_table"));
    this.setHardness(2.5f);
    this.setSoundType(SoundType.WOOD);
  }

  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer owner, EnumHand hand, EnumFacing facing, float side, float x, float y) {
    if (!world.isRemote) {
      owner.openGui(Mod.INSTANCE, GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
    }
    return true;
  }

  public static void registerGui() {
    GUI_ID = GuiHandler.registerGui(new GuiHandler.Factory() {
      @Override
      @SideOnly(Side.CLIENT)
      public net.minecraft.client.gui.Gui createGui(EntityPlayer player, World world, int x, int y, int z) {
        return new Gui(new Container(player, new BlockPos(x, y, z)));
      }

      @Override
      public net.minecraft.inventory.Container createContainer(EntityPlayer player, World world, int x, int y, int z) {
        return new Container(player, new BlockPos(x, y, z));
      }
    });
  }

  public static void registerRecipe(Recipe recipe) {
    RECIPES.add(recipe);
  }

  public static Recipe findRecipe(ItemStack input) {
    for (Recipe i : RECIPES) {
      if (i.match(input)) {
        return i;
      }
    }
    return null;
  }

  public void setModel() {
    Models.setItemModel(Items.SPIRIT_SMITHING_TABLE, "spirit_smithing_table");
  }
}
