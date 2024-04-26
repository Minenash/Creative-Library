package com.minenash.creative_library.mixin;

import com.minenash.creative_library.library.LibraryItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.featuretoggle.FeatureSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;

@Mixin(targets = "net/minecraft/item/ItemGroup$EntriesImpl")
public class ItemGroup$EntriesImplMixin {

    @Shadow @Final private ItemGroup group;

    @Shadow @Mutable @Final public Collection<ItemStack> parentTabStacks;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void changeTabStacksFromSetToList(ItemGroup group, FeatureSet enabledFeatures, CallbackInfo ci) {
        parentTabStacks = new ArrayList<>();
    }

    @Redirect(method = "add", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getCount()I"))
    private int allowEmptyStacks(ItemStack stack) {
        return stack.isEmpty() ? 1 : stack.getCount();
    }

    @Redirect(method = "add", at = @At(value = "INVOKE", target = "Ljava/util/Collection;contains(Ljava/lang/Object;)Z"))
    private boolean allowDuplicates(Collection<ItemStack> instance, Object stack) {
        return !(group instanceof LibraryItemGroup) && instance.contains(stack);
    }


}
