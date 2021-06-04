package com.minenash.creative_library.library;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Library {

    public String name;
    public LibrarySet set;
    private List<ItemStack> items = new ArrayList<>();

    public Library(String name) {
        this.name = name;
    }


    public CompoundTag toTag() {
        ListTag listTag = new ListTag();
        for(ItemStack stack : items)
            listTag.add(stack.toTag(new CompoundTag()));

        CompoundTag rootTag = new CompoundTag();
        rootTag.putString("name", name);
        rootTag.put("0", listTag);

        return rootTag;
    }

    public static Library fromTag(CompoundTag tag, int dataVersion, LibrarySet set) {
        Library library = new Library(tag.contains("name") ? tag.getString("name") : I18n.translate("creative_library.library"));

        ListTag listTag = NbtHelper.update(MinecraftClient.getInstance().getDataFixer(), DataFixTypes.HOTBAR, tag, dataVersion).getList("0", 10);
        for(int i = 0; i < listTag.size(); ++i)
            library.items.add(ItemStack.fromTag(listTag.getCompound(i)));

        library.set = set;
        return library;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public void setItems(List<ItemStack> items) {
        this.items = items.stream().map(ItemStack::copy).collect(Collectors.toList());
    }



}
