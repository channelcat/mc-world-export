package org.scaffoldeditor.worldexport.replay.model_adapters;

import org.scaffoldeditor.worldexport.mat.MaterialConsumer;
import org.scaffoldeditor.worldexport.replay.feature_adapters.ArmorFeatureAdapter;
import org.scaffoldeditor.worldexport.replay.feature_adapters.HeldItemFeatureAdapter;
import org.scaffoldeditor.worldexport.replay.models.MultipartReplayModel;
import org.scaffoldeditor.worldexport.replay.models.ReplayModel.Pose;
import org.scaffoldeditor.worldexport.replay.models.ReplayModelPart;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

/**
 * An animal model adapter that can render items and armor
 */
public class BipedModelAdapter<T extends LivingEntity> extends AnimalModelAdapter<T> {
    private static final String[] HEAD_PARTS = {
        EntityModelPartNames.HEAD
    };
    private static final String[] BODY_PARTS = {
        EntityModelPartNames.BODY,
        EntityModelPartNames.RIGHT_ARM,
        EntityModelPartNames.LEFT_ARM,
        EntityModelPartNames.RIGHT_LEG,
        EntityModelPartNames.LEFT_LEG,
        EntityModelPartNames.HAT
    };

    public BipedModelAdapter(T entity, Identifier texture) throws IllegalArgumentException {
        super(entity, texture);
    }
    
    @Override
    protected String[] getHeadPartNames() { return HEAD_PARTS; }
    @Override
    protected String[] getBodyPartNames() { return BODY_PARTS; }

    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static class BipedModelFactory<U extends LivingEntity> implements ReplayModelAdapterFactory<U> {
        Identifier texture;

        public BipedModelFactory(Identifier texture) {
            this.texture = texture;
        }

        @Override
        public BipedModelAdapter<U> create(U entity) {
            return new BipedModelAdapter<U>(entity, texture);
        }

    }

    protected HeldItemFeatureAdapter heldItemAdapter;
    protected ArmorFeatureAdapter armorAdapter;


    @Override
    protected MultipartReplayModel captureBaseModel(AnimalModel<T> model) {
        MultipartReplayModel rModel = super.captureBaseModel(model);
        heldItemAdapter = new HeldItemFeatureAdapter(getEntity(), rModel);

        ModelPart leggingsModel = client.getEntityModelLoader().getModelPart(getInnerArmorLayer());
        ModelPart armorModel = client.getEntityModelLoader().getModelPart(getOuterArmorLayer());
        armorAdapter = new ArmorFeatureAdapter(this,
                new BipedEntityModel<>(leggingsModel), new BipedEntityModel<>(armorModel));

        return rModel;
    }
    
    @Override
    protected Pose<ReplayModelPart> writePose(float tickDelta) {
        Pose<ReplayModelPart> pose = super.writePose(tickDelta);
        heldItemAdapter.writePose(pose, tickDelta);
        armorAdapter.writePose(pose, tickDelta);

        return pose;
    }

    @Override
    public void generateMaterials(MaterialConsumer file) {
        super.generateMaterials(file);
        heldItemAdapter.generateMaterials(file);
        armorAdapter.generateMaterials(file);
    }

    public boolean isSlim() {
        return false;
    }

    /**
     * Get this entity's inner armor model layer.
     * @return Inner armor model layer.
     */
    protected EntityModelLayer getInnerArmorLayer() {
        return isSlim() ? EntityModelLayers.PLAYER_SLIM_INNER_ARMOR : EntityModelLayers.PLAYER_INNER_ARMOR;
    }

    /**
     * Get this entity's outer armor model layer.
     * @return Outer armor model layer.
     */
    protected EntityModelLayer getOuterArmorLayer() {
        return isSlim() ? EntityModelLayers.PLAYER_SLIM_OUTER_ARMOR : EntityModelLayers.PLAYER_OUTER_ARMOR;
    }

    public ReplayModelPart getHead() {
        return getModel().getBone(EntityModelPartNames.HEAD);
    }

    public ReplayModelPart getHat() {
        return getModel().getBone(EntityModelPartNames.HAT);
    }

    public ReplayModelPart getBody() {
        return getModel().getBone(EntityModelPartNames.BODY);
    }

    public ReplayModelPart getRightArm() {
        return getModel().getBone(EntityModelPartNames.RIGHT_ARM);
    }

    public ReplayModelPart getLeftArm() {
        return getModel().getBone(EntityModelPartNames.LEFT_ARM);
    }

    public ReplayModelPart getRightLeg() {
        return getModel().getBone(EntityModelPartNames.RIGHT_LEG);
    }

    public ReplayModelPart getLeftLeg() {
        return getModel().getBone(EntityModelPartNames.LEFT_LEG);
    }
}
