package org.scaffoldeditor.worldexport.replay.model_adapters;

import org.scaffoldeditor.worldexport.replay.models.ReplayModelPart;

import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

/**
 * An animal model adapter that can render items and armor
 */
public class QuadrupedModelAdapter<T extends LivingEntity> extends AnimalModelAdapter<T> {
    private static final String[] HEAD_PARTS = {
        EntityModelPartNames.HEAD
    };
    private static final String[] BODY_PARTS = {
        EntityModelPartNames.BODY,
        EntityModelPartNames.RIGHT_HIND_LEG,
        EntityModelPartNames.LEFT_HIND_LEG,
        EntityModelPartNames.RIGHT_FRONT_LEG,
        EntityModelPartNames.LEFT_FRONT_LEG
    };

    public QuadrupedModelAdapter(T entity, Identifier texture) throws IllegalArgumentException {
        super(entity, texture);
    }

    public static class QuadrupedModelFactory<U extends LivingEntity> implements ReplayModelAdapterFactory<U> {
        Identifier texture;

        public QuadrupedModelFactory(Identifier texture) {
            this.texture = texture;
        }

        @Override
        public QuadrupedModelAdapter<U> create(U entity) {
            return new QuadrupedModelAdapter<U>(entity, texture);
        }

    }

    @Override
    protected String[] getHeadPartNames() { return HEAD_PARTS; }
    @Override
    protected String[] getBodyPartNames() { return BODY_PARTS; }

    public ReplayModelPart getBody() {
        return getModel().getBone(EntityModelPartNames.BODY);
    }

    public ReplayModelPart getHead() {
        return getModel().getBone(EntityModelPartNames.HEAD);
    }

    public ReplayModelPart getRightHindLeg() {
        return getModel().getBone(EntityModelPartNames.RIGHT_HIND_LEG);
    }

    public ReplayModelPart getLeftHindLeg() {
        return getModel().getBone(EntityModelPartNames.LEFT_HIND_LEG);
    }

    public ReplayModelPart getRightFrontLeg() {
        return getModel().getBone(EntityModelPartNames.RIGHT_FRONT_LEG);
    }

    public ReplayModelPart getLeftFrontLeg() {
        return getModel().getBone(EntityModelPartNames.LEFT_FRONT_LEG);
    }
}
