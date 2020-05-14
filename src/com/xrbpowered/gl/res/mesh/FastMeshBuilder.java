package com.xrbpowered.gl.res.mesh;

import org.joml.Vector3f;

import com.xrbpowered.gl.res.shader.VertexInfo;
import com.xrbpowered.gl.res.shader.VertexInfo.Attribute;

public class FastMeshBuilder extends MeshBuilder {

	public class Vertex extends MeshBuilder.Vertex {
		private int index = 0;
		
		private Vertex setIndex(int index) {
			this.index = index;
			return this;
		}
		
		@Override
		protected void set(Attribute attrib, int offs, float x) {
			MeshBuilder.setData(info, attrib, offs, vertexData, index, x);
		}
	}
	
	private float[] vertexData;
	private short[] indexData;
	private int indexOffs = 0;
	
	private Vertex vertexRef = new Vertex();
	
	public FastMeshBuilder(VertexInfo info, Options options, int numVertices, int numIndices) {
		super(info, options);
		this.vertexData = info.createData(numVertices);
		this.indexData = new short[numIndices];
	}

	public FastMeshBuilder(VertexInfo info, Options options, float[] vertexData, int numIndices) {
		super(info, options);
		this.vertexData = vertexData;
		this.indexData = new short[numIndices];
	}

	public float[] getVertexData() {
		return vertexData;
	}
	
	public Vertex getVertex(int index) {
		return vertexRef.setIndex(index);
	}
	
	public void setIndex(int offs, int index) {
		indexData[offs] = (short) index;
	}
	
	public void addIndex(int index) {
		indexData[indexOffs++] = (short) index;
	}

	public void addEdge(int i1, int i2) {
		addIndex(i1);
		addIndex(i2);
	}

	public void addTriangle(int i1, int i2, int i3) {
		addIndex(i1);
		addIndex(i2);
		addIndex(i3);
	}

	public void addQuad(int i1, int i2, int i3, int i4) {
		addIndex(i1);
		addIndex(i2);
		addIndex(i3);
		addIndex(i1);
		addIndex(i3);
		addIndex(i4);
	}
	
	@Override
	public StaticMesh create() {
		return new StaticMesh(info, vertexData, indexData);
	}

	public StaticMesh create(int verticesPerElement) {
		return new StaticMesh(info, vertexData, indexData, verticesPerElement, false);
	}

	public static StaticMesh cube(float size, VertexInfo info, Options options) {
		float d = size / 2f;
		Vector3f norm = new Vector3f();
		Vector3f tan = new Vector3f();
		FastMeshBuilder mb = new FastMeshBuilder(info, options, 4 * 6, 6 * 6);
		
		norm.set(0, 0, -1);
		tan.set(-1, 0, 0);
		mb.getVertex(0).setPosition(-d, -d, -d).setTexCoord(1, 1).setNormal(norm).setTangent(tan);
		mb.getVertex(1).setPosition(-d, d, -d).setTexCoord(1, 0).setNormal(norm).setTangent(tan);
		mb.getVertex(2).setPosition(d, d, -d).setTexCoord(0, 0).setNormal(norm).setTangent(tan);
		mb.getVertex(3).setPosition(d, -d, -d).setTexCoord(0, 1).setNormal(norm).setTangent(tan);

		norm.set(0, 0, 1);
		tan.set(1, 0, 0);
		mb.getVertex(4).setPosition(d, -d, d).setTexCoord(1, 1).setNormal(norm).setTangent(tan);
		mb.getVertex(5).setPosition(d, d, d).setTexCoord(1, 0).setNormal(norm).setTangent(tan);
		mb.getVertex(6).setPosition(-d, d, d).setTexCoord(0, 0).setNormal(norm).setTangent(tan);
		mb.getVertex(7).setPosition(-d, -d, d).setTexCoord(0, 1).setNormal(norm).setTangent(tan);

		norm.set(0, 1, 0);
		tan.set(1, 0, 0);
		mb.getVertex(8).setPosition(-d, d, -d).setTexCoord(0, 0).setNormal(norm).setTangent(tan);
		mb.getVertex(9).setPosition(-d, d, d).setTexCoord(0, 1).setNormal(norm).setTangent(tan);
		mb.getVertex(10).setPosition(d, d, d).setTexCoord(1, 1).setNormal(norm).setTangent(tan);
		mb.getVertex(11).setPosition(d, d, -d).setTexCoord(1, 0).setNormal(norm).setTangent(tan);
		
		norm.set(0, -1, 0);
		tan.set(-1, 0, 0);
		mb.getVertex(12).setPosition(-d, -d, d).setTexCoord(0, 0).setNormal(norm).setTangent(tan);
		mb.getVertex(13).setPosition(-d, -d, -d).setTexCoord(0, 1).setNormal(norm).setTangent(tan);
		mb.getVertex(14).setPosition(d, -d, -d).setTexCoord(1, 1).setNormal(norm).setTangent(tan);
		mb.getVertex(15).setPosition(d, -d, d).setTexCoord(1, 0).setNormal(norm).setTangent(tan);

		norm.set(-1, 0, 0);
		tan.set(0, 1, 0);
		mb.getVertex(16).setPosition(-d, -d, d).setTexCoord(1, 1).setNormal(norm).setTangent(tan);
		mb.getVertex(17).setPosition(-d, d, d).setTexCoord(1, 0).setNormal(norm).setTangent(tan);
		mb.getVertex(18).setPosition(-d, d, -d).setTexCoord(0, 0).setNormal(norm).setTangent(tan);
		mb.getVertex(19).setPosition(-d, -d, -d).setTexCoord(0, 1).setNormal(norm).setTangent(tan);

		norm.set(1, 0, 0);
		tan.set(0, 1, 0);
		mb.getVertex(20).setPosition(d, -d, -d).setTexCoord(1, 1).setNormal(norm).setTangent(tan);
		mb.getVertex(21).setPosition(d, d, -d).setTexCoord(1, 0).setNormal(norm).setTangent(tan);
		mb.getVertex(22).setPosition(d, d, d).setTexCoord(0, 0).setNormal(norm).setTangent(tan);
		mb.getVertex(23).setPosition(d, -d, d).setTexCoord(0, 1).setNormal(norm).setTangent(tan);
		
		for(int i=0; i<4*6; i+=4)
			mb.addQuad(i+0, i+1, i+2, i+3);
		
		return mb.create();
	}

	public static StaticMesh plane(float size, int segm, int tileTex, VertexInfo info, Options options) {
		int i, j;
		float d = size / segm;
		
		FastMeshBuilder mb = new FastMeshBuilder(info, options, (segm+1) * (segm+1), segm * segm * 6);
		
		Vector3f v = new Vector3f();
		int index = 0;
		for(i=0; i<=segm; i++) {
			for(j=0; j<=segm; j++) {
				Vertex vertex = mb.getVertex(index);
				v.x = -size/2f + i*d;
				v.y = 0;
				v.z = -size/2f + j*d;
				vertex.setPosition(v);
				vertex.setNormal(0, 1, 0);
				vertex.setTangent(1, 0, 0);
				vertex.setTexCoord(i * tileTex / (float) segm, j * tileTex / (float) segm);
				index++;
			}
		}
		
		for(i=0; i<segm; i++) {
			for(j=0; j<segm; j++) {
				mb.addQuad(
					(i+0) * (segm+1) + (j+0),
					(i+0) * (segm+1) + (j+1),
					(i+1) * (segm+1) + (j+1),
					(i+1) * (segm+1) + (j+0)
				);
			}
		}
		
		return mb.create();
	}
	
	public static StaticMesh terrain(float size, float[][] hmap, int tileTex, VertexInfo info, Options options) {
		int i, j;
		int segm = hmap.length - 1;
		float d = size / segm;

		FastMeshBuilder mb = new FastMeshBuilder(info, options, (segm+1) * (segm+1), segm * segm * 6);

		Vector3f v = new Vector3f();
		Vector3f n = new Vector3f();
		int index = 0;
		for(i=0; i<=segm; i++) {
			for(j=0; j<=segm; j++) {
				Vertex vertex = mb.getVertex(index);
				v.x = -size/2f + i*d;
				v.y = hmap[i][j];
				v.z = -size/2f + j*d;
				vertex.setPosition(v);
				
				v.set(0, 0, 0);
				if(i>0) {
					n.set(hmap[i][j] - hmap[i-1][j], d, 0);
					n.normalize();
					v.add(n);
				}
				if(i<segm) {
					n.set(hmap[i+1][j] - hmap[i][j], d, 0);
					n.normalize();
					v.add(n);
				}
				if(j>0) {
					n.set(0, d, hmap[i][j] - hmap[i][j-1]);
					n.normalize();
					v.add(n);
				}
				if(j<segm) {
					n.set(0, d, hmap[i][j+1] - hmap[i][j]);
					n.normalize();
					v.add(n);
				}
				v.normalize();
				vertex.setNormal(-v.x, v.y, -v.z);
				
				vertex.setTangent(1, 0, 0);
				vertex.setTexCoord(i * tileTex / (float) segm, j * tileTex / (float) segm);
				index++;
			}
		}
		
		for(i=0; i<segm; i++) {
			for(j=0; j<segm; j++) {
				mb.addQuad(
					(i+0) * (segm+1) + (j+0),
					(i+0) * (segm+1) + (j+1),
					(i+1) * (segm+1) + (j+1),
					(i+1) * (segm+1) + (j+0)
				);
			}
		}
		
		return mb.create();
	}
	
	public static StaticMesh sphere(float r, int segm, VertexInfo info, Options options) {
		int i, j;
		
		float[] sin = new float[segm*2+1];
		float[] cos = new float[segm*2+1];
		float ai;
		float da = (float) Math.PI / (float) segm;
		for(i=0, ai = 0; i<=segm*2; i++, ai += da) {
			sin[i] = (float) Math.sin(ai);
			cos[i] = (float) Math.cos(ai);
		}
		
		FastMeshBuilder mb = new FastMeshBuilder(info, options, (segm+1) * (segm*2+1), segm * segm * 2 * 6);
		
		Vector3f v = new Vector3f();
		int index = 0;
		for(i=0; i<=segm*2; i++) {
			for(j=0; j<=segm; j++) {
				Vertex vertex = mb.getVertex(index);
				float r0 = r * sin[j];
				v.y = -r * cos[j];
				v.x = r0 * cos[i];
				v.z = r0 * sin[i];
				vertex.setPosition(v);
				vertex.setNormal(v.x/r, v.y/r, v.z/r);
				vertex.setTangent((r0>0f) ? -v.z/r0 : v.y/r, 0, (r0>0f) ? v.x/r0 : v.y/r);
				vertex.setTexCoord(i / (float) segm, j / (float) segm);
				index++;
			}
		}
		
		for(i=0; i<segm*2; i++) {
			for(j=0; j<segm; j++) {
				mb.addQuad(
					(i+0) * (segm+1) + (j+0),
					(i+0) * (segm+1) + (j+1),
					(i+1) * (segm+1) + (j+1),
					(i+1) * (segm+1) + (j+0)
				);
			}
		}
		
		return mb.create();
	}
}
