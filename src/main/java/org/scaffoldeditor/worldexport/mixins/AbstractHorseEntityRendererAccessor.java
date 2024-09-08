package org.scaffoldeditor.worldexport.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.entity.AbstractHorseEntityRenderer;

@Mixin(AbstractHorseEntityRenderer.class)
public interface AbstractHorseEntityRendererAccessor {
    
    @Accessor("scale")
    float getScale();
}
