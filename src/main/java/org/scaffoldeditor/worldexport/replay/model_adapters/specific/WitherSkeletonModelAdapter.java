package org.scaffoldeditor.worldexport.replay.model_adapters.specific;

import org.scaffoldeditor.worldexport.mixins.WitherSkeletonEntityRendererAccessor;
import org.scaffoldeditor.worldexport.replay.model_adapters.BipedModelAdapter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.WitherSkeletonEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.util.Identifier;

public class WitherSkeletonModelAdapter<T extends WitherSkeletonEntity> extends BipedModelAdapter<T> {

    private static final Identifier TEXTURE = new Identifier("textures/entity/skeleton/wither_skeleton.png");

    public WitherSkeletonModelAdapter(T entity) throws IllegalArgumentException {
        super(entity, TEXTURE);
        System.out.println("Entered WitherSkeletonModelAdapter");
    }

    @Override
    protected float getScale() {
        MatrixStack matrixStack = new MatrixStack();
        WitherSkeletonEntityRenderer renderer = (WitherSkeletonEntityRenderer) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(getEntity());
        ((WitherSkeletonEntityRendererAccessor) renderer).invokeScale(getEntity(), matrixStack, 0);
        return matrixStack.peek().getPositionMatrix().m00(); // Get the X scale factor
    }
}