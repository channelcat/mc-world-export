package org.scaffoldeditor.worldexport.replay.model_adapters.specific;

import org.scaffoldeditor.worldexport.mixins.AbstractHorseEntityRendererAccessor;

import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.util.Identifier;

public class DonkeyModelAdapter extends AbstractHorseModelAdapter<AbstractDonkeyEntity> {

    private static Identifier texture = new Identifier("textures/entity/horse/donkey.png");

    AbstractHorseEntityRendererAccessor renderAccessor;

    public DonkeyModelAdapter(AbstractDonkeyEntity entity) throws IllegalArgumentException {
        super(entity, texture);
    }
}