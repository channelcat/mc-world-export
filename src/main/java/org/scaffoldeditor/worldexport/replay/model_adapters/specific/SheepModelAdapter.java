package org.scaffoldeditor.worldexport.replay.model_adapters.specific;

import org.scaffoldeditor.worldexport.mat.MaterialConsumer;
import org.scaffoldeditor.worldexport.replay.feature_adapters.SheepWoolFeatureAdapter;
import org.scaffoldeditor.worldexport.replay.model_adapters.QuadrupedModelAdapter;
import org.scaffoldeditor.worldexport.replay.models.ReplayModel.Pose;
import org.scaffoldeditor.worldexport.replay.models.ReplayModelPart;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.Identifier;

public class SheepModelAdapter extends QuadrupedModelAdapter<SheepEntity> {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final Identifier TEXTURE = new Identifier("textures/entity/sheep/sheep.png");
    private static final Identifier WOOL = new Identifier("textures/entity/sheep/sheep_fur.png");

    // protected SheepWoolEntityModel<SheepEntity> entityModel;
    // protected AnimalFeatureModelAdapter<SheepWoolEntityModel<SheepEntity>> woolAdapter;
    protected SheepWoolFeatureAdapter woolAdapter;

    public SheepModelAdapter(SheepEntity entity) throws IllegalArgumentException {
        super(entity, TEXTURE);
        
        woolAdapter = new SheepWoolFeatureAdapter(this);
        // TODO: Possibly use a feature model adapter instead of manually overwriting model parts
        // woolAdapter = new AnimalFeatureModelAdapter<SheepWoolEntityModel>(entity, WOOL);
    }

    @Override
    public void generateMaterials(MaterialConsumer file) {
        super.generateMaterials(file);
        woolAdapter.generateMaterials(file);
    }
    
    @Override
    protected Pose<ReplayModelPart> writePose(float tickDelta) {
        Pose<ReplayModelPart> pose = super.writePose(tickDelta);
        woolAdapter.writePose(pose, tickDelta);

        return pose;
    }
}