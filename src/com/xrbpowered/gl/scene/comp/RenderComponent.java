package com.xrbpowered.gl.scene.comp;

import com.xrbpowered.gl.res.shader.Shader;

public interface RenderComponent<T> {

	public void startCreateInstances();
	public int addInstance(T obj);
	public void finishCreateInstances();

	public int getInstCount();
	public void drawInstances(Shader shader);
	public void releaseInstances();
	
	public default void release() {
		releaseInstances();
	}
}
