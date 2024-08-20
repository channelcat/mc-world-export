package org.scaffoldeditor.worldexport.replay.feature_adapters;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.worldexport.mat.Material;
import org.scaffoldeditor.worldexport.mat.Material.BlendMode;
import org.scaffoldeditor.worldexport.mat.MaterialConsumer;
import org.scaffoldeditor.worldexport.mat.MaterialUtils;
import org.scaffoldeditor.worldexport.mat.PromisedReplayTexture;
import org.scaffoldeditor.worldexport.mat.ReplayTexture;
import org.scaffoldeditor.worldexport.replay.model_adapters.specific.VillagerModelAdapter;
import org.scaffoldeditor.worldexport.replay.models.ReplayModel.Pose;
import org.scaffoldeditor.worldexport.replay.models.ReplayModelPart;
import org.scaffoldeditor.worldexport.replay.models.Transform;
import org.scaffoldeditor.worldexport.util.MeshUtils;

import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.Util;
import net.minecraft.village.VillagerProfession;

public class VillagerClothingFeatureAdapter implements ReplayFeatureAdapter<ReplayModelPart> {
    // TODO: somehow read this from the feature renderer?
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static final Int2ObjectMap<Identifier> LEVEL_TO_ID = Util.make(new Int2ObjectOpenHashMap(), levelToId -> {
        levelToId.put(1, new Identifier("stone"));
        levelToId.put(2, new Identifier("iron"));
        levelToId.put(3, new Identifier("gold"));
        levelToId.put(4, new Identifier("emerald"));
        levelToId.put(5, new Identifier("diamond"));
    });

    final VillagerModelAdapter<VillagerEntity> villager;

    private final Map<Identifier, Iterable<ReplayModelPart>> clothingParts = new HashMap<>();

    public VillagerClothingFeatureAdapter(VillagerModelAdapter<VillagerEntity> villager) {
        this.villager = villager;
    }

    @Override
    public void writePose(Pose<ReplayModelPart> pose, float tickDelta) {
        for (var parts : clothingParts.values()) {
            for (var part : parts) {
                pose.bones.put(part, new Transform(false));
            }
        }

        for (var clothing : getClothingTextures()) {
            if (!clothingParts.containsKey(clothing.texture)) {
                var partList = new ArrayList<ReplayModelPart>();
                for (var villagerPart : villager.getEntityParts(EntityModelPartNames.HEAD)) {
                    if (villagerPart.getReplayPart() != null) {
                        String texName = MaterialUtils.getTexName(clothing.texture);
                        var clothingPart = new ReplayModelPart("villager." + villagerPart.getIdentifier() + "." + clothing.id);
                        clothingPart.getMesh().setActiveMaterialGroupName(texName);
                        MeshUtils.appendModelPart(villagerPart.getModelPart(), clothingPart.getMesh(), false, null);
                        villagerPart.getReplayPart().children.add(clothingPart);
                        partList.add(clothingPart);
                    } else {
                        LogManager.getLogger().error("No replay part for " + villagerPart.getIdentifier());
                    }
                }
                // Only add if we have parts
                if (partList.size() > 0) {
                    clothingParts.put(clothing.texture, partList);
                }
            }
            var parts = clothingParts.get(clothing.texture);
            if (parts != null) {
                for (var part : parts) {
                    pose.bones.put(part, new Transform(true));
                }
            }
        }
    }

    private List<PartTexture> getClothingTextures() {
        var villagerData = villager.getVillagerData();
        var villagerType = villagerData.getType();
        var villagerProfession = villagerData.getProfession();
        
        // List of all textures to render
        List<PartTexture> textures = new ArrayList<>();
        textures.add(getTextureIdentifier("type", Registries.VILLAGER_TYPE.getId(villagerType)));
        
        // Profession?
        if (villagerProfession != VillagerProfession.NONE) {
            textures.add(getTextureIdentifier("profession", Registries.VILLAGER_PROFESSION.getId(villagerProfession)));
        
            // Profession Level?
            if (villagerProfession != VillagerProfession.NITWIT) {
                var levelId = (Identifier)LEVEL_TO_ID.get(MathHelper.clamp(villagerData.getLevel(), 1, LEVEL_TO_ID.size()));
                textures.add(getTextureIdentifier("profession_level", levelId));
            }
        }

        return textures;
    }

    private PartTexture getTextureIdentifier(String keyType, Identifier keyId) {
        var id = keyType + "_" + keyId;
        var texture = keyId.withPath(path -> "textures/entity/villager/" + keyType + "/" + path + ".png");
        return new PartTexture(id, texture);
    }

    @Override
    public void generateMaterials(MaterialConsumer consumer) {
        for (Identifier texture : clothingParts.keySet()) {
            String texName = MaterialUtils.getTexName(texture);

            Material material = new Material();
            material.setColor(texName);
            material.setRoughness(1);
            material.setTransparent(true);
            material.setColor2BlendMode(BlendMode.SOFT_LIGHT);

            ReplayTexture rTexture = new PromisedReplayTexture(texture);

            consumer.addTexture(texName, rTexture);
            consumer.addMaterial(texName, material);
        }
    }


    record PartTexture (String id, Identifier texture) {}

}
