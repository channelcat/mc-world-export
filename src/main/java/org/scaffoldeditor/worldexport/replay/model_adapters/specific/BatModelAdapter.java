package org.scaffoldeditor.worldexport.replay.model_adapters.specific;

import org.scaffoldeditor.worldexport.replay.model_adapters.LivingEntityModelAdapter;
import org.scaffoldeditor.worldexport.replay.model_adapters.SinglePartModelAdapter;

import org.scaffoldeditor.worldexport.replay.models.ReplayModel.Pose;
import org.scaffoldeditor.worldexport.replay.models.ReplayModelPart;
import org.scaffoldeditor.worldexport.mixins.BatEntityModelAccessor;

import net.minecraft.client.render.entity.AxolotlEntityRenderer;

import net.minecraft.client.render.entity.BatEntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.model.BatEntityModel;

import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.client.render.entity.model.ChickenEntityModel;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import org.joml.Vector3d;

import java.util.Collections;
import java.util.Map;

import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.Transformation;

public class BatModelAdapter extends LivingEntityModelAdapter<BatEntity, BatEntityModel> {

    private static final Identifier BAT_TEXTURE = new Identifier("textures/entity/bat.png");

    public BatModelAdapter(BatEntity entity) {
        super(entity);
    }

    @Override
    protected BatEntityModel extractModel(LivingEntityRenderer<? super BatEntity, ?> entityRenderer) {
        if (entityRenderer instanceof BatEntityRenderer) {
            return ((BatEntityRenderer) entityRenderer).getModel();
        }
        throw new IllegalArgumentException("Renderer is not a BatEntityRenderer");
    }

    @Override
    public Identifier getTexture() {
        return BAT_TEXTURE;
    }

    @Override
    protected void extractPartNames(BatEntityModel model, Map<ModelPart, String> dest) {
        dest.put(model.getPart(), "root");
        BatEntityModelAccessor accessor = (BatEntityModelAccessor) model;
        dest.put(accessor.getHead(), "head");
        dest.put(accessor.getBody(), "body");
        dest.put(accessor.getRightWing(), "right_wing");
        dest.put(accessor.getLeftWing(), "left_wing");
        dest.put(accessor.getRightWingTip(), "right_wing_tip");
        dest.put(accessor.getLeftWingTip(), "left_wing_tip");
    }

    @Override
    protected Iterable<Pair<String, ModelPart>> getRootParts() {
        return Collections.singletonList(new Pair<>("root", getEntityModel().getPart()));
    }

    @Override
    protected Pose<ReplayModelPart> writePose(float tickDelta) {
        Pose<ReplayModelPart> pose = super.writePose(tickDelta);
        // All animation code has been removed from here
        return pose;
    }

    /* 
    @Environment(value=EnvType.CLIENT)
    public class BatAnimations {
        public static final Animation ROOSTING = Animation.Builder.create(0.5f).looping().addBoneAnimation("head", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(180.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("head", new Transformation(Transformation.Targets.TRANSLATE, new Keyframe(0.0f, AnimationHelper.createTranslationalVector(0.0f, 0.5f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("body", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(180.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("body", new Transformation(Transformation.Targets.TRANSLATE, new Keyframe(0.0f, AnimationHelper.createTranslationalVector(0.0f, 0.5f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("feet", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("right_wing", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(0.0f, -10.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("right_wing", new Transformation(Transformation.Targets.TRANSLATE, new Keyframe(0.0f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 1.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("right_wing_tip", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(0.0f, -120.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("left_wing", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(0.0f, 10.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("left_wing", new Transformation(Transformation.Targets.TRANSLATE, new Keyframe(0.0f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 1.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("left_wing_tip", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(0.0f, 120.0f, 0.0f), Transformation.Interpolations.LINEAR))).build();
        public static final Animation FLYING = Animation.Builder.create(0.5f).looping().addBoneAnimation("head", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.125f, AnimationHelper.createRotationalVector(20.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.5f, AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("head", new Transformation(Transformation.Targets.TRANSLATE, new Keyframe(0.0f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.125f, AnimationHelper.createTranslationalVector(0.0f, 2.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0.0f, 1.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.375f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.4583f, AnimationHelper.createTranslationalVector(0.0f, -1.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("body", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(40.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.25f, AnimationHelper.createRotationalVector(52.5f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.5f, AnimationHelper.createRotationalVector(40.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("body", new Transformation(Transformation.Targets.TRANSLATE, new Keyframe(0.0f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.125f, AnimationHelper.createTranslationalVector(0.0f, 2.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0.0f, 1.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.375f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.4583f, AnimationHelper.createTranslationalVector(0.0f, -1.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("feet", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(10.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.125f, AnimationHelper.createRotationalVector(-21.25f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.25f, AnimationHelper.createRotationalVector(-12.5f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.5f, AnimationHelper.createRotationalVector(10.0f, 0.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("right_wing", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(0.0f, 85.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.125f, AnimationHelper.createRotationalVector(0.0f, -55.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.25f, AnimationHelper.createRotationalVector(0.0f, 50.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.375f, AnimationHelper.createRotationalVector(0.0f, 70.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.5f, AnimationHelper.createRotationalVector(0.0f, 85.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("right_wing_tip", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(0.0f, 10.5f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.0417f, AnimationHelper.createRotationalVector(0.0f, 65.5f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.2083f, AnimationHelper.createRotationalVector(0.0f, -135.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.5f, AnimationHelper.createRotationalVector(0.0f, 10.5f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("left_wing", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(0.0f, -85.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.125f, AnimationHelper.createRotationalVector(0.0f, 55.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.25f, AnimationHelper.createRotationalVector(0.0f, -50.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.375f, AnimationHelper.createRotationalVector(0.0f, -70.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.5f, AnimationHelper.createRotationalVector(0.0f, -85.0f, 0.0f), Transformation.Interpolations.LINEAR))).addBoneAnimation("left_wing_tip", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.createRotationalVector(0.0f, -10.5f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.0417f, AnimationHelper.createRotationalVector(0.0f, -65.5f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.2083f, AnimationHelper.createRotationalVector(0.0f, 135.0f, 0.0f), Transformation.Interpolations.LINEAR), new Keyframe(0.5f, AnimationHelper.createRotationalVector(0.0f, -10.5f, 0.0f), Transformation.Interpolations.LINEAR))).build();
    }

    @Override
    public void setAngles(BatEntity batEntity, float f, float g, float h, float i, float j) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        if (batEntity.isRoosting()) {
            this.setRoostingHeadAngles(i);
        }
        this.updateAnimation(batEntity.flyingAnimationState, BatAnimations.FLYING, h, 1.0f);
        this.updateAnimation(batEntity.roostingAnimationState, BatAnimations.ROOSTING, h, 1.0f);
    }

    */
}