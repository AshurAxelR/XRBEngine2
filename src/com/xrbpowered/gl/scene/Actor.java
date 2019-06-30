package com.xrbpowered.gl.scene;

import java.util.Comparator;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Actor {

	public Vector3f position = new Vector3f(0, 0, 0);
	public Vector3f scale = new Vector3f(1, 1, 1);
	public Vector3f rotation = new Vector3f(0, 0, 0);
	public float depth;
	
	private final Matrix4f transform = new Matrix4f();
	
	public void updateTransform() {
		setTransform(position, scale, rotation, transform);
	}
	
	public Matrix4f getTransform() {
		return transform;
	}
	
	public Vector4f calcViewPos(CameraActor camera, Vector4f p) {
		if(p==null)
			p = new Vector4f();
		Matrix4f m = new Matrix4f(camera.getProjection()); // FIXME new
		m.mul(camera.getView());
		m.mul(transform);
		p.set(0f, 0f, 0f, 1f);
		p = m.transform(p); // TODO check =
		p.x /= p.w;
		p.y /= p.w;
		return p;
	}
	
	public float calcDepth(CameraActor camera) {
		Vector4f p = calcViewPos(camera, null);
		depth = p.z;
		return depth;
	}
	
	public float getDistTo(Actor actor) {
		Vector3f d = new Vector3f(position); // FIXME new
		d.sub(actor.position);
		return d.length();
	}
	
	protected static final Vector3f X_AXIS = new Vector3f(1, 0, 0);
	protected static final Vector3f Y_AXIS = new Vector3f(0, 1, 0);
	protected static final Vector3f Z_AXIS = new Vector3f(0, 0, 1);
	
	public static Matrix4f setTransform(Vector3f position, Vector3f scale, Vector3f rotation, Matrix4f m) {
		m.identity();
		m.translate(position);
		rotateYawPitchRoll(rotation, m);
		m.scale(scale);
		return m;
	}
	
	public static Matrix4f rotateYawPitchRoll(Vector3f rotation, Matrix4f m) {
		m.rotate(rotation.z, Z_AXIS);
		m.rotate(rotation.y, Y_AXIS);
		m.rotate(rotation.x, X_AXIS);
		return m;
	}
	
	public Comparator<Actor> sortBackToFront = new Comparator<Actor>() {
		@Override
		public int compare(Actor o1, Actor o2) {
			return -Float.compare(o1.depth, o2.depth);
		}
	};
	
	public Comparator<Actor> sortFrontToBack = new Comparator<Actor>() {
		@Override
		public int compare(Actor o1, Actor o2) {
			return Float.compare(o1.depth, o2.depth);
		}
	};
	
}
