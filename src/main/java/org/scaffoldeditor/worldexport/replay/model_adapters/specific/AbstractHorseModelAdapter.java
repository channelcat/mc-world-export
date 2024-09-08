package org.scaffoldeditor.worldexport.replay.model_adapters.specific;

import java.util.List;

import org.scaffoldeditor.worldexport.mixins.AbstractHorseEntityRendererAccessor;
import org.scaffoldeditor.worldexport.mixins.HorseEntityModelAccessor;
import org.scaffoldeditor.worldexport.replay.model_adapters.AnimalModelAdapter;
import org.scaffoldeditor.worldexport.replay.models.ReplayModel.Pose;
import org.scaffoldeditor.worldexport.replay.models.ReplayModelPart;
import org.scaffoldeditor.worldexport.replay.models.Transform;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.util.Identifier;

public class AbstractHorseModelAdapter<T extends AbstractHorseEntity> extends AnimalModelAdapter<T> {
    protected static final String HEAD_PART = "head_parts";
    protected static final String LEFT_HIND_BABY_LEG = "left_hind_baby_leg";
    protected static final String RIGHT_HIND_BABY_LEG = "right_hind_baby_leg";
    protected static final String LEFT_FRONT_BABY_LEG = "left_front_baby_leg";
    protected static final String RIGHT_FRONT_BABY_LEG = "right_front_baby_leg";
    protected static final String SADDLE = "saddle";
    protected static final String LEFT_SADDLE_MOUTH = "left_saddle_mouth";
    protected static final String LEFT_SADDLE_LINE = "left_saddle_line";
    protected static final String RIGHT_SADDLE_MOUTH = "right_saddle_mouth";
    protected static final String RIGHT_SADDLE_LINE = "right_saddle_line";
    protected static final String HEAD_SADDLE = "head_saddle";
    protected static final String MOUTH_SADDLE_WRAP = "mouth_saddle_wrap";
    protected static final String UPPER_MOUTH = "upper_mouth";

    private AbstractHorseEntityRendererAccessor renderAccessor;

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

    public AbstractHorseModelAdapter(T entity) throws IllegalArgumentException {
        super(entity);
        init();
    }

    public AbstractHorseModelAdapter(T entity, Identifier texture) throws IllegalArgumentException {
        super(entity, texture);
        init();
    }

    private void init() {
        renderAccessor = (AbstractHorseEntityRendererAccessor) getEntityRenderer();
    }
    
    // @Override
    // protected Pose<ReplayModelPart> writePose(float tickDelta) {
    //     Pose<ReplayModelPart> pose = super.writePose(tickDelta);

    //     var horseAccessor = (HorseEntityModelAccessor) getEntityModel();
    //     var entity = getEntity();
    //     boolean isSaddled = entity.isSaddled();
    //     boolean hasPassengers = entity.hasPassengers();
    //     for (ModelPart modelPart : horseAccessor.getSaddle()) {
    //         var part = boneMapping.get(modelPart);
    //         System.out.println("Saddle? " + isSaddled + " - " + part);
    //         pose.bones.put(part, new Transform(isSaddled));
    //     }
    //     for (ModelPart modelPart : horseAccessor.getStraps()) {
    //         var part = boneMapping.get(modelPart);
    //         System.out.println("Straps? " + (isSaddled && hasPassengers) + " - " + part);
    //         pose.bones.put(part, new Transform(isSaddled && hasPassengers));
    //     }

    //     return pose;
    // }

    @Override
    protected String[] getHeadPartNames() { return HEAD_PARTS; }
    @Override
    protected String[] getBodyPartNames() { return BODY_PARTS; }
    @Override
    protected List<ModelPartRelation> getAdditionalModelPartRelations() { return ADDITIONAL_MODEL_PARTS; }
    
    @Override
    protected float getScale() {
        return renderAccessor.getScale();
    }
}
