package com.minenash.creative_library.mixin;

import com.minenash.creative_library.CreativeLibraryStorage;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelStorage.class)
public class LevelStorageMixin {

    @Inject(method = "levelExists", at = @At("HEAD"))
    private void getLevelName(String level, CallbackInfoReturnable<Boolean> _info) {
        CreativeLibraryStorage.loadFromWorld(level);
    }

}