package org.scaffoldeditor.worldexport.replay.feature_adapters;

import java.util.HashMap;
import java.util.Map;

import org.scaffoldeditor.worldexport.mat.Material;
import org.scaffoldeditor.worldexport.mat.MaterialConsumer;
import org.scaffoldeditor.worldexport.mat.MaterialUtils;
import org.scaffoldeditor.worldexport.mat.PromisedReplayTexture;
import org.scaffoldeditor.worldexport.mat.ReplayTexture;
import org.scaffoldeditor.worldexport.mixins.QuadrupedModelAccessor;
import org.scaffoldeditor.worldexport.replay.model_adapters.specific.SheepModelAdapter;
import org.scaffoldeditor.worldexport.replay.models.MultipartReplayModel;
import org.scaffoldeditor.worldexport.replay.models.ReplayModel.Pose;
import org.scaffoldeditor.worldexport.replay.models.ReplayModelPart;
import org.scaffoldeditor.worldexport.replay.models.Transform;
import org.scaffoldeditor.worldexport.util.MeshUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.SheepWoolEntityModel;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import org.joml.Vector3d;

public class SheepWoolFeatureAdapter {
    private static final Identifier SKIN = new Identifier("textures/entity/sheep/sheep_fur.png");
    
    MinecraftClient minecraftClient = MinecraftClient.getInstance();
    SheepWoolEntityModel<SheepEntity> woolEntityModel;
    MultipartReplayModel baseModel;
    SheepModelAdapter sheep;
    private final Map<ReplayModelPart, ReplayModelPart> modelPartCache = new HashMap<>();

    public SheepWoolFeatureAdapter(SheepModelAdapter parent) {
        this.sheep = parent;
        // TODO: get this from the renderer instead of duping
        var furPart = minecraftClient.getEntityModelLoader().getModelPart(EntityModelLayers.SHEEP_FUR);
        this.woolEntityModel = new SheepWoolEntityModel<SheepEntity>(furPart);
    }

    public void writePose(Pose<ReplayModelPart> pose, float tickDelta) {
        QuadrupedModelAccessor woolAccessor = (QuadrupedModelAccessor) woolEntityModel;
        
        var materialId = getMaterialId();

        var sheepEntity = sheep.getEntity();
        var isVisible = !sheepEntity.isSheared() && !sheepEntity.isInvisible();

        // TODO: Use a feature model adapter instead of manually overwriting model parts
        //        Not sure if that would be a better design, but something to consider
        writePosePart(pose, sheep.getHead(), woolAccessor.getHead(), EntityModelPartNames.HEAD, materialId, isVisible);
        writePosePart(pose, sheep.getBody(), woolAccessor.getBody(), EntityModelPartNames.BODY, materialId, isVisible);
        writePosePart(pose, sheep.getRightHindLeg(), woolAccessor.getRightHindLeg(), EntityModelPartNames.RIGHT_HIND_LEG, materialId, isVisible);
        writePosePart(pose, sheep.getLeftHindLeg(), woolAccessor.getLeftHindLeg(), EntityModelPartNames.LEFT_HIND_LEG, materialId, isVisible);
        writePosePart(pose, sheep.getRightFrontLeg(), woolAccessor.getRightFrontLeg(), EntityModelPartNames.RIGHT_FRONT_LEG, materialId, isVisible);
        writePosePart(pose, sheep.getLeftFrontLeg(), woolAccessor.getLeftFrontLeg(), EntityModelPartNames.LEFT_FRONT_LEG, materialId, isVisible);
    }

    public void writePosePart(Pose<ReplayModelPart> pose, ReplayModelPart replayModelPart, ModelPart woolPart, String partName, String materialId, boolean isVisible) {
        // If a wool part doesn't exist, create it
        var woolReplayPart = modelPartCache.get(replayModelPart);
        if (woolReplayPart == null) {
            woolReplayPart = new ReplayModelPart("sheep_wool."+partName);
            woolReplayPart.getMesh().setActiveMaterialGroupName(materialId);
            // Apply the sheep wool part to this mesh
            MeshUtils.appendModelPart(woolPart, woolReplayPart.getMesh(), false, null);
            // Attach this to the corresponding parent model part
            replayModelPart.children.add(woolReplayPart);
            modelPartCache.put(replayModelPart, woolReplayPart);
        }

        pose.bones.put(woolReplayPart, new Transform(isVisible));
    }

    public Vector3d getWoolColor() {
        // TODO: change colors when wool changes
        var sheepEntity = sheep.getEntity();

        // TODO: h = tick for rainbow?
        float h = 0; // tickDelta * 20.0f;

        float u;
        float t;
        float s;
        
        if (sheepEntity.isInvisible()) {
            if (minecraftClient.hasOutline(sheepEntity)) {
                // TODO: outline code?
            }
            return new Vector3d(1.0f, 1.0f, 1.0f);
        }
        // Rainbow sheep
        // Not supported yet since we generate materials only once
        if (sheepEntity.hasCustomName() && "jeb_".equals(sheepEntity.getName().getString())) {
            int n = sheepEntity.age / 25 + sheepEntity.getId();
            int o = DyeColor.values().length;
            int p = n % o;
            int q = (n + 1) % o;
            float r = ((float)(sheepEntity.age % 25) + h) / 25.0f;
            float[] fs = SheepEntity.getRgbColor(DyeColor.byId(p));
            float[] gs = SheepEntity.getRgbColor(DyeColor.byId(q));
            s = fs[0] * (1.0f - r) + gs[0] * r;
            t = fs[1] * (1.0f - r) + gs[1] * r;
            u = fs[2] * (1.0f - r) + gs[2] * r;
        } else {
            float[] hs = SheepEntity.getRgbColor(sheepEntity.getColor());
            s = hs[0];
            t = hs[1];
            u = hs[2];
        }
        return new Vector3d(s, t, u);
    }

    public String getMaterialId() {
        return SKIN + "." + sheep.getEntity().getColor().getName();
    }

    public void generateMaterials(MaterialConsumer consumer) {

        String texName = MaterialUtils.getTexName(SKIN);
        var materialId = getMaterialId();

        Material material = new Material();
        material.setColor(texName);
        // Multiply with wool color
        material.setColor2(getWoolColor());
        material.setRoughness(1);
        material.setTransparent(true);

        ReplayTexture rTexture = new PromisedReplayTexture(SKIN);

        consumer.addTexture(texName, rTexture);
        consumer.addMaterial(materialId, material);
    }
}
