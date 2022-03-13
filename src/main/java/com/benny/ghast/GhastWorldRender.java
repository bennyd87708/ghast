package com.benny.ghast;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
//import carpet.utils.PerimeterDiagnostics;

import java.util.Objects;

public class GhastWorldRender
{
    private static final VertexConsumerProvider vertConsumer;

    static
    {
        vertConsumer = VertexConsumerProvider.immediate(new BufferBuilder(256));
    }

    private static void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f)
    {
        if (!Ghast.isEnabled())
            return;

        var client = MinecraftClient.getInstance();
        if (client == null)
            return;

        var player = client.player;
        var world = client.world;
        if (player == null || world == null)
            return;

        var f = client.textRenderer;
        if (f == null)
            return;

        var pos = camera.getPos();

        matrices.push();

        var showBothValues = client.options.debugEnabled;

        var s = showBothValues ? 1 / 32f : 1 / 16f;

        var frustum = new Frustum(matrices.peek().getPositionMatrix(), RenderSystem.getProjectionMatrix());
        frustum.setPosition(pos.x, pos.y, pos.z);

        var playerPos = player.getBlockPos();
        matrices.translate(-pos.x, -pos.y, -pos.z);
        matrices.translate(playerPos.getX(), playerPos.getY(), playerPos.getZ());

        var mutablePos = playerPos.mutableCopy();
        var q = new Quaternion(Vec3f.POSITIVE_X, -90, true);

        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());

        for (int x = -2; x < 2; x++)
            for (int y = -2; y < 2; y++)
                for (int z = -2; z < 2; z++)
                {
                    var queryPos = mutablePos.set(playerPos, x, y, z);

                    if (!frustum.isVisible(new Box(queryPos)) || !world.isTopSolid(queryPos.down(), player) || world.isTopSolid(queryPos, player))
                        continue;

                    matrices.push();
                    matrices.translate(x, y, z);
                    matrices.multiply(q);
                    matrices.scale(s, -s, 1);

                    var ghastSpawnable = true;//PerimeterDiagnostics.canGhastSpawn(queryPos, Objects.requireNonNull(EntityType.GHAST.create(world)));
                    if(ghastSpawnable){
                        System.out.println("working");
                    }
                    //var ghastSpawnable = world.getBlockState(queryPos).allowsSpawning()
                    var color = 0x00FF00; // spawn never

                    if (ghastSpawnable)
                    {
                        drawNumber(matrices, immediate, f, String.valueOf(10), color, 9, 8);
                    }
                    else
                    {
                        color = 0xFF0000;
                        drawNumber(matrices, immediate, f, String.valueOf(0), color, 9, 8);
                    }

                    matrices.pop();
                }

        matrices.pop();

        RenderSystem.enablePolygonOffset();
        RenderSystem.polygonOffset(-1, -2);

        immediate.draw();

        RenderSystem.disablePolygonOffset();
    }

    private static void drawNumber(MatrixStack matrices, VertexConsumerProvider.Immediate immediate, TextRenderer f, String str, int color, int offsetX, int offsetY)
    {
        matrices.push();
        matrices.translate(offsetX - str.length() * 3.5f, offsetY + 1 - f.fontHeight / 2f, 0);
        f.draw(str, 0, 0, color & 0x3F3F3F, false, matrices.peek().getPositionMatrix(), immediate, false, 0, 0xF000F0, false);
        matrices.translate(-1.1, -0.9, 0.0005);
        f.draw(str, 0, 0, color, false, matrices.peek().getPositionMatrix(), immediate, false, 0, 0xF000F0, false);
        matrices.pop();
    }

    public static void afterTranslucent(WorldRenderContext wrc)
    {
        if (wrc.advancedTranslucency())
        {
            GhastWorldRender.render(wrc.matrixStack(), wrc.tickDelta(), wrc.limitTime(), wrc.blockOutlines(), wrc.camera(), wrc.gameRenderer(), wrc.lightmapTextureManager(), wrc.projectionMatrix());
        }
    }

    public static void onEnd(WorldRenderContext wrc)
    {
        if (!wrc.advancedTranslucency())
        {
            GhastWorldRender.render(wrc.matrixStack(), wrc.tickDelta(), wrc.limitTime(), wrc.blockOutlines(), wrc.camera(), wrc.gameRenderer(), wrc.lightmapTextureManager(), wrc.projectionMatrix());
        }
    }
}