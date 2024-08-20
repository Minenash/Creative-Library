package com.minenash.creative_library.screens;

import com.minenash.creative_library.CLUtils;
import com.minenash.creative_library.library.Library;
import net.fabricmc.fabric.impl.client.itemgroup.CreativeGuiExtensions;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemGroups;
import net.minecraft.text.Text;

import static com.minenash.creative_library.CLUtils.button;

public class ConfirmLibraryDeleteScreen extends Screen {

    private final Library library;
    private final Screen previousScreen;

    public ConfirmLibraryDeleteScreen(Screen previousScreen, Library library) {
        super(Text.literal("'" + library.name + "' Settings"));
        this.library = library;
        this.previousScreen = previousScreen;
    }

    @Override
    protected void init() {

        int y = this.height / 4 + 144 + 5;

        this.addDrawableChild(button("cancel", this.width / 2 - 100, y, 88, 20, _button -> close()));
        this.addDrawableChild(button("delete", this.width / 2 + 12, y, 88, 20, button -> {
            library.set.libraries.remove(library);
            library.set.save();

            CreativeInventoryScreen screen = new CreativeInventoryScreen(client.player, this.client.player.networkHandler.getEnabledFeatures(), this.client.options.getOperatorItemsTab().getValue());
            if ( ItemGroups.getGroups().size() % 9 == 3)
                ((CreativeGuiExtensions) screen).fabric_previousPage();
            this.client.setScreen(screen);
        }));

    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        drawCenteredTextWithShadow(matrices, textRenderer, title, width / 2, 15, 16777215);
        drawCenteredTextWithShadow(matrices, textRenderer, Text.translatable("creative_library.screen.confirm_delete.confirm_delete"), width / 2, 108, 16777215);

        super.render(matrices, mouseX, mouseY, delta);
    }

    public void close() {
        this.client.setScreen(previousScreen);
    }

}
