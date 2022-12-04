package org.scaffoldeditor.worldexport.replaymod.camera_animations;

import org.scaffoldeditor.worldexport.replaymod.util.RollProvider;

import com.replaymod.replaystudio.util.Location;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.util.math.Box;

public class AnimatedCameraEntity extends Entity implements RollProvider {

    public float roll;
    public float fov;
    protected final MinecraftClient client;

    public AnimatedCameraEntity(MinecraftClient client, ClientWorld world) {
        super(EntityType.PLAYER, world);
        this.client = client;
    }

    @Override
    protected void initDataTracker() {        
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound var1) {        
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound var1) {        
    }

    @Override
    public Packet<?> createSpawnPacket() {
        throw new IllegalStateException("This entity is client-side only.");
    }

    @Override
    public ClientWorld getWorld() {
        return (ClientWorld) super.getWorld();
    }

    @Override
    public final float getRoll() {
        return roll;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public final float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    /**
     * Moves the camera by the specified delta.
     * @param x Delta in X direction
     * @param y Delta in Y direction
     * @param z Delta in Z direction
     */
    public void moveCamera(double x, double y, double z) {
        setCameraPosition(this.getX() + x, this.getY() + y, this.getZ() + z);
    }
    
    /**
     * Set the camera position.
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     */
    public void setCameraPosition(double x, double y, double z) {
        this.lastRenderX = this.prevX = x;
        this.lastRenderY = this.prevY = y;
        this.lastRenderZ = this.prevZ = z;
        this.setPos(x, y, z);
        updateBoundingBox();
    }

    /**
     * Sets the camera rotation.
     * @param yaw Yaw in degrees
     * @param pitch Pitch in degrees
     * @param roll Roll in degrees
     */
    public void setCameraRotation(float yaw, float pitch, float roll) {
        this.prevYaw = yaw;
        this.prevPitch = pitch;
        setPitch(pitch);
        setYaw(yaw);
        setRoll(roll);
    }

    public void setCameraPosRot(Location loc) {
        setCameraRotation(loc.getPitch(), loc.getYaw(), roll);
        setCameraPosition(loc.getX(), loc.getY(), loc.getZ());
    }

    private void updateBoundingBox() {
        float width = getWidth();
        float height = getHeight();

        setBoundingBox(new Box(
                this.getX() - width / 2, this.getY(), this.getZ() - width / 2,
                this.getX() + width / 2, this.getY() + height, this.getZ() + width / 2));
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false; // We are in full control of ourselves
    }

    @Override
    protected void spawnSprintingParticles() {
        // We do not produce any particles, we are a camera
    }

    @Override
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return client.getCameraEntity() != this;
    }

    

}
