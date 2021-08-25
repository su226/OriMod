package su226.orimod.blocks;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import su226.orimod.ModClient;
import su226.orimod.components.Components;
import su226.orimod.entities.SpiritLightOrb;
import su226.orimod.others.Interfaces;
import su226.orimod.others.Render;
import su226.orimod.others.Util;

public class SpiritSmithingTable extends Block {
  public static class Container extends ScreenHandler {
    private final Inventory items = new SimpleInventory(2);
    private final PlayerInventory playerInv;
    public Recipe recipe;

    protected final Slot inputSlot = new Slot(items, 0, 27, 47) {
      @Override
      public int getMaxItemCount() {
        return 1;
      }

      @Override
      public void markDirty() {
        ItemStack stack = this.getStack();
        recipe = findRecipe(stack);
        if (recipe != null && Components.SPIRIT_LIGHT.get(playerInv.player).get() >= recipe.cost()) {
          outputSlot.setStack(recipe.getOutput(stack));
        } else {
          outputSlot.setStack(ItemStack.EMPTY);
        }
        super.markDirty();
      }
    };
    protected final Slot outputSlot = new Slot(items, 1, 134, 47) {
      @Override
      public boolean canInsert(ItemStack stack) {
        return false;
      }

      @Override
      public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
        if (!player.world.isClient) {
          Components.SPIRIT_LIGHT.get(player).cost(recipe.cost());
        }
        inputSlot.setStack(ItemStack.EMPTY);
        return stack;
      }
    };

    public Container(int syncId, PlayerInventory playerInv) {
      super(Interfaces.SPIRIT_SMITHING_TABLE, syncId);
      this.playerInv = playerInv;
      this.addSlot(inputSlot);
      this.addSlot(outputSlot);

      for (int y = 0; y < 3; ++y) {
        for (int x = 0; x < 9; ++x) {
          this.addSlot(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
        }
      }

      for (int x = 0; x < 9; ++x) {
        this.addSlot(new Slot(playerInv, x, 8 + x * 18, 142));
      }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
      return true;
    }

    @Override
    public void close(PlayerEntity player) {
      super.close(player);
      if (this.inputSlot.hasStack()) {
        player.dropItem(this.inputSlot.getStack(), false);
      }
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
      ItemStack oldStack = ItemStack.EMPTY;
      Slot slot = this.slots.get(index);
      if (slot != null && slot.hasStack()) {
        ItemStack newStack = slot.getStack();
        oldStack = newStack.copy();
        if (index < 2) {
          if (!this.insertItem(newStack, 2, 38, true)) {
            return ItemStack.EMPTY;
          }
        } else {
          // Special handling for input slot, or limit will be broken.
          // I don't really know how it works, but it does!
          if (inputSlot.hasStack()) {
            return ItemStack.EMPTY;
          } else {
            newStack.setCount(newStack.getCount() - 1);
            oldStack.setCount(1);
            inputSlot.setStack(oldStack);
          }
        }
        if (newStack.isEmpty()) {
          slot.setStack(ItemStack.EMPTY);
        } else {
          slot.markDirty();
        }
        if (newStack.getCount() == oldStack.getCount()) {
          return ItemStack.EMPTY;
        }
        slot.onTakeItem(player, newStack);
      }
      return oldStack;
    }
  }

  public static class Screen extends HandledScreen<Container> {
    public static final Identifier TEXTURE = Util.getIdentifier("textures/gui/spirit_smithing_table.png");

    public Screen(Container container, PlayerInventory inventory, Text title) {
      super(container, inventory, title);
      this.titleX = 60;
      this.titleY = 18;
    }

    @Override
    protected void drawBackground(MatrixStack mat, float tickDelta, int mouseX, int mouseY) {
      this.renderBackground(mat);
      this.client.getTextureManager().bindTexture(TEXTURE);
      this.drawTexture(mat, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
      double rot = (ModClient.tick + tickDelta) * 0.05;
      GlStateManager.enableBlend();
      Render.Legacy.square(mat,
        new Vec3d(this.x + 84 + Math.cos(rot + Math.PI / 2) * 16, this.y + 55 + Math.sin(rot + Math.PI / 2) * 16, 0),
        new Vec3d(this.x + 84 + Math.cos(rot) * 16, this.y + 55 + Math.sin(rot) * 16, 0),
        new Vec3d(this.x + 84 + Math.cos(rot - Math.PI / 2) * 16, this.y + 55 + Math.sin(rot - Math.PI / 2) * 16, 0),
      SpiritLightOrb.Render.TEXTURE);
      GlStateManager.disableBlend();
    }

    @Override
    protected void drawForeground(MatrixStack mat, int mouseX, int mouseY) {
      super.drawForeground(mat, mouseX, mouseY);
      int value = Components.SPIRIT_LIGHT.get(this.client.player).get();
      String str = this.handler.recipe == null ? Integer.toString(value) : String.format("%d/%d", value, this.handler.recipe.cost());
      this.textRenderer.draw(mat, str, 84 - this.textRenderer.getWidth(str) / 2f, 64, 0x404040);
      if (this.handler.recipe != null && value < this.handler.recipe.cost()) {
        this.client.getTextureManager().bindTexture(TEXTURE);
        this.drawTexture(mat, 99, 45, 176, 0, 32, 32);
      }
    }

    @Override
    public void render(MatrixStack mat, int mouseX, int mouseY, float delta) {
      super.render(mat, mouseX, mouseY, delta);
      this.drawMouseoverTooltip(mat, mouseX, mouseY);
    }
  }

  public record Recipe(ItemStack input, ItemStack output, int cost) {
    public boolean match(ItemStack input) {
      return input.isItemEqualIgnoreDamage(this.input);
    }

    public ItemStack getOutput(ItemStack input) {
      ItemStack ret = this.output.copy();
      NbtCompound tag = input.getTag();
      if (tag != null) {
        ret.setTag(tag.copy());
      }
      return ret;
    }
  }

  public static final List<Recipe> RECIPES = new ArrayList<>();

  public SpiritSmithingTable() {
    super(FabricBlockSettings.of(Material.WOOD).strength(2.5f).sounds(BlockSoundGroup.WOOD));
  }

  @Override
  @SuppressWarnings("deprecation")
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
    if (!world.isClient) {
      player.openHandledScreen(new NamedScreenHandlerFactory() {
        @Override
        public Text getDisplayName() {
          return new TranslatableText("gui.orimod.spirit_smithing_table");
        }

        @Override
        public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
          return new Container(syncId, inv);
        }
      });
    }
    return ActionResult.SUCCESS;
  }

  public static void registerRecipe(Recipe recipe) {
    RECIPES.add(recipe);
  }

  public static Recipe findRecipe(ItemStack input) {
    if (!input.isEmpty()) {
      for (Recipe i : RECIPES) {
        if (i.match(input)) {
          return i;
        }
      }
    }
    return null;
  }
}
