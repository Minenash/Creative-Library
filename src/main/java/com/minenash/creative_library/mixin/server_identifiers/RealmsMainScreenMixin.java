package com.minenash.creative_library.mixin.server_identifiers;

import com.minenash.creative_library.library.LibrarySet;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RealmsMainScreen.class)
public class RealmsMainScreenMixin {

    @Inject(method = "play", at = @At("HEAD"))
    private void getRealmNameID(RealmsServer realmsServer, Screen parent, CallbackInfo info) {
        if (realmsServer != null)
            LibrarySet.loadRealm(realmsServer.name);
    }

}