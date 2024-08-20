package org.scaffoldeditor.worldexport.replay.model_adapters.specific;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.scaffoldeditor.worldexport.mat.MaterialConsumer;
import org.scaffoldeditor.worldexport.mixins.ModelPartAccessor;
import org.scaffoldeditor.worldexport.replay.feature_adapters.VillagerClothingFeatureAdapter;
import org.scaffoldeditor.worldexport.replay.model_adapters.LivingEntityModelAdapter;
import org.scaffoldeditor.worldexport.replay.models.MultipartReplayModel;
import org.scaffoldeditor.worldexport.replay.models.ReplayModel.Pose;
import org.scaffoldeditor.worldexport.replay.models.ReplayModelPart;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;

/**
 * Adapter for Villager models.
 */
public class VillagerModelAdapter<T extends VillagerEntity> extends LivingEntityModelAdapter<T, EntityModel<T>> {

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
    protected MultipartReplayModel captureBaseModel(EntityModel<T> model) {
        MultipartReplayModel replayModel = super.captureBaseModel(model);
        clothingFeatureAdapter = new VillagerClothingFeatureAdapter(this);
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
    @SuppressWarnings("unchecked")
    protected EntityModel<T> extractModel(LivingEntityRenderer<? super T, ?> entityRenderer) throws ClassCastException {
        return (EntityModel<T>) entityRenderer.getModel();
    }

    @Override
    protected void extractPartNames(EntityModel<T> model, Map<ModelPart, String> partNames) {
        for (var part : getVillagerParts()) {
            partNames.put(part.modelPart, part.identifier);
        }
    }

    public record VillagerPart (String identifier, ModelPart modelPart, ReplayModelPart replayPart) {
        public String getIdentifier() {
            return identifier;
        }
        public ModelPart getModelPart() {
            return modelPart;
        }
        public ReplayModelPart getReplayPart() {
            return replayPart;
        }
    }

    private List<VillagerPart> getVillagerPartChildren(ModelPart part, Set<String> skipParts) {
        ModelPartAccessor accessor = (ModelPartAccessor) (Object) part;
        List<VillagerPart> children = new ArrayList<>();
        for (var child : accessor.getChildren().entrySet()) {
            if (skipParts.contains(child.getKey())) continue;
            children.add(new VillagerPart(child.getKey(), child.getValue(), replayModel != null ? replayModel.getBone(child.getKey()) : null));
            children.addAll(getVillagerPartChildren(child.getValue(), skipParts));
        }
        return children;
    }

    public List<VillagerPart> getVillagerParts() {
        return getVillagerParts(Set.of());
    }

    public List<VillagerPart> getVillagerParts(String skipPart) {
        return getVillagerParts(Set.of(skipPart));
    }

    public List<VillagerPart> getVillagerParts(Set<String> skipParts) {
        return getVillagerPartChildren(((VillagerResemblingModel<T>) model).getPart(), skipParts);
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

    // Model parts

    public ReplayModelPart getReplayModelPart(String name) {
        return getModel().getBone(name);
    }
}
