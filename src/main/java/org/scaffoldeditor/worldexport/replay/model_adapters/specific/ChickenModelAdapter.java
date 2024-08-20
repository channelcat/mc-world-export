package org.scaffoldeditor.worldexport.replay.model_adapters.specific;

import java.util.Map;

import org.scaffoldeditor.worldexport.mixins.AnimalModelAccessor;
import org.scaffoldeditor.worldexport.replay.model_adapters.AnimalModelAdapter;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ChickenModelAdapter extends AnimalModelAdapter<ChickenEntity> {

    private static final Identifier TEXTURE = new Identifier("textures/entity/chicken.png");

    public ChickenModelAdapter(ChickenEntity entity)
            throws IllegalArgumentException {
        super(entity, TEXTURE);
    }
    
    @Override
    protected float getAnimationProgress(float tickDelta) {
        float flapProgress = MathHelper.lerp(tickDelta, getEntity().prevFlapProgress, getEntity().flapProgress);
        float maxDeviation = MathHelper.lerp(tickDelta, getEntity().prevMaxWingDeviation, getEntity().maxWingDeviation);
        return (MathHelper.sin(flapProgress) + 1) * maxDeviation;
    }
    
    @Override
    protected void extractPartNames(AnimalModel<ChickenEntity> model, Map<ModelPart, String> partNames) {
        if(model instanceof ChickenEntityModel) {
            var accessor = (AnimalModelAccessor) model;

            var iterablePartNames = new String[]{
                    EntityModelPartNames.HEAD,
                    EntityModelPartNames.BEAK,
                    "red_thing",
                    EntityModelPartNames.BODY,
                    EntityModelPartNames.RIGHT_LEG,
                    EntityModelPartNames.LEFT_LEG,
                    EntityModelPartNames.RIGHT_WING,
                    EntityModelPartNames.LEFT_WING
            };

            int i = 0;
            for (var part : accessor.retrieveHeadParts()) {
                partNames.put(part, iterablePartNames[i++]);
            }
            for (var part : accessor.retrieveBodyParts()) {
                partNames.put(part, iterablePartNames[i++]);
            }
            if (i != iterablePartNames.length) {
                throw new IllegalArgumentException("Wrong number of chicken parts!");
            }
        } else {
            throw new IllegalArgumentException("Model is not an instance of ChickenEntityModel");
        }
    }
}
