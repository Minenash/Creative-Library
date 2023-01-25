package com.minenash.creative_library.screens;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
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
            return Text.translatable("creative_library.button.position.first_primary");
        if (position == max)
            return Text.translatable("creative_library.button.position.last");
        return Text.literal( Integer.toString(position) );
    }

    @Override
    protected void renderBackground(MatrixStack matrices, MinecraftClient client, int mouseX, int mouseY) {
        if (max != 0)
            super.renderBackground(matrices, client, mouseX, mouseY);
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
