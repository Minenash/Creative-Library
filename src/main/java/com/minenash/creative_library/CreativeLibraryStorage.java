package com.minenash.creative_library;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class CreativeLibraryStorage {
    private static final Logger LOGGER = LogManager.getLogger();
    private static List<ItemStack> items = new ArrayList<>();
    private static boolean loaded;

    private static File file;

    private static void load(String fileName) {
        CreativeLibraryStorage.file = new File("config/creative_library/" + fileName + ".nbt");
        try {
            CompoundTag compoundTag = NbtIo.read(file);
            if (compoundTag == null)
                return;

            if (!compoundTag.contains("DataVersion", 99))
                compoundTag.putInt("DataVersion", 1343);

            compoundTag = NbtHelper.update(MinecraftClient.getInstance().getDataFixer(), DataFixTypes.HOTBAR, compoundTag, compoundTag.getInt("DataVersion"));

            ListTag tag = compoundTag.getList("0", 10);
            for(int i = 0; i < tag.size(); ++i)
                items.add(ItemStack.fromTag(tag.getCompound(i)));

        } catch (Exception var3) {
            LOGGER.error("Failed to load creative mode options", var3);
        }

    }

    public static void save() {
        try {
            ListTag listTag = new ListTag();
            for(ItemStack stack : items)
                listTag.add(stack.toTag(new CompoundTag()));

            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
            compoundTag.put("0", listTag);

            NbtIo.write(compoundTag, file);
        } catch (Exception var3) {
            LOGGER.error("Failed to save creative mode options", var3);
        }

    }

    public static List<ItemStack> getLibrary() {
        return items;
    }

    public static void setLibrary(List<ItemStack> items) {
        CreativeLibraryStorage.items = items.stream().map(ItemStack::copy).collect(Collectors.toList());
    }


    public static void loadFromServer(String address, int port) {
        load("servers/" + address + "_" + port);
    }

    public static void loadFromWorld(String worldName) {
        load("singleplayer/" + worldName);
    }

    private static final Pattern RESERVED_FILENAMES_PATTERN = Pattern.compile(".*\\.|(?:COM|CLOCK\\$|CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(?:\\..*)?", Pattern.CASE_INSENSITIVE);
    public static void loadFromRealm(String realmName) {
        for (char c : SharedConstants.INVALID_CHARS_LEVEL_NAME)
            realmName = realmName.replace(c, '_');

        if (RESERVED_FILENAMES_PATTERN.matcher(realmName).matches())
            realmName = "_" + realmName + "_";

        if (realmName.length() > 255 - 4)
            realmName = realmName.substring(0, 255 - 4);

        load("realms/" + realmName);
    }
}

