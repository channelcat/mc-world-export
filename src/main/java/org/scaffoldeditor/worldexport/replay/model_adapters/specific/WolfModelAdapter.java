package org.scaffoldeditor.worldexport.replay.model_adapters.specific;

import org.scaffoldeditor.worldexport.replay.model_adapters.QuadrupedModelAdapter;
import net.minecraft.client.render.entity.WolfEntityRenderer;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;

public class WolfModelAdapter extends QuadrupedModelAdapter<WolfEntity> {

    public WolfModelAdapter(WolfEntity entity) throws IllegalArgumentException {
        super(entity, null);
    }

    @Override
    protected float getAnimationProgress(float f) {
        return getEntity().getTailAngle();
    }

    @Override
    public Identifier getTexture() {
        return ((WolfEntityRenderer) getEntityRenderer()).getTexture(getEntity());
    }
}