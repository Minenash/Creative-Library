package com.minenash.creative_library.mixin.creative_screen;

import com.minenash.creative_library.CreativeLibraryStorage;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;

@Mixin(CreativeInventoryScreen.class)
public class CreativeInventoryScreenMixin_prod {

    private int runNumber = 0;
    @Redirect(method = "setSelectedTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_2371;addAll(Ljava/util/Collection;)Z"))
    private boolean getItems_prod(DefaultedList<ItemStack> list, Collection<ItemStack> _in) {
        if (runNumber++ == 8) {
            runNumber = 0;
            return list.addAll(CreativeLibraryStorage.getLibrary());
        }
        return false;
    }

}
