package org.scaffoldeditor.worldexport.replay.model_adapters.specific;

import org.scaffoldeditor.worldexport.replay.model_adapters.QuadrupedModelAdapter;

import net.minecraft.client.render.entity.FoxEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.Identifier;

import java.util.List;

public class FoxModelAdapter extends QuadrupedModelAdapter<FoxEntity> {
    private static final List<ModelPartRelation> ADDITIONAL_MODEL_PARTS = List.of(
        new ModelPartRelation(EntityModelPartNames.BODY, EntityModelPartNames.TAIL)
    );

    public FoxModelAdapter(FoxEntity entity) throws IllegalArgumentException {
        super(entity, null);
    }

    @Override
    protected List<ModelPartRelation> getAdditionalModelPartRelations() {
        return ADDITIONAL_MODEL_PARTS;
    }

    @Override
    public Identifier getTexture() {
        return ((FoxEntityRenderer) getEntityRenderer()).getTexture(getEntity());
    }
}