package org.scaffoldeditor.worldexport.replay.model_adapters.specific;

import org.scaffoldeditor.worldexport.mixins.AbstractHorseEntityRendererAccessor;

import net.minecraft.entity.passive.MuleEntity;
import net.minecraft.util.Identifier;

public class MuleModelAdapter extends AbstractHorseModelAdapter<MuleEntity> {

    private static Identifier texture = new Identifier("textures/entity/horse/mule.png");

    AbstractHorseEntityRendererAccessor renderAccessor;

    public MuleModelAdapter(MuleEntity entity) throws IllegalArgumentException {
        super(entity, texture);
    }
}