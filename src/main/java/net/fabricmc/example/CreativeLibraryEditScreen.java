package net.fabricmc.example;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryListener;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.options.HotbarStorage;
import net.minecraft.client.options.HotbarStorageEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class CreativeLibraryEditScreen extends AbstractInventoryScreen<CreativeLibraryEditScreen.CreativeScreenHandler> {
    private static final Identifier SCROLL_TEXTURE = new Identifier("textures/gui/container/creative_inventory/tabs.png");
    private static final Identifier INV_TEXTURE = new Identifier("modid","textures/creative_library_edit.png");
    private float scrollPosition;
    private boolean scrolling;
    private CreativeInventoryListener listener;
    private boolean lastClickOutsideBounds;
    @Nullable
    private List<Slot> slots;

    public CreativeLibraryEditScreen(PlayerEntity player) {
        super(new CreativeLibraryEditScreen.CreativeScreenHandler(player), player.inventory, LiteralText.EMPTY);
        player.currentScreenHandler = this.handler;
        this.passEvents = true;
        this.backgroundHeight = 223;
        this.backgroundWidth = 195;
    }

    public void tick() {
        if (!this.client.interactionManager.hasCreativeInventory())
            this.client.openScreen(new InventoryScreen(this.client.player));

    }

    protected void onMouseClick(@Nullable Slot slot, int invSlot, int clickData, SlotActionType actionType) {
        super.onMouseClick(slot, invSlot, clickData, actionType);
//        boolean bl = actionType == SlotActionType.QUICK_MOVE;
//        actionType = invSlot == -999 && actionType == SlotActionType.PICKUP ? SlotActionType.THROW : actionType;
//        ItemStack itemStack3;
//        PlayerInventory playerInventory;
//        if (slot == null && actionType != SlotActionType.QUICK_CRAFT) {
//            playerInventory = this.client.player.inventory;
//            if (!playerInventory.getCursorStack().isEmpty() && this.lastClickOutsideBounds) {
//                if (clickData == 0) {
//                    this.client.player.dropItem(playerInventory.getCursorStack(), true);
//                    this.client.interactionManager.dropCreativeStack(playerInventory.getCursorStack());
//                    playerInventory.setCursorStack(ItemStack.EMPTY);
//                }
//
//                if (clickData == 1) {
//                    itemStack3 = playerInventory.getCursorStack().split(1);
//                    this.client.player.dropItem(itemStack3, true);
//                    this.client.interactionManager.dropCreativeStack(itemStack3);
//                }
//            }
//        } else {
//            if (slot != null && !slot.canTakeItems(this.client.player))
//                return;
//            ItemStack itemStack8;
//            ItemStack itemStack10;
//            if (actionType != SlotActionType.QUICK_CRAFT && slot.inventory == INVENTORY) {
//                playerInventory = this.client.player.inventory;
//                itemStack3 = playerInventory.getCursorStack();
//                ItemStack itemStack4 = slot.getStack();
//                if (actionType == SlotActionType.SWAP) {
//                    if (!itemStack4.isEmpty()) {
//                        itemStack10 = itemStack4.copy();
//                        itemStack10.setCount(itemStack10.getMaxCount());
//                        this.client.player.inventory.setStack(clickData, itemStack10);
//                        this.client.player.playerScreenHandler.sendContentUpdates();
//                    }
//
//                    return;
//                }
//
//                if (actionType == SlotActionType.CLONE) {
//                    if (playerInventory.getCursorStack().isEmpty() && slot.hasStack()) {
//                        itemStack10 = slot.getStack().copy();
//                        itemStack10.setCount(itemStack10.getMaxCount());
//                        playerInventory.setCursorStack(itemStack10);
//                    }
//
//                    return;
//                }
//
//                if (actionType == SlotActionType.THROW) {
//                    if (!itemStack4.isEmpty()) {
//                        itemStack10 = itemStack4.copy();
//                        itemStack10.setCount(clickData == 0 ? 1 : itemStack10.getMaxCount());
//                        this.client.player.dropItem(itemStack10, true);
//                        this.client.interactionManager.dropCreativeStack(itemStack10);
//                    }
//
//                    return;
//                }
//
//                if (!itemStack3.isEmpty() && !itemStack4.isEmpty() && itemStack3.isItemEqualIgnoreDamage(itemStack4) && ItemStack.areTagsEqual(itemStack3, itemStack4)) {
//                    if (clickData == 0) {
//                        if (bl) {
//                            itemStack3.setCount(itemStack3.getMaxCount());
//                        } else if (itemStack3.getCount() < itemStack3.getMaxCount()) {
//                            itemStack3.increment(1);
//                        }
//                    } else {
//                        itemStack3.decrement(1);
//                    }
//                } else if (!itemStack4.isEmpty() && itemStack3.isEmpty()) {
//                    playerInventory.setCursorStack(itemStack4.copy());
//                    itemStack3 = playerInventory.getCursorStack();
//                    if (bl) {
//                        itemStack3.setCount(itemStack3.getMaxCount());
//                    }
//                } else if (clickData == 0) {
//                    playerInventory.setCursorStack(ItemStack.EMPTY);
//                } else {
//                    playerInventory.getCursorStack().decrement(1);
//                }
//            } else if (this.handler != null) {
//                itemStack8 = slot == null ? ItemStack.EMPTY : this.handler.getSlot(slot.id).getStack();
//                this.handler.onSlotClick(slot == null ? invSlot : slot.id, clickData, actionType, this.client.player);
//                if (ScreenHandler.unpackQuickCraftStage(clickData) == 2) {
//                    for(int j = 0; j < 9; ++j) {
//                        this.client.interactionManager.clickCreativeStack(this.handler.getSlot(45 + j).getStack(), 36 + j);
//                    }
//                } else if (slot != null) {
//                    itemStack3 = this.handler.getSlot(slot.id).getStack();
//                    this.client.interactionManager.clickCreativeStack(itemStack3, slot.id - this.handler.slots.size() + 9 + 36);
//                    int k = 45 + clickData;
//                    if (actionType == SlotActionType.SWAP) {
//                        this.client.interactionManager.clickCreativeStack(itemStack8, k - this.handler.slots.size() + 9 + 36);
//                    } else if (actionType == SlotActionType.THROW && !itemStack8.isEmpty()) {
//                        itemStack10 = itemStack8.copy();
//                        itemStack10.setCount(clickData == 0 ? 1 : itemStack10.getMaxCount());
//                        this.client.player.dropItem(itemStack10, true);
//                        this.client.interactionManager.dropCreativeStack(itemStack10);
//                    }
//
//                    this.client.player.playerScreenHandler.sendContentUpdates();
//                }
//            }
//        }

    }

    protected void init() {
        if (this.client.interactionManager.hasCreativeInventory()) {
            super.init();
            this.client.player.playerScreenHandler.removeListener(this.listener);
            this.listener = new CreativeInventoryListener(this.client);
            this.client.player.playerScreenHandler.addListener(this.listener);
        } else {
            this.client.openScreen(new InventoryScreen(this.client.player));
        }

    }

    public void removed() {
        super.removed();
        if (this.client.player != null && this.client.player.inventory != null)
            this.client.player.playerScreenHandler.removeListener(this.listener);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.isClickInScrollbar(mouseX, mouseY)) {
            this.scrolling = true;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0)
            this.scrolling = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        int i = (this.handler.itemList.size() + 9 - 1) / 9 - 5;
        this.scrollPosition = (float)((double)this.scrollPosition - amount / (double)i);
        this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 0.96F);
        this.handler.scrollItems(this.scrollPosition);
        return true;
    }

    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        this.lastClickOutsideBounds = mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.backgroundWidth) || mouseY >= (double)(top + this.backgroundHeight);
        return this.lastClickOutsideBounds;
    }

    protected boolean isClickInScrollbar(double mouseX, double mouseY) {
        return mouseX >= x + 175 && mouseY >= y + 18 && mouseX < x + 175 + 14 && mouseY < y + 18 + 112;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.scrolling) {
            float i = this.y + 18;
            float j = i + 112;
            this.scrollPosition = (float)(mouseY - i - 7.5F) / ((j - i) - 15.0F);
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 0.96F);
            this.handler.scrollItems(this.scrollPosition);
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
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
        this.textRenderer.draw(matrices, "Library", 8.0F, 6.0F, 4210752);
        this.textRenderer.draw(matrices, INV_TEXT, 8.0F, 129.0F, 4210752);
    }

    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(INV_TEXTURE);
        this.drawTexture(matrices, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        int i = this.x + 175;
        int j = this.y + 18;
        int k = j + 112;
        this.client.getTextureManager().bindTexture(SCROLL_TEXTURE);
        this.drawTexture(matrices, i, j + (int)((float)(k - j - 17) * this.scrollPosition), 232, 0, 12, 15);
    }

    @Environment(EnvType.CLIENT)
    public static class CreativeScreenHandler extends ScreenHandler {
        public final List<ItemStack> itemList = CreativeLibraryStorage.getLibrary().subList(0,9*6);
        private final Inventory inventory;

        public CreativeScreenHandler(PlayerEntity playerEntity) {
            super(null, 987456);
            PlayerInventory playerInv = playerEntity.inventory;
            inventory = new SimpleInventory(54);
            inventory.onOpen(playerEntity);

            addSlots(inventory, 0,  18, 6);
            addSlots(playerInv, 9, 140, 3);
            addSlots(playerInv, 0, 198, 1);

            addSlot(new Slot(playerInv, 39, 173, 140));
            addSlot(new Slot(playerInv, 38, 173, 158));
            addSlot(new Slot(playerInv, 37, 173, 176));
            addSlot(new Slot(playerInv, 40, 173, 198));

            this.scrollItems(0.0F);
        }

        private void addSlots(Inventory inv, int index, int y, int rows) {
            for (int row = 0; row < rows; ++row)
                for (int col = 0; col < 9; ++col)
                    this.addSlot(new Slot(inv, index + col + row*9, 9 + col*18, y + row*18));
        }

        public void scrollItems(float position) {
            int i = (this.itemList.size() + 9 - 1) / 9 - 5;
            int j = (int)((double)(position * (float)i) + 0.5D);
            if (j < 0)
                j = 0;

            for (int k = 0; k < 6; ++k) {
                for (int l = 0; l < 9; ++l) {
                    int m = l + (k + j) * 9;
                    inventory.setStack(l + k * 9, m >= 0 && m < this.itemList.size() ? this.itemList.get(m) : ItemStack.EMPTY);
                }
            }

        }

        public boolean canUse(PlayerEntity player) {
            return this.inventory.canPlayerUse(player);
        }

        public ItemStack transferSlot(PlayerEntity player, int index) {
            ItemStack itemStack = ItemStack.EMPTY;
            Slot slot = this.slots.get(index);
            if (slot != null && slot.hasStack()) {
                ItemStack itemStack2 = slot.getStack();
                itemStack = itemStack2.copy();
                if (index < this.inventory.size()) {
                    if (!this.insertItem(itemStack2, this.inventory.size(), this.slots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.insertItem(itemStack2, 0, this.inventory.size(), false)) {
                    return ItemStack.EMPTY;
                }

                if (itemStack2.isEmpty()) {
                    slot.setStack(ItemStack.EMPTY);
                } else {
                    slot.markDirty();
                }
            }

            return itemStack;
        }

        public void close(PlayerEntity player) {
            super.close(player);
            this.inventory.onClose(player);
        }

    }
}
