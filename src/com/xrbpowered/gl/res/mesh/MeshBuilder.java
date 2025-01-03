package com.xrbpowered.gl.res.mesh;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.xrbpowered.gl.res.shader.VertexInfo;

public abstract class MeshBuilder {

	public enum BuilderAttribute {
		position("in_Position"),
		normal("in_Normal"),
		tangent("in_Tangent"),
		texCoord("in_TexCoord"),
		color("in_Color");
		
		public final String defaultName;
		
		private BuilderAttribute(String defaultName) {
			this.defaultName = defaultName;
		}
	}
	
	public abstract class Vertex {
		public abstract Vertex set(VertexInfo.Attribute attrib, int offs, float x);
		
		public Vertex set(VertexInfo.Attribute attrib, float x, float y, float z, float w) {
			if(attrib!=null) {
				set(attrib, 0, x);
				set(attrib, 1, y);
				set(attrib, 2, z);
				set(attrib, 3, w);
			}
			return this;
		}

		public Vertex setPosition(float x, float y, float z, float w) {
			return set(positionAttrib, x, y, z, w);
		}

		public Vertex setPosition(float x, float y, float z) {
			return set(positionAttrib, x, y, z, 1f);
		}

		public Vertex setPosition(Vector4f v) {
			return set(positionAttrib, v.x, v.y, v.z, v.w);
		}
		
		public Vertex setPosition(Vector3f v) {
			return set(positionAttrib, v.x, v.y, v.z, 1f);
		}

		public Vertex setNormal(float x, float y, float z) {
			return set(normalAttrib, x, y, z, 0f);
		}

		public Vertex setNormal(Vector3f v) {
			return set(normalAttrib, v.x, v.y, v.z, 0f);
		}
		
		public Vertex setTangent(float x, float y, float z) {
			return set(tangentAttrib, x, y, z, 0f);
		}
		
		public Vertex setTangent(Vector3f v) {
			return set(tangentAttrib, v.x, v.y, v.z, 0f);
		}

		public Vertex setTexCoord(float u, float v) {
			return set(texCoordAttrib, u, v, 0f, 0f);
		}

		public Vertex setTexCoord(Vector2f v) {
			return set(texCoordAttrib, v.x, v.y, 0f, 0f);
		}

		public Vertex setColor(float r, float g, float b, float a) {
			return set(colorAttrib, r, g, b, a);
		}
		
		public Vertex setColor(float r, float g, float b) {
			return set(colorAttrib, r, g, b, 1f);
		}
		
		public Vertex setColor(Vector4f v) {
			return set(colorAttrib, v.x, v.y, v.z, v.w);
		}

		public Vertex setColor(Vector3f v) {
			return set(colorAttrib, v.x, v.y, v.z, 1f);
		}
	}
	
	public final VertexInfo info;
	
	protected VertexInfo.Attribute positionAttrib;
	protected VertexInfo.Attribute normalAttrib;
	protected VertexInfo.Attribute tangentAttrib;
	protected VertexInfo.Attribute texCoordAttrib;
	protected VertexInfo.Attribute colorAttrib;
	
	public MeshBuilder(VertexInfo info) {
		this.info = info;
		positionAttrib = info.get(BuilderAttribute.position);
		normalAttrib = info.get(BuilderAttribute.normal);
		tangentAttrib = info.get(BuilderAttribute.tangent);
		texCoordAttrib = info.get(BuilderAttribute.texCoord);
		colorAttrib = info.get(BuilderAttribute.color);
	}

	public abstract StaticMesh create();
	
	protected static void setData(VertexInfo info, VertexInfo.Attribute attrib, int offs, float[] data, int index, float x) {
		if(offs<attrib.elemCount) {
			data[offs + attrib.offset + index*info.getSkip()] = x;
		}
	}
	
}
