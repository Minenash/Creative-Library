package net.fabricmc.example;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;

import java.util.*;

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
import net.minecraft.screen.ScreenHandlerListener;
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

    public CreativeLibraryEditScreen(PlayerEntity player) {
        super(new CreativeLibraryEditScreen.CreativeScreenHandler(player), player.inventory, LiteralText.EMPTY);
        player.currentScreenHandler = this.handler;
        this.passEvents = true;
        this.backgroundHeight = 223;
        this.backgroundWidth = 195;
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

    public void tick() {
        if (!this.client.interactionManager.hasCreativeInventory())
            this.client.openScreen(new InventoryScreen(this.client.player));
    }

    // Ignores SlotActionType.PICKUP_ALL, doesn't do right-click on stacks, doesn't do drag correctly
    private boolean cursorItemIsAFAAAAAKE = false;
    protected void onMouseClick(@Nullable Slot slot, int invSlot, int clickData, SlotActionType actionType) {

        if (slot == null) {
            System.out.println("Slot is null");
            if (cursorItemIsAFAAAAAKE || actionType == SlotActionType.QUICK_CRAFT)
                return;
            dropItems(playerInventory.getCursorStack(), clickData == 0);
            return;
        }

        boolean isLibrarySlot = slot.inventory == handler.inventory;
        ItemStack cursorStack = playerInventory.getCursorStack();
        ItemStack slotStack = slot.getStack();

        System.out.println("Action: " + actionType + " | click:" + clickData);

        if (actionType == SlotActionType.THROW && !cursorItemIsAFAAAAAKE) {
            dropItems(slot.getStack(), clickData == 1);
        }

        else if (actionType == SlotActionType.SWAP && !isLibrarySlot) {
            ItemStack stack = playerInventory.getStack(clickData);
            playerInventory.setStack(clickData, slotStack);
            slot.setStack(stack);
        }

        else if (actionType == SlotActionType.PICKUP || actionType == SlotActionType.QUICK_CRAFT) {

//            if (clickData == 1 && !isLibrarySlot && !cursorItemIsAFAAAAAKE) {
//                if (!cursorStack.isEmpty() && !slotStack.isEmpty()) {
//                    slot.setStack(cursorStack);
//                    playerInventory.setCursorStack(slotStack);
//                }
//                else if (!cursorStack.isEmpty()) {
//                    float half = slotStack.getCount() / 2F;
//                    playerInventory.setCursorStack(slotStack);
//                    playerInventory.getCursorStack().setCount((int)Math.ceil(half));
//                    slotStack.setCount((int)half);
//                }
//                else if (!slotStack.isEmpty()) {
//                    slot.setStack(cursorStack);
//                    slot.getStack().setCount(1);
//                    playerInventory.getCursorStack().decrement(1);
//
//                }
//            }

            if ((isLibrarySlot == cursorItemIsAFAAAAAKE) || cursorStack.isEmpty()) {
                slot.setStack(cursorStack);
                playerInventory.setCursorStack(slotStack);
                cursorItemIsAFAAAAAKE = !playerInventory.getCursorStack().isEmpty() && isLibrarySlot;
            }
            else if (isLibrarySlot)
                slot.setStack(cursorStack);
            else
                playerInventory.setCursorStack(ItemStack.EMPTY);
        }

        else if (actionType == SlotActionType.CLONE) {
            playerInventory.setCursorStack(slotStack);
            cursorItemIsAFAAAAAKE = isLibrarySlot;
        }

        else if (actionType == SlotActionType.QUICK_MOVE) {
            if (isLibrarySlot)
                slot.setStack(ItemStack.EMPTY);
            else
                handler.addStack(slotStack, scrollPosition);
        }

        if (isLibrarySlot)
            handler.setSlot(slot, invSlot, scrollPosition);

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
        this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 1F);
        this.handler.scrollItems(this.scrollPosition);
        return true;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.scrolling) {
            float i = this.y + 18;
            float j = i + 108;
            this.scrollPosition = (float)(mouseY - i - 7.5F) / ((j - i) - 15.0F);
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 1F);
            this.handler.scrollItems(this.scrollPosition);
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
    }

    private void dropItems(ItemStack original, boolean all) {
        if (!original.isEmpty()) {
            if (all) {
                this.client.player.dropItem(original, true);
                this.client.interactionManager.dropCreativeStack(original);
                original.setCount(0);
            }
            else {
                ItemStack thrown = original.copy();
                thrown.setCount(1);
                original.decrement(1);
                this.client.player.dropItem(thrown, true);
                this.client.interactionManager.dropCreativeStack(thrown);
            }
        }
    }

    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        this.lastClickOutsideBounds = mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.backgroundWidth) || mouseY >= (double)(top + this.backgroundHeight);
        return this.lastClickOutsideBounds;
    }

    protected boolean isClickInScrollbar(double mouseX, double mouseY) {
        return mouseX >= x + 175 && mouseY >= y + 18 && mouseX < x + 175 + 14 && mouseY < y + 18 + 108;
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
        int k = j + 108;
        this.client.getTextureManager().bindTexture(SCROLL_TEXTURE);
        this.drawTexture(matrices, i, j + (int)((float)(k - j - 17) * this.scrollPosition), 232, 0, 12, 15);
    }

    @Environment(EnvType.CLIENT)
    public static class CreativeScreenHandler extends ScreenHandler {
        private static final List<ItemStack> THREE_EMPTY_ROWS = Collections.nCopies(21, ItemStack.EMPTY);
        private static final List<ItemStack> ONE_EMPTY_ROW  = Collections.nCopies(9,  ItemStack.EMPTY);
        public final List<ItemStack> itemList;
        public final Inventory inventory;

        public CreativeScreenHandler(PlayerEntity playerEntity) {
            super(null, 987456);
            PlayerInventory playerInv = playerEntity.inventory;
            inventory = new SimpleInventory(54);
            inventory.onOpen(playerEntity);

            itemList = new ArrayList<>(CreativeLibraryStorage.getLibrary());
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
            addSlot(new Slot(playerInv, 40, 173, 198));

            this.scrollItems(0.0F);
        }

        private void addSlots(Inventory inv, int index, int y, int rows) {
            for (int row = 0; row < rows; ++row)
                for (int col = 0; col < 9; ++col)
                    this.addSlot(new Slot(inv, index + col + row*9, 9 + col*18, y + row*18));
        }

        public void scrollItems(float position) {
            int j = getRow(position);
            for (int k = 0; k < 6; ++k) {
                for (int l = 0; l < 9; ++l) {
                    int m = l + (k + j) * 9;
                    inventory.setStack(l + k * 9, m >= 0 && m < this.itemList.size() ? this.itemList.get(m) : ItemStack.EMPTY);
                }
            }

        }

        private int getRow(float position) {
            return Math.max(0, (int)((position * (this.itemList.size() / 9F - 6)) + 0.5D));
        }

        public void setSlot(Slot slot, int slotID, float position) {
            itemList.set(getRow(position)*9 + slotID, slot.getStack());

            while (!itemList.subList(itemList.size()-21,itemList.size()).equals(THREE_EMPTY_ROWS))
                itemList.addAll(ONE_EMPTY_ROW);

            System.out.println("Set slot [" + (getRow(position)*9 + slotID) + "] to [" + slot.getStack().getName().getString() + "]");
        }

        public void addStack(ItemStack stack, float position) {
            int index = itemList.size() - 1;
            while (itemList.get(index).isEmpty())
                index--;
            itemList.set(++index, stack);

            while (!itemList.subList(itemList.size()-21,itemList.size()).equals(THREE_EMPTY_ROWS))
                itemList.addAll(ONE_EMPTY_ROW);

            System.out.println("Set slot [" + index + "] to [" + stack.getName().getString() + "]");

            scrollItems(position);
        }

        public boolean canUse(PlayerEntity player) {
            return this.inventory.canPlayerUse(player);
        }

        public void close(PlayerEntity player) {
            super.close(player);
            System.out.println("Saved");

            while (itemList.size() > 0 && itemList.get(itemList.size()-1).isEmpty())
                itemList.remove(itemList.size()-1);

            CreativeLibraryStorage.setLibrary(itemList);
            CreativeLibraryStorage.save();
            this.inventory.onClose(player);
        }

    }



}
