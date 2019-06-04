package com.xrbpowered.gl.scene;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class CameraActor extends Actor {

	private Matrix4f projection;
	private final Matrix4f view = new Matrix4f();
	private final Matrix4f followView = new Matrix4f();
	
	public CameraActor setProjection(Matrix4f projection) {
		this.projection = projection;
		return this;
	}
	
	public Matrix4f getProjection() {
		return projection;
	}
	
	public Matrix4f getView() {
		return view;
	}
	
	public Matrix4f getFollowView() {
		return followView;
	}
	
	@Override
	public void updateTransform() {
		super.updateTransform();
		view.identity();
		view.translate(position);
		rotateYawPitchRoll(rotation, view);
		view.invert();
		
		followView.identity();
		rotateYawPitchRoll(rotation, followView);
		followView.invert();
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
	
}
