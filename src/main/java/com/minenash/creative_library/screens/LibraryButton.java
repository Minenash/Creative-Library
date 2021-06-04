package com.minenash.creative_library.screens;

public class LibraryButton {

    public static final LibraryButton EDIT_BUTTON = new LibraryButton(172, 111, 18);
    public static final LibraryButton ADD_BUTTON = new LibraryButton(147, 4, 12);
    public static final LibraryButton CLONE_BUTTON = new LibraryButton(161, 4, 12);
    public static final LibraryButton SETTINGS_BUTTON = new LibraryButton(175, 4, 12);

    public int xOffset, yOffset, size;
    public LibraryButton(int x, int y, int size) {
        this.xOffset = x;
        this.yOffset = y;
        this.size = size;
    }

    public boolean isIn(int x, int y, double mouseX, double mouseY) {
        return mouseX > x + xOffset && mouseX < x + xOffset + size && mouseY > y + yOffset && mouseY < y + yOffset + size;
    }

}
