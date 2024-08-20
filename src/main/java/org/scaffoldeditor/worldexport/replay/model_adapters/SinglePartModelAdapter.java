package org.scaffoldeditor.worldexport.replay.model_adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.scaffoldeditor.worldexport.mixins.ModelPartAccessor;
import org.scaffoldeditor.worldexport.replay.models.ReplayModelPart;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

public class SinglePartModelAdapter<T extends LivingEntity> extends LivingEntityModelAdapter<T, SinglePartEntityModel<T>> {

    private final Identifier texture;

    public SinglePartModelAdapter(T entity) {
        super(entity);
        this.texture = getEntityTexture(entity);
    }

    @Override
    public Identifier getTexture() {
        return texture;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected SinglePartEntityModel<T> extractModel(LivingEntityRenderer<? super T, ?> entityRenderer)
            throws ClassCastException {
        return (SinglePartEntityModel<T>) entityRenderer.getModel();
    }

    @Override
    protected void extractPartNames(SinglePartEntityModel<T> model, Map<ModelPart, String> partNames) {
        for (var part : getEntityParts()) {
            partNames.put(part.modelPart, part.identifier);
        }
    }

    @Override
    protected Iterable<Pair<String, ModelPart>> getRootParts() {
        return Collections.singleton(new Pair<>("root", getEntityModel().getPart()));
    }

    private static Identifier getEntityTexture(Entity entity) {
        return MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(entity).getTexture(entity);
    }
    

    // Entity Part Extraction

    private List<EntityPart> getEntityPartChildren(ModelPart part, Set<String> skipParts) {
        ModelPartAccessor accessor = (ModelPartAccessor) (Object) part;
        List<EntityPart> children = new ArrayList<>();
        for (var child : accessor.getChildren().entrySet()) {
            if (skipParts.contains(child.getKey())) continue;
            children.add(new EntityPart(child.getKey(), child.getValue(), replayModel != null ? replayModel.getBone(child.getKey()) : null));
            children.addAll(getEntityPartChildren(child.getValue(), skipParts));
        }
        return children;
    }

    public List<EntityPart> getEntityParts() {
        return getEntityParts(Set.of());
    }

    public List<EntityPart> getEntityParts(String skipPart) {
        return getEntityParts(Set.of(skipPart));
    }

    public List<EntityPart> getEntityParts(Set<String> skipParts) {
        return getEntityPartChildren(getEntityModel().getPart(), skipParts);
    }

    public record EntityPart (String identifier, ModelPart modelPart, ReplayModelPart replayPart) {
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
}
