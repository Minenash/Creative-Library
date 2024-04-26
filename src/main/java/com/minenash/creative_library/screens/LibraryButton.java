package com.minenash.creative_library.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemGroups;
import net.minecraft.text.Text;

public class LibraryButton {

    public static final LibraryButton EDIT_BUTTON     = new LibraryButton("edit",      0,  0, 172,    111, 18);
    public static final LibraryButton ADD_BUTTON      = new LibraryButton("add",      30, 18, 147, 4, 12);
    public static final LibraryButton CLONE_BUTTON    = new LibraryButton("clone",    78, 66, 161, 4, 12);
    public static final LibraryButton SETTINGS_BUTTON = new LibraryButton("settings", 54, 42, 175, 4, 12);

    public String key;
    public int xOffset, yOffset, size, inactiveV, activeV;
    public LibraryButton(String key, int iV, int aV, int x, int y, int size) {
        this.key = key;
        this.inactiveV = iV;
        this.activeV = aV;
        this.xOffset = x;
        this.yOffset = y;
        this.size = size;
    }

    public boolean isIn(Screen s, int x, int y, double mouseX, double mouseY) {
        return mouseX > fx(s,x) + xOffset && mouseX < fx(s,x) + xOffset + size && mouseY > y + yOffset && mouseY < y + yOffset + size;
    }

    public void draw(Screen s, MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        s.drawTexture(matrices, fx(s,x) + xOffset, y + yOffset, 256 - size, isIn(s,x,y,mouseX,mouseY) ? inactiveV : activeV, size, size);
    }

    public void drawTooltip(Screen s, MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        if (isIn(s,x,y,mouseX,mouseY))
            s.renderTooltip(matrices, Text.translatable("creative_library.button.tooltip." + key), mouseX, mouseY);
    }

    private int fx(Screen s, int x) {
        return ItemGroups.getGroups().size() > 14 && this != EDIT_BUTTON && s instanceof CreativeInventoryScreen ? x-20 : x;
    }
}
