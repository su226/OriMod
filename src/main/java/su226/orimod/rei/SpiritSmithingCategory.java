package su226.orimod.rei;

import com.mojang.blaze3d.platform.GlStateManager;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.RecipeDisplay;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.widget.Widget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import su226.orimod.ModClient;
import su226.orimod.blocks.SpiritSmithingTable;
import su226.orimod.entities.SpiritLightOrb;
import su226.orimod.items.Items;
import su226.orimod.others.Render;
import su226.orimod.others.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpiritSmithingCategory implements RecipeCategory<SpiritSmithingCategory.Display> {
  public static class Display implements RecipeDisplay {
    private final List<List<EntryStack>> inputs;
    private final List<List<EntryStack>> outputs;
    private final int cost;

    public Display(SpiritSmithingTable.Recipe recipe) {
      this.inputs = Collections.singletonList(Collections.singletonList(EntryStack.create(recipe.input())));
      this.outputs = Collections.singletonList(Collections.singletonList(EntryStack.create(recipe.output())));
      this.cost = recipe.cost();
    }

    @Override
    public @NotNull List<List<EntryStack>> getInputEntries() {
      return this.inputs;
    }

    @Override
    public @NotNull List<List<EntryStack>> getResultingEntries() {
      return this.outputs;
    }

    public int getAmount() { return this.cost; }

    @Override
    public @NotNull Identifier getRecipeCategory() {
      return ID;
    }
  }

  private static class SpiritLightWidget extends Widget {
    private final int x;
    private final int y;
    private final int amount;

    public SpiritLightWidget(int x, int y, int amount) {
      this.x = x;
      this.y = y;
      this.amount = amount;
    }

    @Override
    public void render(MatrixStack mat, int mouseX, int mouseY, float delta) {
      double rot = (ModClient.tick + delta) * 0.05;
      GlStateManager.enableBlend();
      Render.Legacy.square(mat,
        new Vec3d(x + Math.cos(rot + Math.PI / 2) * 16, y + Math.sin(rot + Math.PI / 2) * 16, 0),
        new Vec3d(x + Math.cos(rot) * 16, y + Math.sin(rot) * 16, 0),
        new Vec3d(x + Math.cos(rot - Math.PI / 2) * 16, y + Math.sin(rot - Math.PI / 2) * 16, 0),
      SpiritLightOrb.Render.TEXTURE);
      GlStateManager.disableBlend();
      MinecraftClient mc = MinecraftClient.getInstance();
      String str = Integer.toString(amount);
      mc.textRenderer.draw(mat, str, x - mc.textRenderer.getWidth(str) / 2f, y - 4, 0x404040);
    }

    @Override
    public List<? extends Element> children() {
      return Collections.emptyList();
    }
  }

  public static final Identifier ID = Util.getIdentifier("spirit_smithing");

  @Override
  public @NotNull Identifier getIdentifier() {
    return ID;
  }

  @Override
  public @NotNull String getCategoryName() {
    return I18n.translate("gui.orimod.spirit_smithing_table");
  }

  @Override
  public @NotNull EntryStack getLogo() {
    return EntryStack.create(new ItemStack(Items.SPIRIT_SMITHING_TABLE));
  }

  @Override
  public int getDisplayHeight() {
    return 36;
  }

  @Override
  public @NotNull List<Widget> setupDisplay(Display display, Rectangle bounds) {
    List<Widget> widgets = new ArrayList<>();
    widgets.add(Widgets.createRecipeBase(bounds));
    widgets.add(Widgets.createSlot(new Point(bounds.x + 26,  bounds.y + 10)).entry(display.getInputEntries().get(0).get(0)));
    widgets.add(new SpiritLightWidget(bounds.x + 57, bounds.y + 19, display.getAmount()));
    widgets.add(Widgets.createArrow(new Point(bounds.x + 71, bounds.y + 9)));
    widgets.add(Widgets.createResultSlotBackground(new Point(bounds.x + 105, bounds.y + 10)));
    widgets.add(Widgets.createSlot(new Point(bounds.x + 105, bounds.y + 10)).disableBackground().entry(display.getResultingEntries().get(0).get(0)));
    return widgets;
  }
}
