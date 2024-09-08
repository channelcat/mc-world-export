package org.scaffoldeditor.worldexport.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.HorseEntityModel;

@Mixin(HorseEntityModel.class)
public interface HorseEntityModelAccessor {
    
    @Accessor("saddle")
    ModelPart[] getSaddle();
    @Accessor("straps")
    ModelPart[] getStraps();
}
