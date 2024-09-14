package org.scaffoldeditor.worldexport.replay.model_adapters.specific;

import org.scaffoldeditor.worldexport.mixins.GhastEntityRendererAccessor;
import org.scaffoldeditor.worldexport.replay.model_adapters.SinglePartModelAdapter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.GhastEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.GhastEntity;

public class GhastModelAdapter<T extends GhastEntity> extends SinglePartModelAdapter<T> {

    public GhastModelAdapter(T entity) throws IllegalArgumentException {
        super(entity);
        System.out.println("Entered GhastModelAdapter");
    }

    @Override
    protected float getScale() {
        MatrixStack matrixStack = new MatrixStack();
        GhastEntityRenderer renderer = (GhastEntityRenderer) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(getEntity());
        ((GhastEntityRendererAccessor) renderer).invokeScale(getEntity(), matrixStack, 0);
        return matrixStack.peek().getPositionMatrix().m00(); // Get the X scale factor
    }
}