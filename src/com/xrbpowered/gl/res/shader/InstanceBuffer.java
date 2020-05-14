package com.xrbpowered.gl.res.shader;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

public class InstanceBuffer {

	private FloatBuffer instanceBuffer = null;
	
	public final int startAttrib;
	public final VertexInfo instInfo;
	private int iboId;
	
	public InstanceBuffer(int maxCount, int startAttrib, VertexInfo instInfo) {
		this.startAttrib = startAttrib;
		this.instInfo = instInfo;
		int size = maxCount * instInfo.getStride();
		instanceBuffer = BufferUtils.createByteBuffer(size).asFloatBuffer();
		
		iboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, iboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, size, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	public InstanceBuffer(int maxCount, VertexInfo instInfo) {
		this(maxCount, instInfo.start, instInfo);
	}

	public static int bindAttribLocations(Shader shader, int startIndex, String[] names) {
		for(int i=0; i<names.length; i++)
			GL20.glBindAttribLocation(shader.pId, i+startIndex, names[i]);
		return startIndex+names.length;
	}
	
	public void updateInstanceData(float[] instanceData, int count) {
		instanceBuffer.clear();
		instanceBuffer.put(instanceData, 0, count * instInfo.getSkip());
		instanceBuffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, iboId);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, instanceBuffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public void enable() {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, iboId);
		instInfo.enableAttribs();
		instInfo.initAttribPointers(1);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public void disable() {
		instInfo.disableAttribs();
	}
	
	public void release() {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(iboId);
	}
	
}
