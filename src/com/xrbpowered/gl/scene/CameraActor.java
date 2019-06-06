package com.xrbpowered.gl.scene;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class CameraActor extends Actor {

	protected Matrix4f projection = null;
	protected final Matrix4f view = new Matrix4f();
	private float aspectRatio = 1f;
	
	public CameraActor setProjection(Matrix4f projection) {
		this.projection = projection;
		return this;
	}

	public float getAspectRatio() {
		return aspectRatio;
	}
	
	public CameraActor setAspectRatio(float aspectRatio) {
		this.aspectRatio = aspectRatio;
		return this;
	}

	public CameraActor setAspectRatio(float width, float height) {
		return setAspectRatio(width/height);
	}

	public Matrix4f getProjection() {
		return projection;
	}
	
	public Matrix4f getView() {
		return view;
	}
	
	@Override
	public void updateTransform() {
		super.updateTransform();
		view.identity();
		view.translate(position);
		rotateYawPitchRoll(rotation, view);
		view.invert();
	}
	
	public void getDir(Vector3f out) {
		out.set(view.m02(), view.m12(), view.m22());
		out.normalize();
	}
	
	public void getDir(Vector3f out, int x, int y, int displayWidth, int displayHeight) {
		float mx = (x - displayWidth*0.5f) * (1f / displayWidth) / projection.m00();
		float my = (y - displayHeight*0.5f) * (1f / displayWidth) / projection.m00();
		out.set(
			view.m02() - (view.m00() * mx + view.m01() * my) * 2f,
			view.m12() - (view.m10() * mx + view.m11() * my) * 2f,
			view.m22() - (view.m20() * mx + view.m21() * my) * 2f
		);
		out.normalize();
	}
	
	public static class Perspective extends CameraActor {
		private float fov = 70f;
		private float near = 0.1f;
		private float far = 100f;
		
		public Perspective setFov(float fov) {
			this.fov = fov;
			updateTransform();
			return this;
		}
		
		@Override
		public Perspective setAspectRatio(float aspectRatio) {
			super.setAspectRatio(aspectRatio);
			updateTransform();
			return this;
		}
		
		public Perspective setRange(float near, float far) {
			this.near = near;
			this.far = far;
			updateTransform();
			return this;
		}
		
		@Override
		public void updateTransform() {
			projection = perspective(fov, getAspectRatio(), near, far, projection);
			super.updateTransform();
		}
	}

	public static Matrix4f perspective(float fov, float aspectRatio, float near, float far, Matrix4f out) {
		if(out==null)
			out = new Matrix4f();
		out.zero();

		float t = (float)Math.tan(Math.toRadians(fov) / 2.0);
		out._m00(1f / (aspectRatio * t));
		out._m11(1f / t);
		out._m22((far + near) / (near - far));
		out._m23(-1);
		out._m32((2f * near * far) / (near - far));

		return out;
	}

}
