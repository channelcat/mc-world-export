package org.scaffoldeditor.worldexport.replay.model_adapters.specific;

import org.scaffoldeditor.worldexport.replay.model_adapters.QuadrupedModelAdapter;

import net.minecraft.client.render.entity.OcelotEntityRenderer;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.util.Identifier;

public class OcelotModelAdapter extends QuadrupedModelAdapter<OcelotEntity> {

    public OcelotModelAdapter(OcelotEntity entity) throws IllegalArgumentException {
        super(entity, null);
    }

    @Override
    public Identifier getTexture() {
        return ((OcelotEntityRenderer) getEntityRenderer()).getTexture(getEntity());
    }
}