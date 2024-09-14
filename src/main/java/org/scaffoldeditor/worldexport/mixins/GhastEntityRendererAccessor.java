package org.scaffoldeditor.worldexport.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.entity.GhastEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.GhastEntity;

@Mixin(GhastEntityRenderer.class)
public interface GhastEntityRendererAccessor {
    
    @Invoker("scale")
    void invokeScale(GhastEntity ghastEntity, MatrixStack matrixStack, float f);
}