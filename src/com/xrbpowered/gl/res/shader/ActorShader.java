package com.xrbpowered.gl.res.shader;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL20;

import com.xrbpowered.gl.scene.Actor;

public class ActorShader extends CameraShader {

	public ActorShader(VertexInfo info, String pathXS) {
		super(info, pathXS);
	}

	public ActorShader(VertexInfo info, String pathXS, String[] defs) {
		super(info, pathXS, defs);
	}

	public ActorShader(VertexInfo info, String pathVS, String pathFS) {
		super(info, pathVS, pathFS);
	}

	public ActorShader(VertexInfo info, String pathVS, String pathFS, String[] defs) {
		super(info, pathVS, pathFS, defs);
	}
	
	private int modelMatrixLocation;
	
	protected Actor actor = null;
	
	public void setActor(Actor actor) {
		this.actor = actor;
	}
	
	public Actor getActor() {
		return actor;
	}
	
	@Override
	protected void storeUniformLocations() {
		super.storeUniformLocations();
		modelMatrixLocation = GL20.glGetUniformLocation(pId, "modelMatrix");
	}

	private static Matrix4f identity = new Matrix4f().identity();
	
	@Override
	public void updateUniforms() {
		super.updateUniforms();
		uniform(modelMatrixLocation, actor!=null ? actor.getTransform() : identity);
	}
}
