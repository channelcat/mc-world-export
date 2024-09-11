package org.scaffoldeditor.worldexport.replay.model_adapters.specific;

import org.scaffoldeditor.worldexport.replay.model_adapters.QuadrupedModelAdapter;
import net.minecraft.client.render.entity.CatEntityRenderer;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.util.Identifier;

public class CatModelAdapter extends QuadrupedModelAdapter<CatEntity> {

    public CatModelAdapter(CatEntity entity) throws IllegalArgumentException {
        super(entity, null);
    }

    @Override
    public Identifier getTexture() {
        return ((CatEntityRenderer) getEntityRenderer()).getTexture(getEntity());
    }
}