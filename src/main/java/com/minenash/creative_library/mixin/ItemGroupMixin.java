package com.minenash.creative_library.mixin;

import com.minenash.creative_library.DynamicItemGroups;
import com.minenash.creative_library.config.Config;
import com.minenash.creative_library.library.Library;
import com.minenash.creative_library.library.LibraryItemGroup;
import com.minenash.creative_library.library.LibrarySet;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.LiteralTextContent;
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

    @Mutable @Shadow @Final public static ItemGroup[] GROUPS;
    @Mutable @Shadow @Final private int index;

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void creativeLibrary$changeHotBarTranslationKey(CallbackInfoReturnable<Text> info) {
        if (Config.replaceHotBarWithPrimaryLibrary && (Object)this == ItemGroup.HOTBAR)
            info.setReturnValue( Text.literal(LibrarySet.getMain().name));
    }

    @Override
    public void creativeLibrary$setItemGroupLibraries() {
        List<ItemGroup> original = Arrays.stream(GROUPS)
                                    .filter(itemGroup -> !(itemGroup instanceof LibraryItemGroup))
                                    .collect(Collectors.toList());

        int hotbarAdjuster = Config.replaceHotBarWithPrimaryLibrary && LibrarySet.getMain() != null? 1 : 0;
        GROUPS = new ItemGroup[original.size() + LibrarySet.universal.libraries.size() + LibrarySet.server.libraries.size() - hotbarAdjuster];

        if (Config.libraryTabPositions == Config.LibraryTabPositions.BEFORE)
            creativeLibrary$putLibraryTabsBefore(original);
        else
            creativeLibrary$putLibraryTabsAfter(original);

        System.out.println(Arrays.toString(GROUPS));
    }

    private void creativeLibrary$putLibraryTabsBefore(List<ItemGroup> original) {
        for (int i = 0; i < 12; i++) {
            GROUPS[i] = original.get(i);
            ((DynamicItemGroups) GROUPS[i]).creativeLibrary$setIndex(i);
        }
        int i = 12;
        for (Library library : LibrarySet.universal.libraries)
            if (!(Config.replaceHotBarWithPrimaryLibrary && library == LibrarySet.getMain()))
                new LibraryItemGroup(library, i++);

        if (LibrarySet.server.loaded)
            for (Library library : LibrarySet.server.libraries)
                if (!(Config.replaceHotBarWithPrimaryLibrary && library == LibrarySet.getMain()))
                    new LibraryItemGroup(library, i++);

        if (original.size() > 12)
            for (int j = 12; j < original.size(); j++) {
                GROUPS[i] = original.get(j);
                ((DynamicItemGroups) GROUPS[i]).creativeLibrary$setIndex(i);
                i++;
            }
    }

    private void creativeLibrary$putLibraryTabsAfter(List<ItemGroup> original) {
        int i = 0;
        for (ItemGroup itemGroup : original) {
            GROUPS[i] = itemGroup;
            ((DynamicItemGroups) GROUPS[i]).creativeLibrary$setIndex(i);
            i++;
        }
        for (Library library : LibrarySet.universal.libraries)
            if (!(Config.replaceHotBarWithPrimaryLibrary && library == LibrarySet.getMain()))
                new LibraryItemGroup(library, i++);

        if (LibrarySet.server.loaded)
            for (Library library : LibrarySet.server.libraries)
                if (!(Config.replaceHotBarWithPrimaryLibrary && library == LibrarySet.getMain()))
                    new LibraryItemGroup(library, i++);
    }

    @Override
    public void creativeLibrary$setIndex(int index) {
        this.index = index;
    }
}
