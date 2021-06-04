package com.minenash.creative_library.mixin.server_identifiers;

import com.minenash.creative_library.library.LibrarySet;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldListWidget.Entry.class)
public class WorldListWidgetMixin {

    @Shadow @Final private LevelSummary level;

    @Inject(method = "play", at = @At("HEAD"))
    public void setFilename(CallbackInfo info) {
        LibrarySet.loadWorld(level.getName());
    }
}