package org.scaffoldeditor.worldexport.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.entity.MagmaCubeEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MagmaCubeEntity;

@Mixin(MagmaCubeEntityRenderer.class)
public interface MagmaCubeEntityRendererAccessor {
    
    @Invoker("scale")
    void invokeScale(MagmaCubeEntity magmaCubeEntity, MatrixStack matrixStack, float f);
}