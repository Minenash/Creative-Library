package net.fabricmc.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class CreativeLibrary implements ClientModInitializer {

	public static final KeyBinding OPEN_SCREEN = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"creative_library.keybind.open_edit_screen",
			InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G,
			"key.categories.misc"));

	@Override
	public void onInitializeClient() {
		System.out.println(CreativeLibraryStorage.getLibrary());
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (OPEN_SCREEN.wasPressed())
				client.openScreen(new CreativeLibraryEditScreen(client.player));
		});
	}

}
