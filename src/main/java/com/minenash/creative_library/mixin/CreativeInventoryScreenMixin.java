package com.minenash.creative_library.mixin;

import com.minenash.creative_library.CLUtils;
import com.minenash.creative_library.config.Config;
import com.minenash.creative_library.library.LibraryItemGroup;
import com.minenash.creative_library.library.LibrarySet;
import com.minenash.creative_library.screens.CreativeInventoryScreenMixinCallback;
import net.fabricmc.fabric.impl.client.itemgroup.CreativeGuiExtensions;
import net.fabricmc.fabric.impl.client.itemgroup.FabricCreativeGuiComponents;
import net.fabricmc.fabric.impl.itemgroup.FabricItemGroup;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("UnstableApiUsage")
@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> {

	@Shadow private static ItemGroup selectedTab;
	@Shadow private TextFieldWidget searchBox;
	@Shadow private float scrollPosition;

	private CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}

	@Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;addSelectableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"))
	public void creativeLibrary$updateTabs(CallbackInfo ci) {
		CLUtils.updateTabs();
		for (ItemGroup g : ItemGroups.getGroupsToDisplay())
			if (isOnPage(g))
				return;
		((CreativeGuiExtensions)this).fabric_previousPage();
	}

	private boolean isOnPage(ItemGroup group) {
		return ((FabricItemGroup) group).getPage() == ((CreativeGuiExtensions)this).fabric_currentPage();
	}

	@Redirect(method = "drawBackground", at = @At(value = "NEW", target = "net/minecraft/util/Identifier", ordinal = 0))
	private Identifier creativeLibrary$libraryTabTexture(String id) {
		if (modifyTab())
			return new Identifier("creative_library", "textures/tab_creative_library.png");
		return new Identifier(id);
	}

	@Inject(method = "setSelectedTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getType()Lnet/minecraft/item/ItemGroup$Type;", ordinal = 0), cancellable = true)
	private void creativeLibrary$replaceHotbarTabWithMainLibraryTab(ItemGroup group, CallbackInfo ci) {
		if (shouldReplace(group)) {
			this.handler.itemList.addAll(LibrarySet.getMain().getItems());
			if (this.searchBox != null) {
				this.searchBox.setVisible(false);
				this.searchBox.setFocusUnlocked(true);
				this.searchBox.setTextFieldFocused(false);
				this.searchBox.setText("");
			}
			this.scrollPosition = 0.0f;
			this.handler.scrollItems(0.0f);
			ci.cancel();
		}
	}

	@Redirect(method = "drawForeground", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getDisplayName()Lnet/minecraft/text/Text;"))
	private Text creativeLibrary$replaceHotbarTabWithMainLibraryTab2(ItemGroup group) {
		return shouldReplace(group) ? Text.literal(LibrarySet.getMain().name) : group.getDisplayName();
	}

	@Redirect(method = "renderTabTooltipIfHovered", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getDisplayName()Lnet/minecraft/text/Text;"))
	private Text creativeLibrary$replaceHotbarTabWithMainLibraryTab3(ItemGroup group) {
		return shouldReplace(group) ? Text.literal(LibrarySet.getMain().name) : group.getDisplayName();
	}

	private static boolean shouldReplace(ItemGroup group) {
		return Config.replaceHotBarWithPrimaryLibrary && group.getType() == ItemGroup.Type.HOTBAR;
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

	private static boolean modifyTab() {
		return selectedTab.getType() == ItemGroup.Type.HOTBAR || selectedTab instanceof LibraryItemGroup;
	}


}
