package com.xrbpowered.gl.scene.comp;

import java.util.ArrayList;

import com.xrbpowered.gl.res.shader.Shader;

public abstract class ComponentRenderer<C extends RenderComponent<?>> {
	
	protected ArrayList<C> components = new ArrayList<>(); 
	
	public C add(C comp) {
		components.add(comp);
		return comp;
	}
	
	public void releaseResources() {
		for(C comp : components)
			comp.release();
		components = null;
	}
	
	public void startCreateInstances() {
		for(C comp : components)
			comp.startCreateInstances();
	}

	public void finishCreateInstances() {
		for(C comp : components)
			comp.finishCreateInstances();
	}

	protected abstract Shader getShader();

	protected void startDrawInstances(Shader shader) {
		shader.use();
	}

	protected void finishDrawInstances(Shader shader) {
		shader.unuse();
	}

	protected void drawComponent(Shader shader, C comp) {
		comp.drawInstances(shader);
	}

	public void drawInstances() {
		Shader shader = getShader();
		startDrawInstances(shader);
		for(C comp : components)
			drawComponent(shader, comp);
		finishDrawInstances(shader);
	}
	
	public void releaseInstances() {
		for(C comp : components)
			comp.releaseInstances();
	}
	
}
