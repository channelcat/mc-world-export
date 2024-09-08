package org.scaffoldeditor.worldexport.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.entity.HorseEntityRenderer;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.util.Identifier;

@Mixin(HorseEntityRenderer.class)
public interface HorseEntityRendererAccessor {
    
    @Invoker("getTexture")
    Identifier retrieveTexture(HorseEntity horseEntity);
}
