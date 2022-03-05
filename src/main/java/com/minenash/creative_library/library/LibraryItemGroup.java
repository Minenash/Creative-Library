package com.minenash.creative_library.library;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;


public class LibraryItemGroup extends ItemGroup {

    public Library library;

    public LibraryItemGroup(Library library, int index) {
        super(index, library.name);
        this.library = library;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Text getDisplayName() {
        return new LiteralText(library.name);
    }

    @Override
    public ItemStack createIcon() {
        return library.getItems().isEmpty() || library.getItems().get(0).getItem() == Items.AIR? new ItemStack(Items.BOOKSHELF) : library.getItems().get(0);
    }

}
