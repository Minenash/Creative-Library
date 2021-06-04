package com.minenash.creative_library.screens;

import com.minenash.creative_library.library.Library;
import com.minenash.creative_library.library.LibrarySet;
import com.mojang.blaze3d.systems.RenderSystem;

import java.util.*;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryListener;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import static com.minenash.creative_library.screens.LibraryButton.*;

@Environment(EnvType.CLIENT)
public class LibraryContentScreen extends AbstractInventoryScreen<LibraryContentScreen.CreativeScreenHandler> {
    private static final Identifier INV_TEXTURE = new Identifier("creative_library","textures/creative_library_edit.png");
    public static float scrollPosition = 0;
    private boolean scrolling;
    private CreativeInventoryListener listener;
    private final Screen previousScreen;

    public LibraryContentScreen(Screen previousScreen, PlayerEntity player, Library library) {
        super(new LibraryContentScreen.CreativeScreenHandler(player, library), player.inventory, LiteralText.EMPTY);
        player.currentScreenHandler = this.handler;
        this.passEvents = true;
        this.backgroundHeight = 223;
        this.backgroundWidth = 195;
        this.previousScreen = previousScreen;
    }

    protected void init() {
        if (this.client.interactionManager.hasCreativeInventory()) {
            super.init();
            this.client.player.playerScreenHandler.removeListener(this.listener);
            this.listener = new CreativeInventoryListener(this.client);
            this.client.player.playerScreenHandler.addListener(this.listener);
        } else
            this.client.openScreen(new InventoryScreen(this.client.player));

    }

    public void removed() {
        super.removed();
        if (this.client.player != null && this.client.player.inventory != null)
            this.client.player.playerScreenHandler.removeListener(this.listener);
    }

    public void onClose() {
        this.client.openScreen(previousScreen);
    }

    public void tick() {
        if (!this.client.interactionManager.hasCreativeInventory())
            this.client.openScreen(new InventoryScreen(this.client.player));
    }

    private boolean cursorItemIsAFAAAAAKE = false;
    protected void onMouseClick(@Nullable Slot slot, int invSlot, int clickData, SlotActionType actionType) {
        //System.out.println("Action: " + actionType + " | click:" + clickData + " | invSlot: " + invSlot);

        boolean isLibrarySlot = slot != null && slot.inventory == handler.inventory;
        ItemStack slotStack   = slot != null ? slot.getStack() : null;
        ItemStack cursorStack = playerInventory.getCursorStack();

        if (actionType == SlotActionType.QUICK_CRAFT && invSlot == -999)
            super.onMouseClick(slot, invSlot, clickData, actionType);

        else if (!isLibrarySlot && (!cursorItemIsAFAAAAAKE || cursorStack.isEmpty()) && actionType != SlotActionType.QUICK_MOVE) {
            if (actionType == SlotActionType.PICKUP_ALL)
                onPickupAll(slot, clickData == 0);
            else
                super.onMouseClick(slot, invSlot, clickData, actionType);
        }

        else if (!isLibrarySlot && cursorItemIsAFAAAAAKE) {
            cursorStack.setCount(0);
            cursorItemIsAFAAAAAKE = false;
        }

        else if (actionType == SlotActionType.PICKUP || actionType == SlotActionType.QUICK_CRAFT) {
            if (cursorStack.isEmpty() && clickData == 1) {
                slot.setStack(ItemStack.EMPTY);
            }
            else if (cursorItemIsAFAAAAAKE || cursorStack.isEmpty()) {
                slot.setStack(cursorStack);
                playerInventory.setCursorStack(slotStack);
                cursorItemIsAFAAAAAKE = !playerInventory.getCursorStack().isEmpty();
            }
            else
                slot.setStack(cursorStack.copy());
        }

        else if (actionType == SlotActionType.CLONE) {
            playerInventory.setCursorStack(slotStack);
            cursorItemIsAFAAAAAKE = true;
        }

        else if (actionType == SlotActionType.QUICK_MOVE) {
            if (isLibrarySlot)
                slot.setStack(ItemStack.EMPTY);
            else
                handler.addStack(slotStack);
        }

        if (isLibrarySlot)
            handler.setSlot(slot, invSlot);

    }

    private void onPickupAll(@Nullable Slot slot, boolean leftClick) {
        ItemStack cursorStack = playerInventory.getCursorStack();
        PlayerEntity player = playerInventory.player;

        if (!cursorStack.isEmpty() && (slot == null || !slot.hasStack() || !slot.canTakeItems(player))) {
            int l = leftClick ? 54 : handler.slots.size() - 1;
            int q = leftClick ? 1 : -1;

            for(int w = 0; w < 2; ++w) {
                for(int x = l; x >= 54 && x < handler.slots.size() && cursorStack.getCount() < cursorStack.getMaxCount(); x += q) {
                    Slot slot9 = handler.slots.get(x);
                    if (slot9.hasStack() && ScreenHandler.canInsertItemIntoSlot(slot9, cursorStack, true) && slot9.canTakeItems(player) && handler.canInsertIntoSlot(cursorStack, slot9)) {
                        ItemStack itemStack14 = slot9.getStack();
                        if (w != 0 || itemStack14.getCount() != itemStack14.getMaxCount()) {
                            int n = Math.min(cursorStack.getMaxCount() - cursorStack.getCount(), itemStack14.getCount());
                            ItemStack itemStack15 = slot9.takeStack(n);
                            cursorStack.increment(n);
                            if (itemStack15.isEmpty())
                                slot9.setStack(ItemStack.EMPTY);
                            slot9.onTakeItem(player, itemStack15);
                        }
                    }
                }
            }
        }

    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0)
            return super.mouseClicked(mouseX, mouseY, button);


        if (mouseX >= x + 175 && mouseY >= y + 18 && mouseX < x + 175 + 14 && mouseY < y + 18 + 108)
            this.scrolling = true;

        else if (ADD_BUTTON.isIn(x,y,mouseX,mouseY))
            client.openScreen(EditLibraryScreen.create(this));

        else if (CLONE_BUTTON.isIn(x,y,mouseX,mouseY))
            client.openScreen(EditLibraryScreen.clone(this, handler.library));

        else if (SETTINGS_BUTTON.isIn(x,y,mouseX,mouseY))
            client.openScreen(EditLibraryScreen.edit(this, handler.library));
        else
            return super.mouseClicked(mouseX, mouseY, button);

        return true;

    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0)
            this.scrolling = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        int i = (this.handler.itemList.size() + 9 - 1) / 9 - 5;
        scrollPosition = (float)((double)scrollPosition - amount / (double)i);
        scrollPosition = MathHelper.clamp(scrollPosition, 0.0F, 1F);
        this.handler.scrollItems();
        return true;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.scrolling) {
            float i = this.y + 18;
            float j = i + 108;
            scrollPosition = (float)(mouseY - i - 7.5F) / ((j - i) - 15.0F);
            scrollPosition = MathHelper.clamp(scrollPosition, 0.0F, 1F);
            this.handler.scrollItems();
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
    }

    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        return mouseX < left || mouseY < top || mouseX >= (left + backgroundWidth) || mouseY >= (top + backgroundHeight);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    private static final Text INV_TEXT = new TranslatableText("container.inventory");
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        RenderSystem.disableBlend();
        this.textRenderer.draw(matrices, handler.library.name, 8.0F, 6.0F, 4210752);
        this.textRenderer.draw(matrices, INV_TEXT, 8.0F, 129.0F, 4210752);
    }

    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(INV_TEXTURE);
        this.drawTexture(matrices, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        int i = this.x + 175;
        int j = this.y + 18;
        int k = j + 108;
        this.drawTexture(matrices, i, j + (int)((float)(k - j - 17) * scrollPosition), 232, 0, 12, 15);

        CreativeInventoryScreenMixinCallback.renderButtonsAndTooltips(this, matrices, -1, x, y, mouseX, mouseY);

    }

    @Environment(EnvType.CLIENT)
    public static class CreativeScreenHandler extends ScreenHandler {
        private static final List<ItemStack> FOUR_EMPTY_ROWS = Collections.nCopies(36, ItemStack.EMPTY);
        private static final List<ItemStack> THREE_EMPTY_ROWS = Collections.nCopies(27, ItemStack.EMPTY);
        private static final List<ItemStack> ONE_EMPTY_ROW  = Collections.nCopies(9,  ItemStack.EMPTY);
        public List<ItemStack> itemList;
        public final Library library;
        public final Inventory inventory;

        public CreativeScreenHandler(PlayerEntity playerEntity, Library library) {
            super(null, 987456);
            PlayerInventory playerInv = playerEntity.inventory;
            inventory = new SimpleInventory(54);
            inventory.onOpen(playerEntity);
            this.library = library;

            itemList = library.getItems();
            itemList.addAll(Collections.nCopies(9 - (itemList.size() % 9), ItemStack.EMPTY));
            itemList.addAll(THREE_EMPTY_ROWS);

            while (itemList.size() < 54)
                itemList.addAll(ONE_EMPTY_ROW);

            addSlots(inventory, 0,  18, 6);
            addSlots(playerInv, 9, 140, 3);
            addSlots(playerInv, 0, 198, 1);

            addSlot(new Slot(playerInv, 39, 173, 140));
            addSlot(new Slot(playerInv, 38, 173, 158));
            addSlot(new Slot(playerInv, 37, 173, 176));
            addSlot(new Slot(playerInv, 36, 173, 198));


            this.scrollItems();
        }

        private void addSlots(Inventory inv, int index, int y, int rows) {
            for (int row = 0; row < rows; ++row)
                for (int col = 0; col < 9; ++col)
                    this.addSlot(new Slot(inv, index + col + row*9, 9 + col*18, y + row*18));
        }

        public void scrollItems() {
            int j = getRow();
            for (int k = 0; k < 6; ++k) {
                for (int l = 0; l < 9; ++l) {
                    int m = l + (k + j) * 9;
                    inventory.setStack(l + k * 9, m >= 0 && m < this.itemList.size() ? this.itemList.get(m) : ItemStack.EMPTY);
                }
            }

        }

        private int getRow() {
            return (int) Math.max(0, (int)(LibraryContentScreen.scrollPosition * (this.itemList.size() / 9F - 6)) + 0.5D);
        }

        public void setSlot(Slot slot, int slotID) {
            itemList.set(getRow()*9 + slotID, slot.getStack());
            ensureThreeExtraRows();

            //System.out.println("Set slot [" + (getRow()*9 + slotID) + "] to [" + slot.getStack().getName().getString() + "]");
        }

        public void addStack(ItemStack stack) {
            int index = itemList.size() - 1;
            while (itemList.get(index).isEmpty())
                index--;

            itemList.set(++index, stack);
            ensureThreeExtraRows();

            //System.out.println("Set slot [" + index + "] to [" + stack.getName().getString() + "]");
        }

        private void ensureThreeExtraRows() {

            int row = getRow();

            while (itemList.subList(itemList.size()-36, itemList.size()).equals(FOUR_EMPTY_ROWS))
                itemList = itemList.subList(0, Math.max(0, itemList.size() - 9));

            while (!itemList.subList(itemList.size()-27, itemList.size()).equals(THREE_EMPTY_ROWS))
                itemList.addAll(ONE_EMPTY_ROW);

            int listSize = itemList.size() == 54 ? 55 : itemList.size();
            scrollPosition = MathHelper.clamp(row / (listSize / 9F - 6), 0.0F, 1F);

            scrollItems();
        }

        public boolean canUse(PlayerEntity player) {
            return this.inventory.canPlayerUse(player);
        }

        public void close(PlayerEntity player) {
            super.close(player);

            while (itemList.size() > 0 && itemList.get(itemList.size()-1).isEmpty())
                itemList.remove(itemList.size()-1);

            library.setItems(itemList);
            LibrarySet.universal.save();
            LibrarySet.server.save();

            this.inventory.onClose(player);
        }

    }



}
