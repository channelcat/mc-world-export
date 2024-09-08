package org.scaffoldeditor.worldexport.replay.feature_adapters;

import java.util.HashMap;
import java.util.Map;

import org.scaffoldeditor.worldexport.mat.Material;
import org.scaffoldeditor.worldexport.mat.Material.BlendMode;
import org.scaffoldeditor.worldexport.mat.MaterialConsumer;
import org.scaffoldeditor.worldexport.mat.MaterialUtils;
import org.scaffoldeditor.worldexport.mat.PromisedReplayTexture;
import org.scaffoldeditor.worldexport.mat.ReplayTexture;
import org.scaffoldeditor.worldexport.replay.model_adapters.specific.HorseModelAdapter;
import org.scaffoldeditor.worldexport.replay.models.MultipartReplayModel;
import org.scaffoldeditor.worldexport.replay.models.ReplayModel.Pose;
import org.scaffoldeditor.worldexport.replay.models.ReplayModelPart;
import org.scaffoldeditor.worldexport.replay.models.Transform;
import org.scaffoldeditor.worldexport.util.MeshUtils;

import com.google.common.collect.Maps;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.HorseMarking;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;


public class HorseMarkingFeatureAdapter {
    private static final Map<HorseMarking, Identifier> TEXTURES = Util.make(Maps.newEnumMap(HorseMarking.class), textures -> {
        textures.put(HorseMarking.NONE, null);
        textures.put(HorseMarking.WHITE, new Identifier("textures/entity/horse/horse_markings_white.png"));
        textures.put(HorseMarking.WHITE_FIELD, new Identifier("textures/entity/horse/horse_markings_whitefield.png"));
        textures.put(HorseMarking.WHITE_DOTS, new Identifier("textures/entity/horse/horse_markings_whitedots.png"));
        textures.put(HorseMarking.BLACK_DOTS, new Identifier("textures/entity/horse/horse_markings_blackdots.png"));
    });
    
    MinecraftClient minecraftClient = MinecraftClient.getInstance();
    MultipartReplayModel baseModel;
    HorseModelAdapter parent;
    private final Map<ReplayModelPart, ReplayModelPart> modelPartCache = new HashMap<>();

    public HorseMarkingFeatureAdapter(HorseModelAdapter parent) {
        this.parent = parent;
    }

    public void writePose(Pose<ReplayModelPart> pose, float tickDelta) {
        var entity = parent.getEntity();
        var isVisible = !entity.isInvisible();
        var materialId = getMaterialId();

        this.parent.getAllParts().forEach((partName, part) -> {
            writePosePart(pose, parent.getReplayModelPart(part), part, partName, materialId, isVisible);
        });
    }

    public void writePosePart(Pose<ReplayModelPart> pose, ReplayModelPart replayModelPart, ModelPart childModelPart, String partName, String materialId, boolean isVisible) {
        // If a wool part doesn't exist, create it
        var childReplayPart = modelPartCache.get(replayModelPart);
        if (childReplayPart == null && replayModelPart != null) {
            childReplayPart = new ReplayModelPart(partName);
            childReplayPart.getMesh().setActiveMaterialGroupName(materialId);
            // Apply the sheep wool part to this mesh
            MeshUtils.appendModelPart(childModelPart, childReplayPart.getMesh(), false, null);
            // Attach this to the corresponding parent model part
            replayModelPart.children.add(childReplayPart);
            modelPartCache.put(replayModelPart, childReplayPart);
        }

        pose.bones.put(childReplayPart, new Transform(isVisible));
    }

    public Identifier getTexture() {
        return TEXTURES.get(parent.getEntity().getMarking());
    }

    public String getMaterialId() {
        return getTexture().toString();
    }

    public void generateMaterials(MaterialConsumer consumer) {
        var texture = getTexture();
        var materialId = getMaterialId();
        String texName = MaterialUtils.getTexName(texture);

        Material material = new Material();
        material.setColor(texName);
        material.setRoughness(1);
        material.setTransparent(true);
        material.setColor2BlendMode(BlendMode.SOFT_LIGHT);

        ReplayTexture rTexture = new PromisedReplayTexture(texture);

        consumer.addTexture(texName, rTexture);
        consumer.addMaterial(materialId, material);
    }
}
