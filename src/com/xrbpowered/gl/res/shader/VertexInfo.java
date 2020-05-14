package com.xrbpowered.gl.res.shader;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL33;

public class VertexInfo {

	public static class Attribute {
		public final String name;
		public final int elemCount;
		public final int offset;
		public Attribute(String name, int elemCount, int offset) {
			this.name = name;
			this.elemCount = elemCount;
			this.offset = offset;
		}
	}
	
	public final int start;
	
	private int skip = 0;
	private List<Attribute> attribs = new ArrayList<>();
	
	public VertexInfo(int start) {
		this.start = start;
	}
	
	public VertexInfo() {
		this(0);
	}

	public VertexInfo(VertexInfo start) {
		this.start = start.getAttributeCount();
	}

	public VertexInfo addAttrib(String name, int elemCount) {
		this.attribs.add(new Attribute(name, elemCount, skip));
		skip += elemCount;
		return this;
	}

	public float[] createData(int count) {
		return new float[count * skip];
	}
	
	public int getStride() {
		return skip * 4;
	}
	
	public int getSkip() {
		return skip;
	}
	
	public int getAttributeCount() {
		return attribs.size();
	}
	
	public Attribute get(int index) {
		return attribs.get(index);
	}

	public int attributeIndex(String name) {
		if(name==null)
			return -1;
		for(int i=0; i<getAttributeCount(); i++) {
			Attribute a = attribs.get(i);
			if(a.name!=null && name.equals(a.name))
				return i;
		}
		return -1;
	}

	public Attribute get(String name) {
		int index = attributeIndex(name);
		if(index<0)
			return null;
		else
			return get(index);
	}
	
	public void initAttribPointers() {
		for(int i=0; i<getAttributeCount(); i++) {
			Attribute a = attribs.get(i);
			GL20.glVertexAttribPointer(i+start, a.elemCount, GL11.GL_FLOAT, false, getStride(), a.offset * 4);
		}
	}

	public void initAttribPointers(int instDivisor) {
		initAttribPointers();
		for(int i=0; i<getAttributeCount(); i++) {
			GL33.glVertexAttribDivisor(i+start, 1);
		}
	}

	public void enableAttribs() {
		for(int i=0; i<getAttributeCount(); i++) {
			GL20.glEnableVertexAttribArray(i+start);
		}
	}

	public void disableAttribs() {
		for(int i=0; i<getAttributeCount(); i++) {
			GL20.glDisableVertexAttribArray(i+start);
		}
	}

	public int bindAttribLocations(int programId) {
		return bindAttribLocations(programId, this.start);
	}

	public int bindAttribLocations(int programId, int start) {
		for(int i=0; i<getAttributeCount(); i++) {
			Attribute a = attribs.get(i);
			if(a.name!=null)
				GL20.glBindAttribLocation(programId, i+start, a.name);
		}
		return getAttributeCount();
	}
	
}
