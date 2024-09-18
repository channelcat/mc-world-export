package org.scaffoldeditor.worldexport.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BatEntityModel;

@Mixin(BatEntityModel.class)
public interface BatEntityModelAccessor {

    @Accessor("head")
    ModelPart getHead();

    @Accessor("body")
    ModelPart getBody();

    @Accessor("rightWing")
    ModelPart getRightWing();

    @Accessor("leftWing")
    ModelPart getLeftWing();

    @Accessor("rightWingTip")
    ModelPart getRightWingTip();

    @Accessor("leftWingTip")
    ModelPart getLeftWingTip();

    @Accessor("feet")
    ModelPart getFeet();
}