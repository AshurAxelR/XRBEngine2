package com.xrbpowered.gl.res.shader;

import java.awt.Color;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.xrbpowered.gl.res.asset.AssetManager;

public abstract class Shader {

	public final VertexInfo info;
	protected int pId;
	
	protected Shader(VertexInfo info) {
		this.info = info;
	}
	
	public Shader(VertexInfo info, String pathVS, String pathFS) {
		this.info = info;
		int vsId = loadShader(pathVS, GL20.GL_VERTEX_SHADER);
		int fsId = loadShader(pathFS, GL20.GL_FRAGMENT_SHADER);

		pId = GL20.glCreateProgram();
		if(vsId>0)
			GL20.glAttachShader(pId, vsId);
		if(fsId>0)
			GL20.glAttachShader(pId, fsId);
		
		bindAttribLocations();
		
		GL20.glLinkProgram(pId);
		if (GL20.glGetProgrami(pId, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
			System.err.println("Could not link program "+pathVS+", "+pathFS);
			System.err.println(GL20.glGetProgramInfoLog(pId, 8000));
			System.exit(-1);
		}
		GL20.glValidateProgram(pId);
		
		storeUniformLocations();
	}
	
	protected abstract void storeUniformLocations();
	public abstract void updateUniforms(); 
	
	public int getProgramId() {
		return pId;
	}
	
	public void use() {
		GL20.glUseProgram(pId);
		updateUniforms();
	}
	
	public void unuse() {
		GL20.glUseProgram(0);
	}
	
	public void release() {
		GL20.glUseProgram(0);
		GL20.glDeleteProgram(pId);
	}
	
	protected void bindAttribLocations() {
		info.bindAttribLocations(pId);
	}
	
	protected void initSamplers(String[] names) {
		if(names==null)
			return;
		
		GL20.glUseProgram(pId);
		for(int i=0; i<names.length; i++) {
			GL20.glUniform1i(GL20.glGetUniformLocation(pId, names[i]), i);
		}
		GL20.glUseProgram(0);
	}
	
	public static int loadShader(String path, int type) {
		if(path==null)
			return 0;
		int shaderId = 0;
		String shaderSource;
		try {
			shaderSource = AssetManager.defaultAssets.loadString(path);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		shaderId = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderId, shaderSource);
		GL20.glCompileShader(shaderId);

		if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.err.println("Could not compile shader "+path);
			System.err.println(GL20.glGetShaderInfoLog(shaderId, 8000));
			System.exit(-1); // TODO handle this exception
		}
		
		return shaderId;
	}
	
	private static final FloatBuffer matrix4Buffer = BufferUtils.createFloatBuffer(16);
	public static void uniform(int location, Matrix4f matrix) {
		matrix.get(matrix4Buffer);
		GL20.glUniformMatrix4fv(location, false, matrix4Buffer);
	}

	private static final FloatBuffer matrix3Buffer = BufferUtils.createFloatBuffer(9);
	public static void uniform(int location, Matrix3f matrix) {
		matrix.get(matrix3Buffer);
		GL20.glUniformMatrix3fv(location, false, matrix3Buffer);
	}

	private static final FloatBuffer vec4Buffer = BufferUtils.createFloatBuffer(4);
	public static void uniform(int location, Vector4f v) {
		v.get(vec4Buffer);
		GL20.glUniform4fv(location, vec4Buffer);
	}

	public static void uniform(int location, Color c) {
		GL20.glUniform4f(location, c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f, c.getAlpha()/255f);
	}

	private static final FloatBuffer vec3Buffer = BufferUtils.createFloatBuffer(3);
	public static void uniform(int location, Vector3f v) {
		v.get(vec3Buffer);
		GL20.glUniform3fv(location, vec3Buffer);
	}

	private static final FloatBuffer vec2Buffer = BufferUtils.createFloatBuffer(2);
	public static void uniform(int location, Vector2f v) {
		v.get(vec2Buffer);
		GL20.glUniform2fv(location, vec2Buffer);
	}

}
