package com.minenash.creative_library;

import com.minenash.creative_library.config.Config;
import com.minenash.creative_library.library.LibrarySet;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.resource.language.I18n;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CreativeLibrary implements ClientModInitializer {


	@Override
	public void onInitializeClient() {

		Config.init("creative_library", Config.class);
		LibrarySet.loadUniversal();

		try {
			Path path = FabricLoader.getInstance().getConfigDir().resolve("creative_library");
			Files.createDirectories(path.resolve("singleplayer"));
			Files.createDirectories(path.resolve("servers"));
			Files.createDirectories(path.resolve("realms"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static String serverTerm() {
		switch (LibrarySet.server.serverType) {
			case WORLD: return I18n.translate("creative_library.world");
			case SERVER: return I18n.translate("creative_library.server");
			case REALM: return I18n.translate("creative_library.realm");
		}
		return null;
	}

}
