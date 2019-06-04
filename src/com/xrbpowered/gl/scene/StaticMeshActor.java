package com.xrbpowered.gl.scene;

import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.shader.ActorShader;
import com.xrbpowered.gl.res.texture.Texture;

public class StaticMeshActor extends Actor {

	private StaticMesh mesh = null;
	private ActorShader shader = null;
	private Texture[] textures = null;
	
	public void setMesh(StaticMesh mesh) {
		this.mesh = mesh;
	}
	
	public StaticMesh getMesh() {
		return mesh;
	}
	
	public void setShader(ActorShader shader) {
		this.shader = shader;
	}
	
	public void setTextures(Texture[] textures) {
		this.textures = textures;
	}
	
	public void changeTexture(int index, Texture texture) {
		this.textures[index] = texture;
	}
	
	public void draw() {
		if(shader==null || mesh==null)
			return;
		shader.setActor(this);
		shader.use();
		if(textures!=null) {
			for(int i=0; i<textures.length; i++)
				textures[i].bind(i);
		}
		mesh.draw();
		shader.unuse();
	}
	
	public static StaticMeshActor make(final StaticMesh mesh, final ActorShader shader, final Texture diffuse) {
		StaticMeshActor actor =  new StaticMeshActor();
		actor.setMesh(mesh);
		actor.setShader(shader);
		actor.setTextures(new Texture[] {diffuse});
		return actor;
	}
}
