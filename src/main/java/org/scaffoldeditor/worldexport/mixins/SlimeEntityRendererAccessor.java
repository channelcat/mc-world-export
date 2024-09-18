package org.scaffoldeditor.worldexport.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.entity.SlimeEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.SlimeEntity;

@Mixin(SlimeEntityRenderer.class)
public interface SlimeEntityRendererAccessor {
    
    @Invoker("scale")
    void invokeScale(SlimeEntity slimeEntity, MatrixStack matrixStack, float f);
}