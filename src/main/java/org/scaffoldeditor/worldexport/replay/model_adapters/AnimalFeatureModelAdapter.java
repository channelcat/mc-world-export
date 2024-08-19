package org.scaffoldeditor.worldexport.replay.model_adapters;

import org.scaffoldeditor.worldexport.mixins.LivingEntityRendererAccessor;
import org.scaffoldeditor.worldexport.mixins.SheepWoolFeatureRendererAccessor;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

/**
 * Umbrella model adapter that works specifically with entities which use Animal Models.
 */
public class AnimalFeatureModelAdapter<T extends LivingEntity> extends AnimalModelAdapter<T> {

    public AnimalFeatureModelAdapter(T entity, Identifier texture) throws IllegalArgumentException {
        super(entity, texture);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected AnimalModel<T> extractModel(LivingEntityRenderer<? super T, ?> entityRenderer) throws ClassCastException {
        var feature = ((LivingEntityRendererAccessor<? super T, ?>) entityRenderer).getFeatures().getFirst();
        if (feature instanceof SheepWoolFeatureRenderer) {
            return (AnimalModel<T>) ((SheepWoolFeatureRendererAccessor) feature).getModel();
        }
        throw new ClassCastException("Unsupported feature type: " + feature.getClass().getName());
    }
}
