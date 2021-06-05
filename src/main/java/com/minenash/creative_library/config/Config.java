package com.minenash.creative_library.config;

public class Config extends TinyConfig {

    public enum PrimaryLibrary { UNIVERSAL, SERVER };
    public enum LibraryTabPositions { BEFORE, AFTER };

    @Entry public static boolean replaceHotBarWithPrimaryLibrary = true;
    @Entry public static PrimaryLibrary primaryLibraryTab = PrimaryLibrary.SERVER;
    @Entry public static LibraryTabPositions libraryTabPositions = LibraryTabPositions.BEFORE;

}
