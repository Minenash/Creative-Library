package com.minenash.creative_library.mixin.server_identifiers;

import com.minenash.creative_library.library.Library;
import com.minenash.creative_library.library.LibrarySet;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LevelStorage.class)
public class LevelStorageMixin {

    @Inject(method = "levelExists", at = @At("HEAD"))
    private void getLevelName(String level, CallbackInfoReturnable<Boolean> _info) {
        LibrarySet.loadWorld(level);
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