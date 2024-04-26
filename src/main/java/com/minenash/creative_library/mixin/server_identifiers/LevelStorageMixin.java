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

}