package com.xrbpowered.gl.res.mesh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Scanner;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.xrbpowered.gl.res.asset.AssetManager;
import com.xrbpowered.gl.res.mesh.AdvancedMeshBuilder.Vertex;
import com.xrbpowered.gl.res.shader.VertexInfo;

public class ObjMeshLoader {
	protected static final float NORM_EPSILON = 0.01f;

	protected int maxVertexArgs = 4;
	protected int maxLineArgs = 5;
	
	protected ArrayList<Vector3f> v = new ArrayList<>();
	protected ArrayList<Vector2f> vt = new ArrayList<>();
	protected ArrayList<Vector3f> vn = new ArrayList<>();

	protected AdvancedMeshBuilder builder;
	protected HashMap<String, Vertex> indexMap = new HashMap<>();
	
	protected final float scale;

	protected String objectName = null;

	protected ObjMeshLoader(float scale) {
		this.scale = scale;
	}
	
	protected static Vector2f vec2(String[] s) {
		return new Vector2f(Float.parseFloat(s[1]), 1f-Float.parseFloat(s[2]));
	}

	protected static Vector3f vec3(String[] s) {
		return new Vector3f(Float.parseFloat(s[1]), Float.parseFloat(s[2]), Float.parseFloat(s[3]));
	}
	
	protected Vertex getVertex(String sv) {
		Vertex v = indexMap.get(sv);
		if(v==null) {
			v = builder.addVertex(); 
			String[] s = sv.split("\\/", maxVertexArgs);
			v.setPosition(this.v.get(Integer.parseInt(s[0])-1));
			if(s.length>1 && !s[1].isEmpty())
				v.setTexCoord(this.vt.get(Integer.parseInt(s[1])-1));
			if(s.length>2 && !s[2].isEmpty()) {
				Vector3f norm = this.vn.get(Integer.parseInt(s[2])-1);
				v.setNormal(norm);
				if(Math.abs(Math.abs(norm.x)-1f)<NORM_EPSILON)
					v.setTangent(new Vector3f(0, 1, 0));
				else
					v.setTangent(new Vector3f(1, 0, 0));
			}
			indexMap.put(sv, v);
		}
		return v;
	}
	
	protected Vector3f adjustPosition(Vector3f v) {
		return v.mul(scale);
	}

	protected Vector3f adjustNormal(Vector3f vn) {
		return vn;
	}

	protected Vector2f adjustTexCoord(Vector2f vt) {
		return vt;
	}
	
	protected void loadLine(String[] s, boolean skip) {
		if("v".equals(s[0]))
			v.add(skip ? null : adjustPosition(vec3(s)));
		else if("vn".equals(s[0]))
			vn.add(skip ? null : adjustNormal(vec3(s)));
		else if("vt".equals(s[0]))
			vt.add(skip ? null : adjustTexCoord(vec2(s)));
		else if("f".equals(s[0]) && !skip) {
			if(s.length==4)
				builder.add(new AdvancedMeshBuilder.Triangle(getVertex(s[1]), getVertex(s[2]), getVertex(s[3])) /*.calcTangents()*/ );
			else if(s.length==5)
				builder.add(new AdvancedMeshBuilder.Quad(getVertex(s[1]), getVertex(s[2]), getVertex(s[3]), getVertex(s[4])) /*.calcTangents()*/ );
			else if(s.length==3)
				builder.add(new AdvancedMeshBuilder.Edge(getVertex(s[1]), getVertex(s[2])));
			else throw new RuntimeException("Unknown face type; can do only edges, tris, and quads.");
		}
	}

	protected AdvancedMeshBuilder load(Scanner in, boolean skip) {
		while(in.hasNextLine()) {
			String[] s = in.nextLine().split("\\s+", maxLineArgs);
			if("o".equals(s[0])) {
				objectName = s[1];
				return builder;
			}
			else {
				loadLine(s, skip);
			}
		}
		objectName = null;
		return builder;
	}
	
	protected void setBuilder(AdvancedMeshBuilder builder) {
		this.builder = builder;
		this.indexMap.clear();
	}
	
	protected AdvancedMeshBuilder load(Scanner in, AdvancedMeshBuilder builder) {
		setBuilder(builder);
		return load(in, false);
	}
	
	protected String skip(Scanner in) {
		load(in, true);
		return objectName;
	}
	
	public LinkedHashMap<String, AdvancedMeshBuilder> loadBuilders(Scanner in, VertexInfo info) {
		try {
			LinkedHashMap<String, AdvancedMeshBuilder> builders = new LinkedHashMap<>();
			String name = skip(in);
			while(name!=null) {
				AdvancedMeshBuilder b = load(in, new AdvancedMeshBuilder(info));
				builders.put(name, b);
				name = objectName;
			}
			return builders;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public StaticMesh loadObj(Scanner in, String objName, VertexInfo info) {
		String name = skip(in);
		while(name!=null && !objName.equals(name))
			name = skip(in);
		StaticMesh m = load(in, new AdvancedMeshBuilder(info)).create();
		return m;
	}
	
	public StaticMesh loadObj(Scanner in, int objIndex, VertexInfo info) {
		for(int i=0; i<objIndex+1; i++)
			skip(in);
		StaticMesh m = load(in, new AdvancedMeshBuilder(info)).create();
		return m;
	}
	
	public LinkedHashMap<String, AdvancedMeshBuilder> loadBuilders(String path, VertexInfo info) {
		try {
			Scanner in = new Scanner(AssetManager.defaultAssets.openStream(path));
			LinkedHashMap<String, AdvancedMeshBuilder> builders = loadBuilders(in, info);
			in.close();
			return builders;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public StaticMesh loadObj(String path, String objName, VertexInfo info) {
		try {
			Scanner in = new Scanner(AssetManager.defaultAssets.openStream(path));
			StaticMesh m = loadObj(in, objName, info);
			in.close();
			return m;
		}
		catch(Exception e) {
			System.err.printf("Error loading OBJ from %s\n", path);
			e.printStackTrace();
			return null;
		}
	}
	
	public StaticMesh loadObj(String path, int objIndex, VertexInfo info) {
		try {
			Scanner in = new Scanner(AssetManager.defaultAssets.openStream(path));
			StaticMesh m = loadObj(in, objIndex, info);
			in.close();
			return m;
		}
		catch(Exception e) {
			System.err.printf("Error loading OBJ from %s\n", path);
			e.printStackTrace();
			return null;
		}
	}
	
	public static StaticMesh loadObj(String path, String objName, float scale, VertexInfo info) {
		return new ObjMeshLoader(scale).loadObj(path, objName, info);
	}
	
	public static StaticMesh loadObj(String path, int objIndex, float scale, VertexInfo info) {
		return new ObjMeshLoader(scale).loadObj(path, objIndex, info);
	}
}
