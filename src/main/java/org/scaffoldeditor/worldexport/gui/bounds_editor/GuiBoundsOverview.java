package org.scaffoldeditor.worldexport.gui.bounds_editor;

import java.io.Closeable;
import java.util.Objects;

import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.lwjgl.glfw.GLFW;

import com.replaymod.lib.de.johni0702.minecraft.gui.GuiRenderer;
import com.replaymod.lib.de.johni0702.minecraft.gui.RenderInfo;
import com.replaymod.lib.de.johni0702.minecraft.gui.element.AbstractGuiElement;
import com.replaymod.lib.de.johni0702.minecraft.gui.function.Draggable;
import com.replaymod.lib.de.johni0702.minecraft.gui.function.Scrollable;
import com.replaymod.lib.de.johni0702.minecraft.gui.utils.lwjgl.Dimension;
import com.replaymod.lib.de.johni0702.minecraft.gui.utils.lwjgl.ReadableDimension;
import com.replaymod.lib.de.johni0702.minecraft.gui.utils.lwjgl.ReadablePoint;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.World;

public class GuiBoundsOverview extends AbstractGuiElement<GuiBoundsOverview> implements Closeable, Draggable, Scrollable {

    private final OverviewData overviewData;
    private final Identifier texID;
    private final World world;

    private Vector2f panOffset = new Vector2f();
    private double zoomAmount = 0;

    public GuiBoundsOverview(World world, OverviewData overviewData) {
        Objects.requireNonNull(world);
        Objects.requireNonNull(overviewData);

        this.overviewData = overviewData;
        this.world = world;
        this.texID = getMinecraft().getTextureManager().registerDynamicTexture("overview/", overviewData.getTexture());
        updateTexture();
    }

    public double getZoomAmount() {
        return zoomAmount;
    }

    public void setZoomAmount(double zoomAmount) {
        this.zoomAmount = zoomAmount;
    }

    public Vector2f getPanOffset() {
        return panOffset;
    }

    public void setPanOffset(Vector2fc panOffset) {
        this.panOffset.set(panOffset);
    }

    public void updateTexture() {
        overviewData.updateTexture(world, world.getBottomY(), world.getTopY(), Util.getMainWorkerExecutor());
    }

    @Override
    public void draw(GuiRenderer renderer, ReadableDimension size, RenderInfo renderInfo) {
        drawMap(renderer, size, renderInfo);
    }

    public double getZoomMultiplier() {
        return Math.pow(2, zoomAmount);
    }

    private void drawMap(GuiRenderer renderer, ReadableDimension size, RenderInfo renderInfo) {
        NativeImage image = overviewData.getTexture().getImage();

        MatrixStack matrices = renderer.getMatrixStack();
        matrices.push();

        ReadablePoint glOffset = renderer.getOpenGlOffset();
        matrices.translate(glOffset.getX(), glOffset.getY(), 0);

        // Center image
        matrices.translate(size.getWidth() / 2f - image.getWidth() / 2f, size.getHeight() / 2f - image.getHeight() / 2f, 0);

        // Apply zoom
        float zoomMultiplier = (float) getZoomMultiplier();
        float centerX = image.getWidth() / 2f;
        float centerY = image.getHeight() / 2f;

        matrices.translate(centerX, centerY, 0);
        matrices.scale(zoomMultiplier, zoomMultiplier, zoomMultiplier);
        matrices.translate(-centerX, -centerY, 0);
        
        matrices.translate(panOffset.x, panOffset.y, 0);

        // matrices.translate(-image.getWidth() / 2f, -image.getHeight() / 2f, 0);
        
        // Center image
        int imageX = 0;
        int imageY = 0;

        renderer.bindTexture(texID);
        DrawableHelper.drawTexture(matrices, imageX, imageY, 0, 0, image.getWidth(), image.getHeight(), image.getWidth(), image.getHeight());
        // renderer.drawTexturedRect(imageX, imageY, 0, 0, image.getWidth(), image.getHeight());

        matrices.pop();
    }

    @Override
    protected ReadableDimension calcMinSize() {
        return new Dimension(384, 256);
    }

    @Override
    protected GuiBoundsOverview getThis() {
        return this;
    }

    @Override
    public void close() {
        overviewData.close();
    }

    @Override
    public boolean mouseClick(ReadablePoint position, int button) {
        // return (button == GLFW.GLFW_MOUSE_BUTTON_1);
        return false;
    }

    @Override
    public boolean scroll(ReadablePoint mousePosition, int dWheel) {
        setZoomAmount(zoomAmount + (dWheel > 0 ? 1 : -1));
        return true;
    }

    private ReadablePoint lastDragPosition;

    @Override
    public boolean mouseDrag(ReadablePoint position, int button, long timeSinseLastCall) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_1) return false;
        
        if (lastDragPosition == null) {
            lastDragPosition = position;
            return true;
        }

        float zoomMultiplier = (float) getZoomMultiplier();
        float deltaX = (position.getX() - lastDragPosition.getX()) / zoomMultiplier;
        float deltaY = (position.getY() - lastDragPosition.getY()) / zoomMultiplier;
        
        this.panOffset.x += deltaX;
        this.panOffset.y += deltaY;

        lastDragPosition = position;
        return true;
    }

    @Override
    public boolean mouseRelease(ReadablePoint position, int button) {
        if (lastDragPosition != null) {
            lastDragPosition = null;
            return true;
        } else {
            return false;
        }
    }
    
}
