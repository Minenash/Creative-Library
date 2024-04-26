package com.minenash.creative_library.screens;

import com.minenash.creative_library.config.Config;
import com.minenash.creative_library.library.Library;
//import com.minenash.creative_library.library.LibraryItemGroup;
import com.minenash.creative_library.library.LibraryItemGroup;
import com.minenash.creative_library.library.LibrarySet;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.minenash.creative_library.screens.LibraryButton.*;

public class CreativeInventoryScreenMixinCallback {

    private static final Identifier EDIT_BUTTON_TEXTURE = new Identifier("creative_library","textures/tab_creative_library.png");

    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void renderButtonsAndTooltips(HandledScreen<?> screen, MatrixStack matrices, ItemGroup selectedTab, int x, int y, int mouseX, int mouseY) {
        if (selectedTab != null && !modifyTab(selectedTab))
            return;

        RenderSystem.setShaderTexture(0, EDIT_BUTTON_TEXTURE);

        ADD_BUTTON.draw(screen, matrices, x, y, mouseX, mouseY);
        CLONE_BUTTON.draw(screen, matrices, x, y, mouseX, mouseY);
        SETTINGS_BUTTON.draw(screen, matrices, x, y, mouseX, mouseY);

        ADD_BUTTON.drawTooltip(screen, matrices, x, y, mouseX, mouseY);
        CLONE_BUTTON.drawTooltip(screen, matrices, x, y, mouseX, mouseY);
        SETTINGS_BUTTON.drawTooltip(screen, matrices, x, y, mouseX, mouseY);

        if (EDIT_BUTTON.isIn(screen, x, y, mouseX, mouseY))
            EDIT_BUTTON.draw(screen, matrices, x, y, mouseX, mouseY);

    }

    public static void onButtonClick(Screen screen, int x, int y, double mouseX, double mouseY, int button, ItemGroup selectedTab, CallbackInfoReturnable<Boolean> info) {
        if (!modifyTab(selectedTab) || button != 0)
            return;

        Library library = selectedTab instanceof LibraryItemGroup lig? lig.library : LibrarySet.getMain();
        System.out.println(library);

        if (EDIT_BUTTON.isIn(screen,x,y,mouseX,mouseY))
            client.setScreen(new LibraryContentScreen(screen, client.player, library));

        else if (ADD_BUTTON.isIn(screen,x,y,mouseX,mouseY))
            client.setScreen(EditLibraryScreen.create(screen));

        else if (CLONE_BUTTON.isIn(screen,x,y,mouseX,mouseY))
            client.setScreen(EditLibraryScreen.clone(screen, library));

        else if (SETTINGS_BUTTON.isIn(screen,x,y,mouseX,mouseY))
            client.setScreen(EditLibraryScreen.edit(screen, library));

        else
            return;

        info.setReturnValue(true);
        info.cancel();
    }

    private static boolean modifyTab(ItemGroup selectedTab) {
        return (Config.replaceHotBarWithPrimaryLibrary && selectedTab == ItemGroups.HOTBAR) || selectedTab instanceof LibraryItemGroup;
    }

}
