package com.xrbpowered.gl.res.mesh;

import java.util.HashMap;
import java.util.List;

import com.xrbpowered.gl.res.mesh.AdvancedMeshBuilder.Vertex;
import com.xrbpowered.gl.res.shader.VertexInfo;

public class MaterialObjMeshLoader extends ObjMeshLoader {

	protected final HashMap<String, Integer> materialMap;
	protected String materialName = null;
	protected int materialId = -1;
	
	protected VertexInfo.Attribute materialAttrib;
	
	public MaterialObjMeshLoader(VertexInfo.Attribute materialAttrib, List<String> materials, float scale) {
		super(scale);
		this.materialAttrib = materialAttrib;
		this.materialMap = new HashMap<>();
		for(int i=0; i<materials.size(); i++)
			materialMap.put(materials.get(i), i);
		maxVertexArgs = 5;
	}
	
	protected Vertex getVertex(String sv) {
		if(materialId<0)
			return null;
		Vertex v = super.getVertex(sv+"/"+materialId);
		v.set(materialAttrib, 0, materialId);
		return v;
	}
	
	@Override
	protected void loadLine(String[] s, boolean skip) {
		if("f".equals(s[0]) && materialId<0)
			return;
		else if("usemtl".equals(s[0])) {
			materialName = s[1];
			Integer id = materialMap.get(s[1]);
			materialId = id==null ? -1 : id;
		}
		else
			super.loadLine(s, skip);
	}

}
