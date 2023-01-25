package com.minenash.creative_library.screens;

import com.minenash.creative_library.config.Config;
import com.minenash.creative_library.library.Library;
import com.minenash.creative_library.library.LibraryItemGroup;
import com.minenash.creative_library.library.LibrarySet;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.minenash.creative_library.screens.LibraryButton.*;

public class CreativeInventoryScreenMixinCallback {

    private static final Identifier EDIT_BUTTON_TEXTURE = new Identifier("creative_library","textures/tab_creative_library.png");

    private static final MinecraftClient client = MinecraftClient.getInstance();


    public static void renderButtonsAndTooltips(HandledScreen<?> screen, MatrixStack matrices, int selectedTab, int x, int y, int mouseX, int mouseY) {
        if (selectedTab != -1 && !modifyTab(selectedTab))
            return;

        RenderSystem.setShaderTexture(0, EDIT_BUTTON_TEXTURE);

        screen.drawTexture(matrices, x + 147, y + 4, 256 - 12, ADD_BUTTON.isIn(x,y,mouseX,mouseY) ? 30 : 18, 12, 12);
        screen.drawTexture(matrices, x + 161, y + 4, 256 - 12, CLONE_BUTTON.isIn(x,y,mouseX,mouseY) ? 78 : 66, 12, 12);
        screen.drawTexture(matrices, x + 175, y + 4, 256 - 12, SETTINGS_BUTTON.isIn(x,y,mouseX,mouseY) ? 54 : 42, 12, 12);

        if (EDIT_BUTTON.isIn(x,y,mouseX,mouseY)) {
            screen.drawTexture(matrices, x + 172, y + 111, 256 - 18, 0, 18, 18);
            screen.renderTooltip(matrices, Text.translatable("creative_library.tooltip.edit_library_contents"), mouseX, mouseY);
        }
        else if (ADD_BUTTON.isIn(x,y,mouseX,mouseY))
            screen.renderTooltip(matrices, Text.translatable("creative_library.tooltip.create_new_library"), mouseX, mouseY);

        else if (CLONE_BUTTON.isIn(x,y,mouseX,mouseY))
            screen.renderTooltip(matrices, Text.translatable("creative_library.tooltip.clone_library"), mouseX, mouseY);

        else if (SETTINGS_BUTTON.isIn(x,y,mouseX,mouseY))
            screen.renderTooltip(matrices, Text.translatable("creative_library.tooltip.library_settings"), mouseX, mouseY);
    }

    public static void onButtonClick(Screen screen, int x, int y, double mouseX, double mouseY, int button, int selectedTab, CallbackInfoReturnable<Boolean> info) {
        if (!modifyTab(selectedTab) || button != 0)
            return;

        ItemGroup itemGroup = ItemGroup.GROUPS[selectedTab];
        Library library = itemGroup instanceof LibraryItemGroup ? ((LibraryItemGroup)itemGroup).library : LibrarySet.getMain();
        System.out.println(library);

        if (EDIT_BUTTON.isIn(x,y,mouseX,mouseY))
            client.setScreen(new LibraryContentScreen(screen, client.player, library));

        else if (ADD_BUTTON.isIn(x,y,mouseX,mouseY))
            client.setScreen(EditLibraryScreen.create(screen));

        else if (CLONE_BUTTON.isIn(x,y,mouseX,mouseY))
            client.setScreen(EditLibraryScreen.clone(screen, library));

        else if (SETTINGS_BUTTON.isIn(x,y,mouseX,mouseY))
            client.setScreen(EditLibraryScreen.edit(screen, library));

        else
            return;

        info.setReturnValue(true);
        info.cancel();
    }

    private static boolean modifyTab(int selectedTab) {
        return (Config.replaceHotBarWithPrimaryLibrary && selectedTab == ItemGroup.HOTBAR.getIndex()) || ItemGroup.GROUPS[selectedTab] instanceof LibraryItemGroup;
    }

}
