package com.xrbpowered.gl.res.shader;

public class InstanceInfo extends VertexInfo {

	public final VertexInfo vertexInfo;
	
	public InstanceInfo(VertexInfo vertexInfo) {
		this.vertexInfo = vertexInfo;
	}
	
	@Override
	public InstanceInfo addAttrib(String name, int elemCount) {
		super.addAttrib(name, elemCount);
		return this;
	}
	
	@Override
	public int getStartAttributeIndex() {
		return vertexInfo.getAttributeCount();
	}
	
	@Override
	public void bindAttribLocations(int programId) {
		vertexInfo.bindAttribLocations(programId);
		super.bindAttribLocations(programId);
	}

}
