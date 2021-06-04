package com.minenash.creative_library.mixin;

import com.minenash.creative_library.DynamicItemGroups;
import com.minenash.creative_library.config.Config;
import com.minenash.creative_library.library.Library;
import com.minenash.creative_library.library.LibraryItemGroup;
import com.minenash.creative_library.library.LibrarySet;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(ItemGroup.class)
public class ItemGroupMixin implements DynamicItemGroups {

    @Mutable
    @Shadow @Final public static ItemGroup[] GROUPS;

    @Inject(method = "getTranslationKey", at = @At("RETURN"), cancellable = true)
    private void creativeLibrary$changeHotBarTranslationKey(CallbackInfoReturnable<Text> info) {
        if (Config.replaceHotBarWithPrimaryLibrary && (Object)this == ItemGroup.HOTBAR)
            info.setReturnValue( new LiteralText(LibrarySet.getMain().name));
    }

    @Override
    public void creativeLibrary$setItemGroupLibraries() {
        List<ItemGroup> original = Arrays.stream(GROUPS)
                                    .filter(itemGroup -> !(itemGroup instanceof LibraryItemGroup))
                                    .collect(Collectors.toList());

        int hotbarAdjuster = Config.replaceHotBarWithPrimaryLibrary && LibrarySet.getMain() != null? 1 : 0;
        GROUPS = new ItemGroup[original.size() + LibrarySet.universal.libraries.size() + LibrarySet.server.libraries.size() - hotbarAdjuster];

        for (int i = 0; i < 12; i++)
            GROUPS[i] = original.get(i);

        int i = 12;
        for (Library library : LibrarySet.universal.libraries)
            if (!(Config.replaceHotBarWithPrimaryLibrary && library == LibrarySet.getMain()))
                new LibraryItemGroup(library, i++);

        if (LibrarySet.server.loaded)
            for (Library library : LibrarySet.server.libraries)
                if (!(Config.replaceHotBarWithPrimaryLibrary && library == LibrarySet.getMain()))
                    new LibraryItemGroup(library, i++);

        if (original.size() > 12)
            for (int j = 12; j < original.size(); j++)
                GROUPS[i++] = original.get(j);

        System.out.println(Arrays.toString(GROUPS));
    }
}
