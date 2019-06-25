package com.xrbpowered.gl.scene;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.shader.ActorShader;
import com.xrbpowered.gl.res.shader.VertexInfo;

public class ActorPicker {

	private static final int[] attribMask = {0};

	public class ActorPickerShader extends ActorShader {
		
		private ActorPickerShader() {
			super(new VertexInfo().addAttrib("in_Position", 3), "pick_v.glsl", "pick_f.glsl");
		}
		
		private int objIdLocation;
		
		@Override
		protected void storeUniformLocations() {
			super.storeUniformLocations();
			objIdLocation = GL20.glGetUniformLocation(pId, "objId");
		}
		
		public void updateUniforms(Actor actor, int objId) {
			setActor(actor);
			updateUniforms();
			GL20.glUniform3f(objIdLocation, (float)((objId>>16)&0xff) / 255f, (float)((objId>>8)&0xff) / 255f, (float)(objId&0xff) / 255f);
		}
		
	}

	public  ActorPickerShader shader;
	
	private int x, y;
	private ByteBuffer pixels = ByteBuffer.allocateDirect(4);
	
	public ActorPicker(CameraActor camera) {
		shader = new ActorPickerShader();
		shader.setCamera(camera);
	}
	
	public void startPicking(int x, int y, RenderTarget pickTarget) {
		this.x = x;
		this.y = y;
		pickTarget.use();
		GL11.glScissor(x, y, 1, 1);
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glClearColor(0f, 0f, 0f, 0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		shader.use();
	}
	
	public void drawActor(StaticMeshActor actor, int objId) {
		shader.updateUniforms(actor, objId);
		actor.getMesh().draw(attribMask);
	}

	public void drawSceneMesh(StaticMesh mesh, int objId) {
		shader.updateUniforms(null, objId);
		mesh.draw(attribMask);
	}

	public int finishPicking(RenderTarget nextTarget) {
		GL11.glReadPixels(x, y, 1, 1, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, pixels);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		nextTarget.use();
		return pixels.asIntBuffer().get(0) >> 8;
	}
	
	public void release() {
		shader.release();
	}
}
