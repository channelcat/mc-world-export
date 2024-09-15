package org.scaffoldeditor.worldexport.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.entity.WitherSkeletonEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.AbstractSkeletonEntity;


@Mixin(WitherSkeletonEntityRenderer.class)
public interface WitherSkeletonEntityRendererAccessor{

    @Invoker("scale")
    void invokeScale(AbstractSkeletonEntity abstractSkeletonEntity, MatrixStack matrixStack, float f);

}