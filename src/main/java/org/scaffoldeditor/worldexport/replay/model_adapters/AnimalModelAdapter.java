package org.scaffoldeditor.worldexport.replay.model_adapters;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.worldexport.mixins.AnimalModelAccessor;
import org.scaffoldeditor.worldexport.mixins.QuadrupedModelAccessor;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.DonkeyEntityRenderer;
import net.minecraft.client.render.entity.FoxEntityRenderer;
import net.minecraft.client.render.entity.HoglinEntityRenderer;
import net.minecraft.client.render.entity.HorseEntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.BeeEntityModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.render.entity.model.DonkeyEntityModel;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.FoxEntityModel;
import net.minecraft.client.render.entity.model.GoatEntityModel;
import net.minecraft.client.render.entity.model.HoglinEntityModel;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.render.entity.model.PandaEntityModel;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.client.render.entity.model.PolarBearEntityModel;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.client.render.entity.model.TurtleEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;


/**
 * Umbrella model adapter that works specifically with entities which use Animal Models.
 */
public class AnimalModelAdapter<T extends LivingEntity> extends LivingEntityModelAdapter<T, AnimalModel<T>> {

    private Identifier texture;
    private LivingEntityRenderer<? super T, ?> entityRenderer;

    public AnimalModelAdapter(T entity) throws IllegalArgumentException {
        super(entity);
    }

    public AnimalModelAdapter(T entity, Identifier texture) throws IllegalArgumentException {
        super(entity);
        this.texture = texture;
    }

    @Override
    public Identifier getTexture() {
        return texture;
    }

    // These should be overridden by subclasses
    protected String[] getHeadPartNames() { return null; }
    protected String[] getBodyPartNames() { return null; }
    protected List<ModelPartRelation> getAdditionalModelPartRelations() { return null; }
    
    // Fetches model parts from a parent -> child relationship
    protected Map<String, ModelPart> getAdditionalBodyParts(Map<String, ModelPart> headAndBodyParts) {
        var additionalModelPartRelations = getAdditionalModelPartRelations();
        if (additionalModelPartRelations != null) {
            var additionalBodyParts = new HashMap<String, ModelPart>();
            for (var relation : additionalModelPartRelations) {
                var parent = headAndBodyParts.get(relation.parent);
                if (parent == null) {
                    LogManager.getLogger().error("Missing parent part for additional body part relation: {}", relation);
                    continue;
                }
                var part = parent.getChild(relation.child);
                if (part != null) {
                    additionalBodyParts.put(relation.child, part);
                }
            }
            return additionalBodyParts;
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected AnimalModel<T> extractModel(LivingEntityRenderer<? super T, ?> entityRenderer) throws ClassCastException {
        this.entityRenderer = entityRenderer;
        return (AnimalModel<T>) entityRenderer.getModel();
    }

    public LivingEntityRenderer<? super T, ?> getEntityRenderer() {
        return entityRenderer;
    }

    public Map<String, ModelPart> getHeadAndBodyParts() {
        Map<String, ModelPart> parts = new HashMap<>();

        var headPartNames = getHeadPartNames();
        if (headPartNames != null) {
            var headParts = ((AnimalModelAccessor) model).retrieveHeadParts();
            var h = 0;
            for (var part : headParts) {
                parts.put(h < headPartNames.length ? headPartNames[h++] : null, part);
            }
            if (h != headPartNames.length) {
                LogManager.getLogger().error("Head part count mismatch: expected {}, got {}", headPartNames.length, h);
            }
        }
        
        var bodyPartNames = getBodyPartNames();
        if (bodyPartNames != null) {
            var bodyParts = ((AnimalModelAccessor) model).retrieveBodyParts();
            var b = 0;
            for (var part : bodyParts) {
                parts.put(b < bodyPartNames.length ? bodyPartNames[b++] : null, part);
            }
            if (b != bodyPartNames.length) {
                LogManager.getLogger().error("Body part count mismatch: expected {}, got {}", bodyPartNames.length, b);
            }
        }

        return parts;
    }

    @Override
    protected void extractPartNames(AnimalModel<T> model, Map<ModelPart, String> partNames) {
        var headAndBodyParts = getHeadAndBodyParts();
        for (var part : headAndBodyParts.entrySet()) {
            partNames.put(part.getValue(), part.getKey());
        }

        // Any additional parts from child classes
        var additionalParts = getAdditionalBodyParts(headAndBodyParts);
        if (additionalParts != null) {
            for (var part : additionalParts.entrySet()) {
                partNames.put(part.getValue(), part.getKey());
            }
        }
    }


    @Override
    protected Iterable<Pair<String, ModelPart>> getRootParts() {
        return this::createRootPartIterator;

        // BeeEntityModel;
        // FoxEntityModel;
        // FoxEntityRenderer;
        // HoglinEntityModel;
        // HoglinEntityRenderer;
        // HorseEntityModel;
        // HorseEntityRenderer;
        // DonkeyEntityModel;
        // DonkeyEntityRenderer;
        // EndermanEntityModel;
        // TurtleEntityModel;
        // PolarBearEntityModel;
        // PigEntityModel;
        // PigEntityRenderer;
        // PandaEntityModel;
        // GoatEntityModel;
        // CowEntityModel;
    }

    private Iterator<Pair<String, ModelPart>> createRootPartIterator() {
        Stream<Pair<String, ModelPart>> stream1 = StreamSupport
                .stream(((AnimalModelAccessor) model).retrieveBodyParts().spliterator(), false)
                .map(part -> new Pair<>(findName(part), part));

        Stream<Pair<String, ModelPart>> stream2 = StreamSupport
                .stream(((AnimalModelAccessor) model).retrieveHeadParts().spliterator(), false)
                .map(part -> new Pair<>(findName(part), part));
        
        return Stream.concat(stream1, stream2).iterator();
    }

    private String findName(ModelPart part) {
        return boneMapping.containsKey(part) ? boneMapping.get(part).getName() : part.toString();
    }

    public record ModelPartRelation(String parent, String child) {}

}
