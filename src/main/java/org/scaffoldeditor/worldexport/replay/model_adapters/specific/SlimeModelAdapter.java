package org.scaffoldeditor.worldexport.replay.model_adapters.specific;

import org.scaffoldeditor.worldexport.mixins.SlimeEntityRendererAccessor;
import org.scaffoldeditor.worldexport.replay.model_adapters.SinglePartModelAdapter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.SlimeEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.math.MathHelper;

public class SlimeModelAdapter<T extends SlimeEntity> extends SinglePartModelAdapter<T> {

    public SlimeModelAdapter(T entity) throws IllegalArgumentException {
        super(entity);
        System.out.println("Entered SlimeModelAdapter");
    }

    @Override
    protected float getScale() {
        MatrixStack matrixStack = new MatrixStack();
        SlimeEntityRenderer renderer = (SlimeEntityRenderer) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(getEntity());
        ((SlimeEntityRendererAccessor) renderer).invokeScale(getEntity(), matrixStack, 0);
        return matrixStack.peek().getPositionMatrix().m00(); // Get the X scale factor
    }
}