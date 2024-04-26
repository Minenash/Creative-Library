package com.minenash.creative_library;

import com.minenash.creative_library.config.Config;
import com.minenash.creative_library.library.Library;
import com.minenash.creative_library.library.LibraryItemGroup;
import com.minenash.creative_library.library.LibrarySet;
import net.fabricmc.fabric.mixin.itemgroup.ItemGroupsAccessor;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class CLUtils {

    public static ButtonWidget button(String key, int x, int y, int width, int height, ButtonWidget.PressAction action) {
        return buttonRaw(Text.translatable("creative_library.button." + key), x, y, width, height, action);
    }

    public static ButtonWidget buttonRaw(Text text, int x, int y, int width, int height, ButtonWidget.PressAction action) {
        return ButtonWidget
                .builder(text, action)
                .position(x, y)
                .size(width, height)
                .build();
    }

    public static void updateTabs() {
        List<ItemGroup> original = ItemGroups.getGroups();
        List<ItemGroup> tabs = new ArrayList<>();

        for (int i = 0; i < 14; i++)
            tabs.add(original.get(i));

        if (Config.libraryTabPositions == Config.LibraryTabPositions.BEFORE)
            addLibraryTabs(tabs);

        for (int i = 14; i < original.size(); i++) {
            ItemGroup tab = original.get(i);
            if (!(tab instanceof LibraryItemGroup))
                tabs.add(tab);
        }

        if (Config.libraryTabPositions == Config.LibraryTabPositions.AFTER)
            addLibraryTabs(tabs);

        ItemGroupsAccessor.setGroups( ItemGroups.collect(tabs.toArray(new ItemGroup[0])) );

        System.out.println(ItemGroups.getGroups());
        System.out.println(ItemGroups.getGroupsToDisplay());
    }

    private static void addLibraryTabs(List<ItemGroup> tabs) {
        System.out.println("START");
        for (Library library : LibrarySet.universal.libraries)
            if (!(Config.replaceHotBarWithPrimaryLibrary && library == LibrarySet.getMain())) {
                tabs.add(new LibraryItemGroup(library));
                System.out.println("U: " + library.name);
            }

        if (LibrarySet.server.loaded)
            for (Library library : LibrarySet.server.libraries)
                if (!(Config.replaceHotBarWithPrimaryLibrary && library == LibrarySet.getMain())) {
                    tabs.add(new LibraryItemGroup(library));
                    System.out.println("S: " + library.name);
                }
        System.out.println("END");
    }

}
