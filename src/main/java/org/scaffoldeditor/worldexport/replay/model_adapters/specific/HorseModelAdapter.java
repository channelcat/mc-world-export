package org.scaffoldeditor.worldexport.replay.model_adapters.specific;

import java.util.Map;

import org.scaffoldeditor.worldexport.mat.MaterialUtils;
import org.scaffoldeditor.worldexport.mixins.AbstractHorseEntityRendererAccessor;
import org.scaffoldeditor.worldexport.mixins.HorseEntityRendererAccessor;
import org.scaffoldeditor.worldexport.replay.feature_adapters.HorseArmorFeatureAdapter;
import org.scaffoldeditor.worldexport.replay.models.ReplayModel.Pose;
import org.scaffoldeditor.worldexport.replay.models.ReplayModelPart;

import com.google.common.collect.Maps;

import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.HorseMarking;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class HorseModelAdapter extends AbstractHorseModelAdapter<HorseEntity> {
    private static final Map<HorseMarking, Identifier> MARKING_TEXTURES = Util.make(Maps.newEnumMap(HorseMarking.class), textures -> {
        textures.put(HorseMarking.NONE, null);
        textures.put(HorseMarking.WHITE, new Identifier("textures/entity/horse/horse_markings_white.png"));
        textures.put(HorseMarking.WHITE_FIELD, new Identifier("textures/entity/horse/horse_markings_whitefield.png"));
        textures.put(HorseMarking.WHITE_DOTS, new Identifier("textures/entity/horse/horse_markings_whitedots.png"));
        textures.put(HorseMarking.BLACK_DOTS, new Identifier("textures/entity/horse/horse_markings_blackdots.png"));
    });

    AbstractHorseEntityRendererAccessor renderAccessor;
    HorseArmorFeatureAdapter armorAdapter;

    public HorseModelAdapter(HorseEntity entity) throws IllegalArgumentException {
        super(entity);
        
        //armorAdapter = new HorseArmorFeatureAdapter(this);
    }

    @Override
    protected String getMaterialName() {
        return MaterialUtils.getTexName(getTexture())+ "." + getEntity().getMarking().name();
    }

    @Override
    public Identifier getTexture() {
        var renderAccessor = (HorseEntityRendererAccessor) getEntityRenderer();
        return renderAccessor.retrieveTexture(getEntity());
    }

    @Override
    public Identifier getOverlayTexture() {
        return MARKING_TEXTURES.get(getEntity().getMarking());
    }
    
    @Override
    protected Pose<ReplayModelPart> writePose(float tickDelta) {
        Pose<ReplayModelPart> pose = super.writePose(tickDelta);
        //armorAdapter.writePose(pose, tickDelta);

        return pose;
    }
}