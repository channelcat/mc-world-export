package org.scaffoldeditor.worldexport.replay.model_adapters.specific;

import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.ApiStatus.OverrideOnly;
import org.joml.Vector3d;
import org.scaffoldeditor.worldexport.replay.model_adapters.AnimalModelAdapter;

import com.google.common.collect.Maps;

import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.entity.passive.HorseColor;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class HorseModelAdapter extends AnimalModelAdapter<HorseEntity> {
    private static final String HEAD_PART = "head_parts";
    private static final String LEFT_HIND_BABY_LEG = "left_hind_baby_leg";
    private static final String RIGHT_HIND_BABY_LEG = "right_hind_baby_leg";
    private static final String LEFT_FRONT_BABY_LEG = "left_front_baby_leg";
    private static final String RIGHT_FRONT_BABY_LEG = "right_front_baby_leg";
    private static final String SADDLE = "saddle";
    private static final String LEFT_SADDLE_MOUTH = "left_saddle_mouth";
    private static final String LEFT_SADDLE_LINE = "left_saddle_line";
    private static final String RIGHT_SADDLE_MOUTH = "right_saddle_mouth";
    private static final String RIGHT_SADDLE_LINE = "right_saddle_line";
    private static final String HEAD_SADDLE = "head_saddle";
    private static final String MOUTH_SADDLE_WRAP = "mouth_saddle_wrap";
    private static final String UPPER_MOUTH = "upper_mouth";

    private static final String[] HEAD_PARTS = {
        HEAD_PART
    };
    private static final String[] BODY_PARTS = {
        EntityModelPartNames.BODY,
        EntityModelPartNames.RIGHT_HIND_LEG,
        EntityModelPartNames.LEFT_HIND_LEG,
        EntityModelPartNames.RIGHT_FRONT_LEG,
        EntityModelPartNames.LEFT_FRONT_LEG,
        RIGHT_HIND_BABY_LEG,
        LEFT_HIND_BABY_LEG,
        RIGHT_FRONT_BABY_LEG,
        LEFT_FRONT_BABY_LEG
    };
    private static final List<ModelPartRelation> ADDITIONAL_MODEL_PARTS = List.of(
        new ModelPartRelation(EntityModelPartNames.BODY, EntityModelPartNames.TAIL),
        new ModelPartRelation(EntityModelPartNames.BODY, SADDLE),
        new ModelPartRelation(HEAD_PART, HEAD_SADDLE),
        new ModelPartRelation(HEAD_PART, MOUTH_SADDLE_WRAP),
        new ModelPartRelation(HEAD_PART, LEFT_SADDLE_MOUTH),
        new ModelPartRelation(HEAD_PART, RIGHT_SADDLE_MOUTH),
        new ModelPartRelation(HEAD_PART, LEFT_SADDLE_LINE),
        new ModelPartRelation(HEAD_PART, RIGHT_SADDLE_LINE),
        new ModelPartRelation(HEAD_PART, UPPER_MOUTH),
        new ModelPartRelation(HEAD_PART, EntityModelPartNames.HEAD),
        new ModelPartRelation(HEAD_PART, EntityModelPartNames.MANE),
        new ModelPartRelation(EntityModelPartNames.HEAD, EntityModelPartNames.LEFT_EAR),
        new ModelPartRelation(EntityModelPartNames.HEAD, EntityModelPartNames.RIGHT_EAR)
          
    );

    private static final Map<HorseColor, Identifier> TEXTURES = Util.make(Maps.newEnumMap(HorseColor.class),
            enumMap -> {
                enumMap.put(HorseColor.WHITE, new Identifier("textures/entity/horse/horse_white.png"));
                enumMap.put(HorseColor.CREAMY, new Identifier("textures/entity/horse/horse_creamy.png"));
                enumMap.put(HorseColor.CHESTNUT, new Identifier("textures/entity/horse/horse_chestnut.png"));
                enumMap.put(HorseColor.BROWN, new Identifier("textures/entity/horse/horse_brown.png"));
                enumMap.put(HorseColor.BLACK, new Identifier("textures/entity/horse/horse_black.png"));
                enumMap.put(HorseColor.GRAY, new Identifier("textures/entity/horse/horse_gray.png"));
                enumMap.put(HorseColor.DARK_BROWN, new Identifier("textures/entity/horse/horse_darkbrown.png"));
            });

    public HorseModelAdapter(HorseEntity entity) throws IllegalArgumentException {
        super(entity, TEXTURES.get(entity.getVariant()));
    }

    @Override
    protected String[] getHeadPartNames() { return HEAD_PARTS; }
    @Override
    protected String[] getBodyPartNames() { return BODY_PARTS; }
    @Override
    protected List<ModelPartRelation> getAdditionalModelPartRelations() { return ADDITIONAL_MODEL_PARTS; }
    
    @Override
    protected float getScale() {
        return 0.75f * 1.1f;
    }
}
