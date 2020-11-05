package com.xrbpowered.gl.scene.comp;

import java.util.ArrayList;

import com.xrbpowered.gl.res.shader.VertexInfo;

public abstract class InstancedMeshList<T> extends InstancedMeshComponent<T> {

	public final VertexInfo instInfo;
	
	private ArrayList<T> instances = null;
	private int instCount = 0;
	
	public InstancedMeshList(VertexInfo instInfo) {
		this.instInfo = instInfo;
	}
	
	@Override
	public int getInstCount() {
		return instances==null ? instCount : instances.size();
	}

	@Override
	public void startCreateInstances() {
		instances = new ArrayList<>();
	}

	@Override
	public int addInstance(T obj) {
		instances.add(obj);
		return instances.size()-1;
	}

	protected int getDataOffs(int index) {
		return index * instInfo.getSkip();
	}
	
	@Override
	public void finishCreateInstances() {
		instCount = getInstCount();
		if(createInstanceBuffer(instCount, instInfo)) {
			float[] instanceData = createInstanceData(instCount, instInfo);
			int index = 0;
			for(T obj : instances) {
				setInstanceData(instanceData, obj, index);
				index++;
			}
			instBuffer.updateInstanceData(instanceData, instCount);
		}
		instances = null;
	}

}
