package com.minenash.creative_library.mixin.creative_screen;

import com.minenash.creative_library.CreativeInventoryScreenDuck;
import com.minenash.creative_library.library.LibraryItemGroup;
import com.minenash.creative_library.screens.CreativeInventoryScreenMixinCallback;
import com.minenash.creative_library.config.Config;
import net.fabricmc.fabric.impl.item.group.CreativeGuiExtensions;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.options.HotbarStorageEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CreativeInventoryScreen.class, priority = 900)
public abstract class CreativeInventoryScreenMixin extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> implements CreativeInventoryScreenDuck {

	@Shadow private static int selectedTab;

	private CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}

	@ModifyVariable(method = "init", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;selectedTab:I", ordinal = 1))
	private int creativeLibrary$saveFromCrash(int selectedTab) {
		return selectedTab < ItemGroup.GROUPS.length ? selectedTab : 0;
	}

	@Redirect(method = "setSelectedTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/options/HotbarStorageEntry;isEmpty()Z"))
	private boolean creativeLibrary$doNotDisplayEmptyHotbarThing(HotbarStorageEntry entry) {
		return !modifyTab() && entry.isEmpty();
	}

	@Redirect(method = "drawBackground", at = @At(value = "NEW", target = "net/minecraft/util/Identifier", ordinal = 0))
	private Identifier creativeLibrary$libraryTabTexture(String id) {
		if (modifyTab())
			return new Identifier("creative_library", "textures/tab_creative_library.png");
		return new Identifier(id);
	}

	private static final int SCROLLBAR_BOTTOM = 90;
	@ModifyConstant(method = "mouseDragged", constant = @Constant(intValue = 112))
	private int creativeLibrary$shortenScrollbar1(int normal) { return modifyTab() ? SCROLLBAR_BOTTOM : normal; }

	@ModifyConstant(method = "isClickInScrollbar", constant = @Constant(intValue = 112))
	private int creativeLibrary$shortenScrollbar2(int normal) { return modifyTab() ? SCROLLBAR_BOTTOM : normal; }

	@ModifyConstant(method = "drawBackground", constant = @Constant(intValue = 112))
	private int creativeLibrary$shortenScrollbar3(int normal) { return modifyTab() ? SCROLLBAR_BOTTOM : normal; }


	@Inject(method = "render", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/client/gui/screen/ingame/AbstractInventoryScreen;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V"))
	private void creativeLibrary$addButtons(MatrixStack matrices, int mouseX, int mouseY, float _delta, CallbackInfo _info) {
		CreativeInventoryScreenMixinCallback.renderButtonsAndTooltips(this, matrices, selectedTab, x, y, mouseX, mouseY);
	}

	@Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
	private void creativeLibrary$buttonClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> info) {
		CreativeInventoryScreenMixinCallback.onButtonClick(this, x, y, mouseX, mouseY, button, selectedTab, info);
	}

	@Redirect(method = "setSelectedTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;appendStacks(Lnet/minecraft/util/collection/DefaultedList;)V"))
	private void creativeLibrary$setLibraryStacks(ItemGroup itemGroup, DefaultedList<ItemStack> items) {
		if (itemGroup instanceof LibraryItemGroup)
			items.addAll(((LibraryItemGroup) itemGroup).library.getItems());
		else
			itemGroup.appendStacks(items);

	}

	private static boolean modifyTab() {
		return (Config.replaceHotBarWithPrimaryLibrary && selectedTab == ItemGroup.HOTBAR.getIndex()) || ItemGroup.GROUPS[selectedTab] instanceof LibraryItemGroup;
	}

	public Screen withTab(int tab) {
		selectedTab = tab;
		System.out.println(selectedTab);
		return this;
	}


}
