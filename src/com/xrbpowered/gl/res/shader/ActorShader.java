package com.xrbpowered.gl.res.shader;

import org.lwjgl.opengl.GL20;

import com.xrbpowered.gl.scene.Actor;

public class ActorShader extends CameraShader {

	public ActorShader(VertexInfo info, String pathVS, String pathFS) {
		super(info, pathVS, pathFS);
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
	
	@Override
	public void updateUniforms() {
		if(actor==null)
			return;
		super.updateUniforms();
		uniform(modelMatrixLocation, actor.getTransform());
	}
}
