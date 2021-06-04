package com.minenash.creative_library.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.function.Supplier;

public class PositionSliderWidget extends SliderWidget {

    private static int position = -1;
    private final Supplier<Integer> maxValue;
    private int max;

    public static PositionSliderWidget create(int x, int y, int width, Supplier<Integer> maxValue) {
        int max = maxValue.get();
        position = position == -1? max : position;
        return new PositionSliderWidget(x, y, width, max, maxValue);
    }
    public static PositionSliderWidget create(int x, int y, int width, int pos, Supplier<Integer> maxValue) {
        int max = maxValue.get();
        position = position == -1? pos : position;
        return new PositionSliderWidget(x, y, width, max, maxValue);
    }

    private PositionSliderWidget(int x, int y, int width, int max, Supplier<Integer> maxValue) {
        super(x, y, width, 20, getText(position,max),(double) position / max);
        this.maxValue = maxValue;
        this.max = max;
    }

    @Override
    protected void updateMessage() {
        this.setMessage( getText(position, max) );
    }

    private static Text getText(int position, int max) {
        if (position == 0)
            return new TranslatableText("creative_library.button.position.first_primary");
        if (position == max)
            return new TranslatableText("creative_library.button.position.last");
        return new LiteralText( Integer.toString(position) );
    }

    protected void renderBg(MatrixStack matrices, MinecraftClient client, int mouseX, int mouseY) {
        if (max == 0)
            return;

        client.getTextureManager().bindTexture(WIDGETS_LOCATION);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        int i = 46 + (this.isHovered() ? 40 : 20);
        int x = this.x + (int)(this.value * (double)(this.width - 8));
        this.drawTexture(matrices, x, this.y, 0, i, 4, 20);
        this.drawTexture(matrices, x + 4, this.y, 196, i, 4, 20);
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.onRelease(mouseX, mouseY);
        value = (double) position / maxValue.get();
    }

    @Override
    protected void applyValue() {
        update();
    }

    public void update() {
        position = (int) Math.round(value * maxValue.get());
        max = maxValue.get();
        updateMessage();
    }

    public int getValue() {
        return position;
    }

    public static void reset() {
        position = -1;
    }
}
