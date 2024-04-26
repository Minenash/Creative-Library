package com.minenash.creative_library.library;

import net.fabricmc.fabric.impl.itemgroup.FabricItemGroup;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;


public class LibraryItemGroup extends ItemGroup {

    public static final MinecraftClient client = MinecraftClient.getInstance();
    public final Library library;
    public final boolean special;

    public LibraryItemGroup(Library library) {
        this(library, false);
    }

    public LibraryItemGroup(Library library, boolean special) {

        super(null, -1, Type.CATEGORY, Text.literal(library.name),
                () -> library.getItems().isEmpty() || library.getItems().get(0).getItem() == Items.AIR? new ItemStack(Items.BOOKSHELF) : library.getItems().get(0),
                (enabledFeatures, entries, operatorEnabled) -> entries.addAll(library.getItems()));

        ((FabricItemGroup) this).setId(new Identifier("creative_library", library.name.toLowerCase().replaceAll("[^a-z0-9/._-]", "_")));
        this.library = library;
        this.special = special;
        updateEntries(client.player.networkHandler.getEnabledFeatures(), client.options.getOperatorItemsTab().getValue());
    }

    @Override
    public boolean isSpecial() {
        return special;
    }
}
