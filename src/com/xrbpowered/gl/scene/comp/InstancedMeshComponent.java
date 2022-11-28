package com.xrbpowered.gl.scene.comp;

import org.lwjgl.opengl.GL11;

import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.shader.InstanceBuffer;
import com.xrbpowered.gl.res.shader.InstanceInfo;
import com.xrbpowered.gl.res.shader.Shader;
import com.xrbpowered.gl.res.texture.Texture;

public abstract class InstancedMeshComponent<T> implements RenderComponent<T> {

	protected StaticMesh mesh = null;
	protected Texture[] textures = null;
	protected boolean culling = true;
	
	protected InstanceBuffer instBuffer = null;
	
	public void setMesh(StaticMesh mesh) {
		this.mesh = mesh;
	}
	
	public StaticMesh getMesh() {
		return mesh;
	}
	
	public void setTextures(Texture[] textures) {
		this.textures = textures;
	}
	
	public void changeTexture(int index, Texture texture) {
		this.textures[index] = texture;
	}

	public void setCulling(boolean culling) {
		this.culling = culling;
	}
	
	protected void bindTextures() {
		Texture.bindAll(textures);
	}
	
	@Override
	public void drawInstances(Shader shader) {
		if(getInstCount()==0)
			return;
		if(culling)
			GL11.glEnable(GL11.GL_CULL_FACE);
		else
			GL11.glDisable(GL11.GL_CULL_FACE);
		bindTextures();
		meshDrawCallInstanced();
	}

	public void meshDrawCallInstanced() {
		int count = getInstCount();
		if(count==0)
			return;
		mesh.enableDraw(null);
		instBuffer.enable();
		mesh.drawCallInstanced(count);
		instBuffer.disable();
		mesh.disableDraw();
	}
	
	protected boolean createInstanceBuffer(int count, InstanceInfo instInfo) {
		if(instBuffer!=null)
			releaseInstances();
		if(count>0)
			instBuffer = new InstanceBuffer(count, instInfo);
		else
			instBuffer = null;
		return instBuffer!=null;
	}
	
	protected float[] createInstanceData(int count, InstanceInfo instInfo) {
		return instInfo.createData(count);
	}
	
	protected abstract void setInstanceData(float[] instanceData, T obj, int index);
	
	@Override
	public void releaseInstances() {
		if(instBuffer!=null)
			instBuffer.release();
		instBuffer = null;
	}
	
	@Override
	public void release() {
		mesh.release();
		releaseInstances();
	}

}
