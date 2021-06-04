package com.minenash.creative_library.config;

public class Config extends TinyConfig {

    public enum PrimaryLibrary{ UNIVERSAL, SERVER};

    @Entry public static boolean replaceHotBarWithPrimaryLibrary = true;
    @Entry public static PrimaryLibrary primaryLibraryTab = PrimaryLibrary.SERVER;

}
