package com.minenash.creative_library.screens;

import com.minenash.creative_library.CreativeLibrary;
import com.minenash.creative_library.config.Config.PrimaryLibrary;
import com.minenash.creative_library.library.Library;
import com.minenash.creative_library.library.LibrarySet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class EditLibraryScreen extends Screen {

    private final Screen previousScreen;
    private final Library library;
    private final boolean newLibrary;

    private TextFieldWidget libraryName;
    private boolean tieToServer = false;
    private PositionSliderWidget position;
    private PrimaryLibrary hotbarOverride;
    private ButtonWidget createSaveButton;



    public static EditLibraryScreen create(Screen previousScreen) {
        return new EditLibraryScreen(previousScreen, "new_library", null, true);
    }

    public static EditLibraryScreen clone(Screen previousScreen, Library library) {
        return new EditLibraryScreen(previousScreen, "clone_library", library, true);
    }

    public static EditLibraryScreen edit(Screen previousScreen, Library library) {
        return new EditLibraryScreen(previousScreen, "library_settings", library, false);
    }

    public EditLibraryScreen(Screen previousScreen, String title, Library library, boolean newLibrary) {
        super(new TranslatableText("creative_library.screen." + title + ".title", !newLibrary ? library.name : ""));
        this.previousScreen = previousScreen;
        this.library = library;
        this.newLibrary = newLibrary;
        this.hotbarOverride = LibrarySet.server.primaryLibraryOverride;
        if (library != null)
            this.tieToServer = library.set == LibrarySet.server;
    }

    public void tick() {
        this.libraryName.tick();
    }

    @Override
    protected void init() {
        this.client.keyboard.setRepeatEvents(true);

        int y = this.height / 4 + 144 + 5;
        if (newLibrary) {
            this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, y, 98, 20, new TranslatableText("creative_library.button.cancel"), _button -> onClose()));
            createSaveButton = this.addDrawableChild(new ButtonWidget(this.width / 2, y, 98, 20, new TranslatableText("creative_library.button.create"), _button -> createLibrary()));
        }
        else {
            this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, y, 88, 20, new TranslatableText("creative_library.button.cancel"), _button -> onClose()));
            this.addDrawableChild(new ButtonWidget(this.width / 2 - 10, y, 20, 20, new TranslatableText("creative_library.button.delete_short"), _button -> delete()));
            createSaveButton = this.addDrawableChild(new ButtonWidget(this.width / 2 + 12, y, 88, 20, new TranslatableText("creative_library.button.save"), _button -> saveLibrary()));
        }

        this.libraryName = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 48, 200, 20, null);
        this.libraryName.setChangedListener(this::updateButtonStatus);
        if (!newLibrary)
            this.libraryName.setText(library.name);
        this.addSelectableChild(this.libraryName);
        this.setInitialFocus(this.libraryName);

        this.addDrawableChild(new ButtonWidget(this.width / 2, 78, 100, 20, booleanText(tieToServer), button -> {
            tieToServer = !tieToServer;
            position.update();
            button.setMessage(booleanText(tieToServer));
        }));

        int offset = newLibrary ? 0 : 1;
        this.position = this.addDrawableChild(PositionSliderWidget.create(this.width / 2, 103, 100,
                () -> tieToServer ? LibrarySet.server.libraries.size() - offset: LibrarySet.universal.libraries.size() - offset));

        this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, 158, 95, 20, hotbarText(), button -> {
            if (hotbarOverride == null)
                hotbarOverride = PrimaryLibrary.UNIVERSAL;
            else if (hotbarOverride == PrimaryLibrary.UNIVERSAL)
                hotbarOverride = PrimaryLibrary.SERVER;
            else
                hotbarOverride = null;

            button.setMessage(hotbarText());
        }));

        updateButtonStatus(libraryName.getText());

    }

    private Text booleanText(boolean bool) {
        return bool ? new TranslatableText("creative_library.tinyconfig.boolean.true") :
                      new TranslatableText("creative_library.tinyconfig.boolean.false");
    }

    private Text hotbarText() {
        if (hotbarOverride == null)
            return new TranslatableText("creative_library.default");
        switch (hotbarOverride) {
            case UNIVERSAL: return new TranslatableText("creative_library.universal");
            case SERVER: return new LiteralText(CreativeLibrary.serverTerm());
        }
        return null;
    }

    public void resize(MinecraftClient client, int width, int height) {
        String string = this.libraryName.getText();
        this.init(client, width, height);
        this.libraryName.setText(string);
    }

    private void updateButtonStatus(String input) {
        createSaveButton.active = !input.isEmpty();
    }

    public void removed() {
        this.client.keyboard.setRepeatEvents(false);
        PositionSliderWidget.reset();
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, 16777215);
        drawTextWithShadow(matrices, this.textRenderer, new TranslatableText("creative_library.screen.options.name_field"), this.width / 2 - 100, 34, 10526880);

        drawTextWithShadow(matrices, textRenderer, new TranslatableText("creative_library.screen.options.tie_to_server", CreativeLibrary.serverTerm()), width / 2 - 100, 83, 16777215);
        drawTextWithShadow(matrices, textRenderer, new TranslatableText("creative_library.screen.options.position"), width / 2 - 100, 108, 16777215);
        drawCenteredText(matrices, textRenderer, new TranslatableText("creative_library.screen.options.sever_settings", CreativeLibrary.serverTerm()), width / 2, 133, 16777215);
        drawTextWithShadow(matrices, textRenderer, new TranslatableText("creative_library.screen.options.primary_library_tab.line_1"), width / 2 - 100, 158, 16777215);
        drawTextWithShadow(matrices, textRenderer, new TranslatableText("creative_library.screen.options.primary_library_tab.line_2"), width / 2 - 100, 168, 16777215);

        this.libraryName.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    private void createLibrary() {
        LibrarySet set = tieToServer ? LibrarySet.server : LibrarySet.universal;
        Library library = new Library(libraryName.getText());

        library.set = set;
        if (this.library != null)
            library.setItems(this.library.getItems());

        set.libraries.add(position.getValue(), library);
        set.save();

        LibrarySet.server.primaryLibraryOverride = hotbarOverride;
        if (set != LibrarySet.server)
            LibrarySet.server.save();

        this.client.openScreen(new LibraryContentScreen(previousScreen, client.player, library));
    }

    private void saveLibrary() {
        LibrarySet set = tieToServer ? LibrarySet.server : LibrarySet.universal;
        int libraryIndex = library.set.libraries.indexOf(library);
        int pos = position.getValue();

        System.out.println("Sets: " + library.set + " | " + set);
        System.out.println("Pos: " + library.set + " | " + set);

        if (pos != libraryIndex || library.set != set) {
            library.set.libraries.remove(libraryIndex);
            set.libraries.add(pos, library);
        }

        LibrarySet.server.primaryLibraryOverride = hotbarOverride;

        LibrarySet.server.save();
        LibrarySet.universal.save();

        onClose();
    }

    private void delete() {
        this.client.openScreen(new ConfirmLibraryDeleteScreen(this, library));
    }

    public void onClose() {
        this.client.openScreen(previousScreen);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        position.onRelease(mouseX, mouseY);
        return super.mouseReleased(mouseX, mouseY, button);
    }

}
