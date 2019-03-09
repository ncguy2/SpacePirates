package net.game.spacepirates.data;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;

public class Transform2D {

    public transient Transform2D parent;

    public final Vector2 translation = new Vector2();
    public float rotation;
    public final Vector2 scale = new Vector2(1, 1);

    public final Matrix3 transformation = new Matrix3();

    protected transient final Vector2 worldTranslation = new Vector2();
    protected transient final Vector2 worldScale = new Vector2(1, 1);

    public Transform2D() {
    }

    public void fromExistingWorldTransform(Transform2D transform) {
        translation.set(transform.worldTranslation());
        rotation = transform.worldRotation();
        scale.set(transform.worldScale());
        update();
    }

    public static Transform2D fromWorldTransform(Transform2D transform) {
        Transform2D tr = new Transform2D();
        tr.translation.set(transform.worldTranslation());
        tr.rotation = transform.worldRotation();
        tr.scale.set(transform.worldScale());
        tr.update();
        return tr;
    }

    public Transform2D copy() {
        return fromWorldTransform(this);
    }

    public boolean hasParent() {
        return parent != null;
    }

    public void adopt(Transform2D child) {
        if(child.hasParent()) {
            throw new IllegalStateException("Cannot adopt a non-orphan child");
        }
        child.setParent(this);
    }

    public void release(Transform2D child) {
        if(!child.hasParent()) {
            throw new IllegalStateException("Cannot release an orphan");
        }
        if(child.getParent() != this) {
            throw new IllegalStateException("Cannot release a child belonging to a different parent");
        }

        child.clearParent();
    }

    private Transform2D getParent() {
        return parent;
    }

    public Matrix3 update() {
        transformation.idt();
        transformation.translate(translation);
        transformation.rotate(rotation);
        transformation.scale(scale);
        return transformation;
    }

    public Matrix3 localTransform() {
        return update();
    }

    public Matrix3 worldTransform() {
        Matrix3 mat = getParentTransform();
        return mat.mul(this.localTransform());
    }

    public float rotationRad() {
        return (float) Math.toRadians(rotation);
    }

    public void translate(Vector2 vec) {
        translation.add(vec);
        update();
    }

    public Matrix3 getParentTransform() {
        Matrix3 mat = new Matrix3().idt();
        if(parent != null)
            mat.set(parent.worldTransform());
        return mat;
    }

    public Vector2 worldTranslation() {
        worldTransform().getTranslation(worldTranslation);
        return worldTranslation;
    }

    public float worldRotation() {
        return worldTransform().getRotation();
    }

    public Vector2 worldScale() {
        worldTransform().getScale(worldScale);
        return worldScale;
    }

    public void setParent(Transform2D transform) {
        this.parent = transform;
    }

    public void clearParent() {
        setParent(null);
    }

    public void setWorldTranslation(Vector2 worldLoc) {
        translation.set(worldLoc).mul(getParentTransform().inv());
    }

    public void setWorldRotation(float rotation) {
        this.rotation = getParentTransform().inv().rotate(rotation).getRotation();
    }

    public void setWorldRotationRad(float rotationRadians) {
        this.rotation = getParentTransform().inv().rotateRad(rotationRadians).getRotation();
    }

    public void setWorldScale(Vector2 worldScale) {
        scale.set(worldScale).mul(getParentTransform().inv());
    }

}
