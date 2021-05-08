package com.minenash.creative_library.mixin;

import com.minenash.creative_library.CreativeLibraryStorage;
import net.minecraft.client.gui.screen.ConnectScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConnectScreen.class)
public class ConnectScreenMixin {

    @Inject(method = "connect", at = @At("HEAD"), cancellable = true)
    public void getImage(String address, int port, CallbackInfo info) {
        CreativeLibraryStorage.setFromServer(address, port);
        CreativeLibraryStorage.load();
    }
}
