package com.kevinthegreat.organizableplayscreens.gui;

import com.kevinthegreat.organizableplayscreens.api.EntryType;
import com.kevinthegreat.organizableplayscreens.mixin.accessor.EntryListWidgetInvoker;
import com.kevinthegreat.organizableplayscreens.mixin.accessor.MultiplayerScreenAccessor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public abstract class AbstractMultiplayerEntry extends MultiplayerServerListWidget.Entry implements AbstractEntry<MultiplayerServerListWidget.Entry> {
    @NotNull
    protected final MultiplayerScreen screen;
    /**
     * The parent of this folder.
     */
    @Nullable
    protected MultiplayerFolderEntry parent;
    @NotNull
    protected final EntryType type;
    @NotNull
    protected String name;
    /**
     * Used to detect double-clicking.
     */
    private long time;

    /**
     * Creates a new entry with the default name.
     *
     * @param screen the screen this entry is on
     * @param parent the parent folder of this entry
     * @param type   the type of this entry
     */
    public AbstractMultiplayerEntry(@NotNull MultiplayerScreen screen, @Nullable MultiplayerFolderEntry parent, @NotNull EntryType type) {
        this(screen, parent, type, I18n.translate("organizableplayscreens:entry.new", type.text().getString()));
    }

    /**
     * Creates a new entry with the specified name.
     *
     * @param screen the screen this entry is on
     * @param parent the parent folder of this entry
     * @param type   the type of this entry
     * @param name   the name of this entry
     */
    public AbstractMultiplayerEntry(@NotNull MultiplayerScreen screen, @Nullable MultiplayerFolderEntry parent, @NotNull EntryType type, @NotNull String name) {
        this.screen = screen;
        this.parent = parent;
        this.type = type;
        this.name = name;
    }

    public @Nullable MultiplayerFolderEntry getParent() {
        return parent;
    }

    public void setParent(@Nullable MultiplayerFolderEntry parent) {
        this.parent = parent;
    }

    @Override
    public @NotNull EntryType getType() {
        return type;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public void setName(@NotNull String name) {
        this.name = name;
    }

    @Override
    public final void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        render(context, index, y, x, mouseX, mouseY, hovered, tickDelta, name, ((MultiplayerServerListWidgetAccessor) ((MultiplayerScreenAccessor) screen).getServerListWidget()).organizableplayscreens_getCurrentEntries().size());
    }

    /**
     * Handles key presses for this folder.
     * <p>
     * The folder is shifted down or up if {@link org.lwjgl.glfw.GLFW#GLFW_KEY_LEFT_SHIFT} and {@link GLFW#GLFW_KEY_DOWN} or {@link GLFW#GLFW_KEY_UP} are pressed, and it is valid to shift.
     *
     * @param keyCode the key code of the key that was pressed
     * @return whether the key press has been consumed (prevents further processing or not)
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (Screen.hasShiftDown()) {
            MultiplayerServerListWidget serverListWidget = ((MultiplayerScreenAccessor) screen).getServerListWidget();
            int i = ((MultiplayerServerListWidgetAccessor) serverListWidget).organizableplayscreens_getCurrentEntries().indexOf(this);
            if (i == -1) {
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_DOWN && i < ((MultiplayerServerListWidgetAccessor) serverListWidget).organizableplayscreens_getCurrentEntries().size() - 1 || keyCode == GLFW.GLFW_KEY_UP && i > 0) {
                swapEntries(i, keyCode == GLFW.GLFW_KEY_DOWN ? i + 1 : i - 1);
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * Handles mouse clicks for this folder.
     * <p>
     * Checks for click on the open and swap buttons, and handles double-clicking.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        MultiplayerServerListWidget serverListWidget = ((MultiplayerScreenAccessor) screen).getServerListWidget();
        int i = ((MultiplayerServerListWidgetAccessor) serverListWidget).organizableplayscreens_getCurrentEntries().indexOf(this);
        double d = mouseX - (double) serverListWidget.getRowLeft();
        double e = mouseY - (double) ((EntryListWidgetInvoker) serverListWidget).rowTop(i);
        if (d <= 32) {
            if (d < 32 && d > 16) {
                screen.select(this);
                screen.connect();
                return true;
            }
            if (d < 16 && e < 16 && i > 0) {
                swapEntries(i, i - 1);
                return true;
            }
            if (d < 16 && e > 16 && i < ((MultiplayerServerListWidgetAccessor) serverListWidget).organizableplayscreens_getCurrentEntries().size() - 1) {
                swapEntries(i, i + 1);
                return true;
            }
        }

        screen.select(this);
        if (Util.getMeasuringTimeMs() - time < 250) {
            screen.connect();
        }
        time = Util.getMeasuringTimeMs();
        return false;
    }

    /**
     * Swaps the entries at {@code i} and {@code j} and updates and saves the entries.
     *
     * @param i the index of the selected entry
     * @param j the index of the entry to swap with
     * @see com.kevinthegreat.organizableplayscreens.gui.MultiplayerServerListWidgetAccessor#organizableplayscreens_swapEntries(int, int) swapEntries(int, int)
     */
    private void swapEntries(int i, int j) {
        ((MultiplayerServerListWidgetAccessor) ((MultiplayerScreenAccessor) screen).getServerListWidget()).organizableplayscreens_swapEntries(i, j);
    }

    @Override
    public Text getNarration() {
        return Text.translatable("narrator.select", name);
    }
}
