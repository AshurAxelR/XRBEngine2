package com.xrbpowered.gl.res.shader;

import org.lwjgl.opengl.GL20;

import com.xrbpowered.gl.scene.CameraActor;

public class CameraShader extends Shader {

	protected int projectionMatrixLocation;
	protected int viewMatrixLocation;
	protected int cameraPositionLocation;

	protected CameraActor camera = null;
	public boolean followCamera = false;
	
	public CameraShader(VertexInfo info, String pathXS) {
		super(info, pathXS);
	}

	public CameraShader(VertexInfo info, String pathXS, String[] defs) {
		super(info, pathXS, defs);
	}

	public CameraShader(VertexInfo info, String pathVS, String pathFS) {
		super(info, pathVS, pathFS);
	}

	public CameraShader(VertexInfo info, String pathVS, String pathFS, String[] defs) {
		super(info, pathVS, pathFS, defs);
	}
	
	public CameraShader setCamera(CameraActor camera) {
		this.camera = camera;
		return this;
	}

	public CameraShader setCamera(CameraActor camera, boolean follow) {
		this.camera = camera;
		this.followCamera = follow;
		return this;
	}

	public CameraActor getCamera() {
		return camera;
	}
	
	@Override
	protected void storeUniformLocations() {
		projectionMatrixLocation = GL20.glGetUniformLocation(pId, "projectionMatrix");
		viewMatrixLocation = GL20.glGetUniformLocation(pId, "viewMatrix");
		cameraPositionLocation = GL20.glGetUniformLocation(pId, "cameraPosition");
	}

	@Override
	public void updateUniforms() {
		uniform(projectionMatrixLocation, camera.getProjection());
		uniform(viewMatrixLocation, followCamera ? camera.getViewFollow() : camera.getView());
		if(cameraPositionLocation>=0)
			uniform(cameraPositionLocation, camera.position);
	}

}
