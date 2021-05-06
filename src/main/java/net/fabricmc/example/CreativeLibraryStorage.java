package net.fabricmc.example;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.HotbarStorageEntry;
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
    private static final File file =  new File(MinecraftClient.getInstance().runDirectory, "creative_library.nbt");
    private static List<ItemStack> items = new ArrayList<>();
    private static boolean loaded;

    private static void load() {
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
        if (!loaded) {
            load();
            loaded = true;
        }
        return items;
    }

    public static void setLibrary(List<ItemStack> items) {
        CreativeLibraryStorage.items = items;
    }
}

