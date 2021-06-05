package com.minenash.creative_library.mixin.creative_screen;

import com.minenash.creative_library.config.Config;
import com.minenash.creative_library.library.LibrarySet;
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
    private boolean creativeLibrary$getItems_dev(DefaultedList<ItemStack> list, Collection<ItemStack> hotbarRow) {
        if (!Config.replaceHotBarWithPrimaryLibrary)
            return list.addAll(hotbarRow);

        if (runNumber++ == 8) {
            runNumber = 0;
            return list.addAll(LibrarySet.getMain().getItems());
        }
        return false;
    }

    private ItemConvertible randomWool() {
        return switch ((int) (Math.random() * (16))) {
            case 0 -> Blocks.WHITE_WOOL;
            case 1 -> Blocks.ORANGE_WOOL;
            case 2 -> Blocks.MAGENTA_WOOL;
            case 3 -> Blocks.LIGHT_BLUE_WOOL;
            case 4 -> Blocks.YELLOW_WOOL;
            case 5 -> Blocks.LIME_WOOL;
            case 6 -> Blocks.PINK_WOOL;
            case 7 -> Blocks.GRAY_WOOL;
            case 8 -> Blocks.LIGHT_GRAY_WOOL;
            case 9 -> Blocks.CYAN_WOOL;
            case 10 -> Blocks.PURPLE_WOOL;
            case 11 -> Blocks.BLUE_WOOL;
            case 12 -> Blocks.BROWN_WOOL;
            case 13 -> Blocks.GREEN_WOOL;
            case 14 -> Blocks.RED_WOOL;
            case 15 -> Blocks.BLACK_WOOL;
            default -> null;
        };
    }

}
