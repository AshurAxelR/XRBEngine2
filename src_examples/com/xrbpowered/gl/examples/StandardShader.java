package com.xrbpowered.gl.examples;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;

import com.xrbpowered.gl.res.shader.ActorShader;
import com.xrbpowered.gl.res.shader.VertexInfo;

public class StandardShader extends ActorShader {
	
	public static final VertexInfo standardVertexInfo = new VertexInfo()
			.addAttrib("in_Position", 3)
			.addAttrib("in_Normal", 3)
			.addAttrib("in_Tangent", 3)
			.addAttrib("in_TexCoord", 2);
	
	public Vector3f lightDir = new Vector3f(0, 0, 1);
	public Vector4f lightColor = new Vector4f(1, 1, 1, 1);
	public Vector4f ambientColor = new Vector4f(0, 0, 0, 1);
	public float time = 0f;
	
	public static final String[] SAMLER_NAMES = {"texDiffuse", "texSpecular", "texNormal"};
	
	public float specPower = 20f;
	public float alpha = 1f;
	
	private int lightDirLocation;
	private int lightColorLocation;
	private int ambientColorLocation;
	private int specPowerLocation;
	private int alphaLocation;
	private int timeLocation;

	private StandardShader() {
		super(standardVertexInfo, "std_v.glsl", "std_f.glsl");
	}
	
	protected StandardShader(String pathVS, String pathFS) {
		super(standardVertexInfo, pathVS, pathFS);
	}

	protected StandardShader(VertexInfo info, String pathVS, String pathFS) {
		super(info, pathVS, pathFS);
	}

	@Override
	protected void storeUniformLocations() {
		super.storeUniformLocations();
		lightDirLocation = GL20.glGetUniformLocation(pId, "lightDirection");
		lightColorLocation = GL20.glGetUniformLocation(pId, "lightColor");
		ambientColorLocation = GL20.glGetUniformLocation(pId, "ambientColor");
		specPowerLocation = GL20.glGetUniformLocation(pId, "specPower");
		alphaLocation = GL20.glGetUniformLocation(pId, "alpha");
		timeLocation = GL20.glGetUniformLocation(pId, "time");
		initSamplers(SAMLER_NAMES);
	}
	
	public void setPointLights(int n, Vector3f[] positions, Vector4f[] colors, Vector3f[] att) {
		GL20.glUseProgram(pId);
		GL20.glUniform1i(GL20.glGetUniformLocation(pId, "numPointLights"), n);
		for(int i=0; i<n; i++) {
			String s = String.format("pointLights[%d].", i);
			uniform(GL20.glGetUniformLocation(pId, s+"position"), positions[i]);
			uniform(GL20.glGetUniformLocation(pId, s+"att"), att[i]);
			uniform(GL20.glGetUniformLocation(pId, s+"color"), colors[i]);
		}
		GL20.glUseProgram(0);
	}
	
	public void setFog(float near, float far, Vector4f color) {
		GL20.glUseProgram(pId);
		GL20.glUniform1f(GL20.glGetUniformLocation(pId, "fogNear"), near);
		GL20.glUniform1f(GL20.glGetUniformLocation(pId, "fogFar"), far);
		uniform(GL20.glGetUniformLocation(pId, "fogColor"), color);
		GL20.glUseProgram(0);
	}

	@Override
	public void updateUniforms() {
		super.updateUniforms();

		uniform(lightDirLocation, lightDir);
		uniform(lightColorLocation, lightColor);
		uniform(ambientColorLocation, ambientColor);
		
		GL20.glUniform1f(specPowerLocation, specPower);
		GL20.glUniform1f(alphaLocation, alpha);
		GL20.glUniform1f(timeLocation, time);
	}
	
	private static StandardShader instance = null;
	
	public static StandardShader getInstance() {
		if(instance==null) {
			instance = new StandardShader();
		}
		return instance;
	}
	
	public static void destroyInstance() {
		if(instance!=null) {
			instance.release();
			instance = null;
		}
	}
	

}