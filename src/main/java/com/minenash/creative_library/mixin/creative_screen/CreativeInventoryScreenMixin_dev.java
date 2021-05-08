package com.minenash.creative_library.mixin.creative_screen;

import com.minenash.creative_library.CreativeLibraryStorage;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;

@Mixin(CreativeInventoryScreen.class)
public class CreativeInventoryScreenMixin_dev {

    private int runNumber = 0;
    @Redirect(method = "setSelectedTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/DefaultedList;addAll(Ljava/util/Collection;)Z"))
    private boolean getItems_dev(DefaultedList<ItemStack> list, Collection<ItemStack> _in) {
        if (runNumber++ == 8) {
            runNumber = 0;
            if (CreativeLibraryStorage.getLibrary().isEmpty()) {
                for (int i = 0; i < 3 * 9; i++)
                    CreativeLibraryStorage.getLibrary().add(new ItemStack(randomWool()));
                CreativeLibraryStorage.save();
            }
            return list.addAll(CreativeLibraryStorage.getLibrary());
        }
        return false;
    }

    private ItemConvertible randomWool() {
        switch ((int)(Math.random()*(16))) {
            case 0: return Blocks.WHITE_WOOL;
            case 1: return Blocks.ORANGE_WOOL;
            case 2: return Blocks.MAGENTA_WOOL;
            case 3: return Blocks.LIGHT_BLUE_WOOL;
            case 4: return Blocks.YELLOW_WOOL;
            case 5: return Blocks.LIME_WOOL;
            case 6: return Blocks.PINK_WOOL;
            case 7: return Blocks.GRAY_WOOL;
            case 8: return Blocks.LIGHT_GRAY_WOOL;
            case 9: return Blocks.CYAN_WOOL;
            case 10: return Blocks.PURPLE_WOOL;
            case 11: return Blocks.BLUE_WOOL;
            case 12: return Blocks.BROWN_WOOL;
            case 13: return Blocks.GREEN_WOOL;
            case 14: return Blocks.RED_WOOL;
            case 15: return Blocks.BLACK_WOOL;
        }
        return null;
    }

}
