package org.scaffoldeditor.worldexport.replay.model_adapters.specific;

import org.scaffoldeditor.worldexport.mixins.MagmaCubeEntityRendererAccessor;
import org.scaffoldeditor.worldexport.replay.model_adapters.SinglePartModelAdapter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.MagmaCubeEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.util.math.MathHelper;

public class MagmaCubeModelAdapter<T extends MagmaCubeEntity> extends SinglePartModelAdapter<T> {

    public MagmaCubeModelAdapter(T entity) throws IllegalArgumentException {
        super(entity);
        System.out.println("Entered MagmaCubeModelAdapter");
    }

    @Override
    protected float getScale() {
        MatrixStack matrixStack = new MatrixStack();
        MagmaCubeEntityRenderer renderer = (MagmaCubeEntityRenderer) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(getEntity());
        ((MagmaCubeEntityRendererAccessor) renderer).invokeScale(getEntity(), matrixStack, 0);
        return matrixStack.peek().getPositionMatrix().m00(); // Get the X scale factor
    }
}