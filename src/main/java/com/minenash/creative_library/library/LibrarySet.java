package com.minenash.creative_library.library;

import com.minenash.creative_library.DynamicItemGroups;
import com.minenash.creative_library.config.Config;
import com.minenash.creative_library.config.Config.PrimaryLibrary;
import net.minecraft.SharedConstants;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtIo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LibrarySet {

    public enum ServerType { WORLD, SERVER, REALM}

    private static final Logger LOGGER = LogManager.getLogger();

    public static final LibrarySet universal = new LibrarySet();
    public static LibrarySet server = new LibrarySet();

    public List<Library> libraries = new ArrayList<>();
    private File file;
    public boolean loaded = false;

    //Server Only
    public ServerType serverType;
    public PrimaryLibrary primaryLibraryOverride = null;

    private void load(String serverIdentifier) {
        this.file = new File("config/creative_library/" + serverIdentifier);
        this.libraries = new ArrayList<>();
        this.loaded = true;

        try {
            NbtCompound rootTag = NbtIo.read(file);
            if (rootTag == null)
                return;

            if (rootTag.contains("primaryLibraryOverride")) {
                switch (rootTag.getString("primaryLibraryOverride")) {
                    case "universal": primaryLibraryOverride = PrimaryLibrary.UNIVERSAL;
                    case "server": primaryLibraryOverride = PrimaryLibrary.SERVER;
                }
            }

            int dataVersion = rootTag.getInt("DataVersion");

            if (rootTag.contains("0"))
                this.libraries.add(Library.fromTag(rootTag, dataVersion, this));
            else {
                NbtList libraries = rootTag.getList("libraries", 10);
                for (int i = 0; i < libraries.size(); i++)
                    this.libraries.add(Library.fromTag(libraries.getCompound(i), dataVersion, this));
            }

            ((DynamicItemGroups) ItemGroup.BUILDING_BLOCKS).creativeLibrary$setItemGroupLibraries();


        } catch (IOException e) {
            LOGGER.error("Failed to load libraries", e);
        }

    }

    public void save() {

        if (libraries.isEmpty() && !file.exists())
            return;

        NbtList libraries = new NbtList();
        for (Library library : this.libraries)
            libraries.add(library.toTag());

        NbtCompound rootTag = new NbtCompound();
        rootTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        rootTag.put("libraries", libraries);

        if (primaryLibraryOverride != null) {
            switch (primaryLibraryOverride) {
                case UNIVERSAL: rootTag.putString("primaryLibraryOverride", "universal");
                case SERVER: rootTag.putString("primaryLibraryOverride", "server");
            }
        }

        try {
            NbtIo.write(rootTag, file);
            ((DynamicItemGroups) ItemGroup.BUILDING_BLOCKS).creativeLibrary$setItemGroupLibraries();
        } catch (IOException e) {
            LOGGER.error("Failed to save libraries", e);
        }

    }

    public static Library getMain() {
        //System.out.println("Primary: " + primaryLibrary());

        if ( primaryLibrary() == PrimaryLibrary.SERVER)  {
            if (!LibrarySet.server.libraries.isEmpty())
                return LibrarySet.server.libraries.get(0);
            if (!LibrarySet.universal.libraries.isEmpty())
                return LibrarySet.universal.libraries.get(0);
            return null;
        }

        if (!LibrarySet.universal.libraries.isEmpty())
            return LibrarySet.universal.libraries.get(0);
        if (!LibrarySet.server.libraries.isEmpty())
            return LibrarySet.server.libraries.get(0);
        return null;
    }

    private static PrimaryLibrary primaryLibrary() {
        return server.primaryLibraryOverride != null ? server.primaryLibraryOverride : Config.primaryLibraryTab;
    }

    public static void loadUniversal() {
        LibrarySet.universal.load("universal.nbt");
        if (universal.libraries.isEmpty()) {
            Library library = new Library(I18n.translate("creative_library.library"));
            library.set = LibrarySet.universal;
            universal.libraries.add(library);
            universal.save();
        }
    }

    public static void loadWorld(String worldName) {
        LibrarySet.server.load("singleplayer/" + worldName + ".nbt");
        LibrarySet.server.serverType = ServerType.WORLD;
        if (!LibrarySet.universal.loaded)
            LibrarySet.loadUniversal();
    }

    public static void loadServer(ServerAddress address) {
        LibrarySet.server.load("servers/" + address.getAddress() + ".nbt");
        LibrarySet.server.serverType = ServerType.SERVER;
        if (!LibrarySet.universal.loaded)
            LibrarySet.loadUniversal();
    }

    private static final Pattern RESERVED_FILENAMES_PATTERN = Pattern.compile(".*\\.|(?:COM|CLOCK\\$|CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(?:\\..*)?", Pattern.CASE_INSENSITIVE);
    public static void loadRealm(String realmName) {
        for (char c : SharedConstants.INVALID_CHARS_LEVEL_NAME)
            realmName = realmName.replace(c, '_');

        if (RESERVED_FILENAMES_PATTERN.matcher(realmName).matches())
            realmName = "_" + realmName + "_";

        if (realmName.length() > 255 - 4)
            realmName = realmName.substring(0, 255 - 4);

        LibrarySet.server.load("realms/" + realmName + ".nbt");
        LibrarySet.server.serverType = ServerType.REALM;
        if (!LibrarySet.universal.loaded)
            LibrarySet.loadUniversal();
    }

}
