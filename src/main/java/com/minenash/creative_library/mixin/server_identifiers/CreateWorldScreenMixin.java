package com.minenash.creative_library.mixin.server_identifiers;

import com.minenash.creative_library.library.LibrarySet;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {

    @Shadow private TextFieldWidget levelNameField;

    @Inject(method = "createLevel", at = @At("RETURN"))
    public void getLevelName(CallbackInfo info) {
        LibrarySet.loadWorld(levelNameField.getText().trim());
    }

}
