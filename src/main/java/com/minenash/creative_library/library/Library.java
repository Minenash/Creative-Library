package com.minenash.creative_library.library;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Library {

    public String name;
    public LibrarySet set;
    private List<ItemStack> items = new ArrayList<>();
    public ItemGroup group;

    public Library(String name) {
        this.name = name;
    }


    public NbtCompound toTag() {
        NbtList listTag = new NbtList();
        for(ItemStack stack : items)
            listTag.add(stack.writeNbt(new NbtCompound()));

        NbtCompound rootTag = new NbtCompound();
        rootTag.putString("name", name);
        rootTag.put("0", listTag);

        return rootTag;
    }

    public static Library fromTag(NbtCompound tag, int dataVersion, LibrarySet set) {
        Library library = new Library(tag.contains("name") ? tag.getString("name") : I18n.translate("creative_library.library"));

        NbtList listTag = DataFixTypes.HOTBAR.update(MinecraftClient.getInstance().getDataFixer(), tag, dataVersion).getList("0", 10);
        for(int i = 0; i < listTag.size(); ++i) {
            ItemStack stack = ItemStack.fromNbt(listTag.getCompound(i));
            stack.setCount(1);
            library.items.add(stack);
        }

        library.set = set;
        return library;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public void setItems(List<ItemStack> items) {
        this.items = items.stream().map(ItemStack::copy).collect(Collectors.toList());
    }

    public ItemGroup withGroup(ItemGroup group) {
        this.group = group;
        return group;
    }



}
