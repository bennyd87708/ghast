package com.benny.ghast.mixin;

import com.benny.ghast.Ghast;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class GhastMixin {
    @Inject(method = "handleInputEvents", at = @At("HEAD"))
    private void handleInputEvents(CallbackInfo ci)
    {
        Ghast.handleInputEvents();
    }
}