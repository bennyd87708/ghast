package com.benny.ghast;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.command.argument.ColorArgumentType;
import net.minecraft.network.MessageType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.minecraft.command.argument.ColorArgumentType.getColor;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Ghast implements ModInitializer {

    public static final String MOD_ID = "ghast";
    public static final Logger LOGGER = LoggerFactory.getLogger("ghast");

    private static final String KEYBIND_CATEGORY = "key.ghast.category";
    private static final String TOGGLE_KEYBIND = "key.ghast.toggle";
    private static KeyBinding keyToggle;

    private static boolean enabled;

    public static void handleInputEvents()
    {
        while (keyToggle.wasPressed())
            enabled = !enabled;
    }

    public static boolean isEnabled()
    {
        return enabled;
    }

    @Override
    public void onInitialize() {
        KeyBindingRegistryImpl.addCategory(KEYBIND_CATEGORY);
        KeyBindingHelper.registerKeyBinding(keyToggle = new KeyBinding(TOGGLE_KEYBIND, GLFW.GLFW_KEY_F9, KEYBIND_CATEGORY));
        WorldRenderEvents.AFTER_TRANSLUCENT.register(GhastWorldRender::afterTranslucent);
        WorldRenderEvents.END.register(GhastWorldRender::onEnd);
    }
}
