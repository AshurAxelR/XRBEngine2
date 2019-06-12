package com.xrbpowered.gl.ui.pane;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.shader.Shader;
import com.xrbpowered.gl.res.shader.VertexInfo;

public class PaneShader extends Shader {
	
	public static VertexInfo vertexInfo = new VertexInfo()
			.addAttrib("in_Position", 2)
			.addAttrib("in_TexCoord", 2);

	public final StaticMesh quad;
	
	private PaneShader() {
		super(vertexInfo, "pane_v.glsl", "pane_f.glsl");
		quad = new StaticMesh(PaneShader.vertexInfo, new float[] {
				0, 0, 0, 0,
				1, 0, 1, 0,
				1, 1, 1, 1,
				0, 1, 0, 1
		}, new short[] {
				0, 1, 2, 0, 2, 3
		});
	}
	
	private int panePositionLocation;
	private int paneSizeLocation;
	private int yscaleLocation;
	private int alphaLocation;
	private int screenSizeLocation;
	
	private float screenWidth = 0;
	private float screenHeight = 0;
	
	@Override
	protected void storeUniformLocations() {
		alphaLocation = GL20.glGetUniformLocation(pId, "alpha");
		panePositionLocation = GL20.glGetUniformLocation(pId, "panePosition");
		paneSizeLocation = GL20.glGetUniformLocation(pId, "paneSize");
		yscaleLocation = GL20.glGetUniformLocation(pId, "yscale");
		screenSizeLocation = GL20.glGetUniformLocation(pId, "screenSize");
		GL20.glUseProgram(pId);
		GL20.glUniform1i(GL20.glGetUniformLocation(pId, "tex"), 0);
		GL20.glUseProgram(0);
	}
	
	/*public void resize() { // TODO can be resized automatically
		GL20.glUseProgram(pId);
		GL20.glUniform2f(screenSizeLocation, Client.getWidth(), Client.getHeight()); // FIXME not static
		GL20.glUseProgram(0);
	}*/
	
	@Override
	public void use() {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		super.use();
	}
	
	@Override
	public void updateUniforms() {
	}

	public void updateScreenSize(float width, float height) {
		if(width!=screenWidth || height!=screenHeight) {
			GL20.glUniform2f(screenSizeLocation, width, height);
			screenWidth = width;
			screenHeight = height;
		}
	}

	public void updateScreenSize(RenderTarget target) {
		updateScreenSize(target.getWidth(), target.getHeight());
	}
	
	public void updateUniforms(float x, float y, float width, float height, float alpha, boolean ydown) {
		GL20.glUniform2f(panePositionLocation, x, y);
		GL20.glUniform2f(paneSizeLocation, width, height);
		GL20.glUniform1f(alphaLocation, alpha);
		GL20.glUniform1f(yscaleLocation, ydown ? -1 : 1);
	}
	
	@Override
	public void unuse() {
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		super.unuse();
	}
	
	@Override
	public void release() {
		super.release();
		quad.release();
	}
	
	private static PaneShader instance = null;
	
	public static void createInstance() {
		instance = new PaneShader();
	}
	
	public static PaneShader getInstance() {
		return instance;
	}
	
	public static void releaseInstance() {
		instance.release();
	}
}
