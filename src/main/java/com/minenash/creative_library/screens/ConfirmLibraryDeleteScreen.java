package com.minenash.creative_library.screens;

import com.minenash.creative_library.CreativeInventoryScreenDuck;
import com.minenash.creative_library.library.Library;
import com.minenash.creative_library.mixin.creative_screen.CreativeInventoryScreenMixin;
import net.fabricmc.fabric.impl.item.group.CreativeGuiExtensions;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class ConfirmLibraryDeleteScreen extends Screen {

    private final Library library;
    private final Screen previousScreen;

    public ConfirmLibraryDeleteScreen(Screen previousScreen, Library library) {
        super(new LiteralText("'" + library.name + "' Settings" ));
        this.library = library;
        this.previousScreen = previousScreen;
    }

    @Override
    protected void init() {
        this.client.keyboard.setRepeatEvents(true);

        int y = this.height / 4 + 144 + 5;
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, y, 88, 20, new TranslatableText("creative_library.button.cancel"), _button -> onClose()));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 12, y, 88, 20, new TranslatableText("creative_library.button.delete"), _button -> {
            library.set.libraries.remove(library);
            library.set.save();

            CreativeInventoryScreen screen = new CreativeInventoryScreen(client.player);
            if ( ItemGroup.GROUPS.length % 9 == 3)
                ((CreativeGuiExtensions) screen).fabric_previousPage();
            this.client.setScreen(screen);
        }));

    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        drawCenteredText(matrices, textRenderer, title, width / 2, 15, 16777215);
        drawCenteredText(matrices, textRenderer, new TranslatableText("creative_library.screen.confirm_delete.confirm_delete"), width / 2, 108, 16777215);

        super.render(matrices, mouseX, mouseY, delta);
    }

    public void onClose() {
        this.client.setScreen(previousScreen);
    }

}
