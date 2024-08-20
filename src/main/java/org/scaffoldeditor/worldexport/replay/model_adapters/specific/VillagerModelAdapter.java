package org.scaffoldeditor.worldexport.replay.model_adapters.specific;

import java.util.Iterator;

import org.scaffoldeditor.worldexport.mat.MaterialConsumer;
import org.scaffoldeditor.worldexport.mixins.ModelPartAccessor;
import org.scaffoldeditor.worldexport.replay.feature_adapters.VillagerClothingFeatureAdapter;
import org.scaffoldeditor.worldexport.replay.model_adapters.SinglePartModelAdapter;
import org.scaffoldeditor.worldexport.replay.models.MultipartReplayModel;
import org.scaffoldeditor.worldexport.replay.models.ReplayModel.Pose;
import org.scaffoldeditor.worldexport.replay.models.ReplayModelPart;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;

/**
 * Adapter for Villager models.
 */
public class VillagerModelAdapter<T extends VillagerEntity> extends SinglePartModelAdapter<T> {

    private Identifier texture;
    private VillagerClothingFeatureAdapter clothingFeatureAdapter;

    public VillagerModelAdapter(T entity, Identifier texture) throws IllegalArgumentException {
        super(entity);
        this.texture = texture;
    }

    @Override
    public Identifier getTexture() {
        return texture;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected MultipartReplayModel captureBaseModel(SinglePartEntityModel<T> model) {
        MultipartReplayModel replayModel = super.captureBaseModel(model);
        clothingFeatureAdapter = new VillagerClothingFeatureAdapter((VillagerModelAdapter<VillagerEntity>) this);
        // TODO: baby scaling
        return replayModel;
    }
    
    @Override
    protected Pose<ReplayModelPart> writePose(float tickDelta) {
        Pose<ReplayModelPart> pose = super.writePose(tickDelta);
        clothingFeatureAdapter.writePose(pose, tickDelta);
        return pose;
    }

    @Override
    public void generateMaterials(MaterialConsumer file) {
        super.generateMaterials(file);
        clothingFeatureAdapter.generateMaterials(file);
    }

    @Override
    protected Iterable<Pair<String, ModelPart>> getRootParts() {
        return this::createRootPartIterator;
    }
    private Iterator<Pair<String, ModelPart>> createRootPartIterator() {
        ModelPartAccessor accessor = (ModelPartAccessor) (Object) ((VillagerResemblingModel<T>) model).getPart();

        return accessor.getChildren().entrySet().stream().map(
            child -> new Pair<>(child.getKey(), child.getValue())
        ).iterator();
    }


    // Villager Information
    public boolean isBaby() {
        return ((VillagerEntity)getEntity()).isBaby();
    }
    public VillagerData getVillagerData() {
        return ((VillagerDataContainer)getEntity()).getVillagerData();
    }
}
