package com.xrbpowered.gl.ui.pane;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.xrbpowered.gl.client.Client;
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
				0, 3, 2, 2, 1, 0
		});
	}
	
	private int panePositionLocation;
	private int paneSizeLocation;
	private int yscaleLocation;
	private int alphaLocation;
	private int screenSizeLocation;
	
	@Override
	protected void storeUniformLocations() {
		alphaLocation = GL20.glGetUniformLocation(pId, "alpha");
		panePositionLocation = GL20.glGetUniformLocation(pId, "panePosition");
		paneSizeLocation = GL20.glGetUniformLocation(pId, "paneSize");
		yscaleLocation = GL20.glGetUniformLocation(pId, "yscale");
		screenSizeLocation = GL20.glGetUniformLocation(pId, "screenSize");
		GL20.glUseProgram(pId);
		GL20.glUniform1i(GL20.glGetUniformLocation(pId, "tex"), 0);
		GL20.glUniform2f(screenSizeLocation, Client.getWidth(), Client.getHeight());
		GL20.glUseProgram(0);
	}
	
	public void resize() {
		GL20.glUseProgram(pId);
		GL20.glUniform2f(screenSizeLocation, Client.getWidth(), Client.getHeight()); // FIXME not static
		GL20.glUseProgram(0);
	}
	
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
	public void destroy() {
		super.destroy();
		quad.destroy();
	}
	
	private static PaneShader instance = null;
	
	public static PaneShader getInstance() {
		if(instance==null)
			instance = new PaneShader();
		return instance;
	}
	
	public static void destroyInstance() {
		if(instance!=null) {
			instance.destroy();
			instance = null;
		}
	}
}
