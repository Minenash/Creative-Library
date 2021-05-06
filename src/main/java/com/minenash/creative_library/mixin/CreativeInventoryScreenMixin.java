package com.minenash.creative_library.mixin;

import com.minenash.creative_library.CreativeLibraryEditScreen;
import com.minenash.creative_library.CreativeLibraryStorage;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.options.HotbarStorageEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemConvertible;
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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> {

	@Shadow private static int selectedTab;

	private CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}

	@Redirect(method = "setSelectedTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/options/HotbarStorageEntry;isEmpty()Z"))
	private boolean doNotDisplayEmptyHotbarThing(HotbarStorageEntry _entry) {
		return false;
	}

	@Redirect(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getTexture()Ljava/lang/String;"))
	private String libraryTabTexture(ItemGroup group) {
		return group == ItemGroup.HOTBAR ? "library" : group.getTexture();
	}

	@Redirect(method = "drawBackground", at = @At(value = "NEW", target = "net/minecraft/util/Identifier", ordinal = 0))
	private Identifier libraryTabTexture(String id) {
		if (id.equals("textures/gui/container/creative_inventory/tab_library"))
			return new Identifier("creative_library", "textures/tab_creative_library.png");
		return new Identifier(id);
	}

	private static final int SCROLLBAR_BOTTOM = 90;
	@ModifyConstant(method = "mouseDragged", constant = @Constant(intValue = 112))
	private int shortenScrollbar1(int _in) { return SCROLLBAR_BOTTOM; }

	@ModifyConstant(method = "isClickInScrollbar", constant = @Constant(intValue = 112))
	private int shortenScrollbar2(int _in) {return SCROLLBAR_BOTTOM; }

	@ModifyConstant(method = "drawBackground", constant = @Constant(intValue = 112))
	private int shortenScrollbar3(int _in) { return SCROLLBAR_BOTTOM; }

	private static final Identifier EDIT_BUTTON_TEXTURE = new Identifier("creative_library","textures/tab_creative_library.png");
	@Inject(method = "render", at = @At("TAIL"))
	private void editButtonHover(MatrixStack matrices, int mouseX, int mouseY, float _delta, CallbackInfo _info) {
		if (inEditButtonLocation(mouseX, mouseY)) {
			MinecraftClient.getInstance().getTextureManager().bindTexture(EDIT_BUTTON_TEXTURE);
			drawTexture(matrices, x + 172, y + 111, 256 - 18, 0, 18, 18);
		}
	}

	@Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
	private void editButtonClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> info) {
		if (button == 0 && inEditButtonLocation(mouseX, mouseY)) {
			client.openScreen(new CreativeLibraryEditScreen(client.player));
			info.setReturnValue(true);
			info.cancel();
		}
	}

	private boolean inEditButtonLocation(double mouseX, double mouseY) {
		return selectedTab == ItemGroup.HOTBAR.getIndex() && mouseX > x + 172 && mouseX < x + 172 + 18 && mouseY > y + 111 && mouseY < y + 111 + 18;
	}

}
