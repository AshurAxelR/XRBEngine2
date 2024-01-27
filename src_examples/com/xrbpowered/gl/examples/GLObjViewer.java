package com.xrbpowered.gl.examples;

import static com.xrbpowered.zoomui.MouseInfo.LEFT;
import static com.xrbpowered.zoomui.MouseInfo.RIGHT;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import com.xrbpowered.gl.client.ClientInput;
import com.xrbpowered.gl.client.UIClient;
import com.xrbpowered.gl.res.asset.AssetManager;
import com.xrbpowered.gl.res.asset.FileAssetManager;
import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.gl.res.mesh.FastMeshBuilder;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.texture.Texture;
import com.xrbpowered.gl.scene.Actor;
import com.xrbpowered.gl.scene.CameraActor;
import com.xrbpowered.gl.scene.Controller;
import com.xrbpowered.gl.scene.StaticMeshActor;
import com.xrbpowered.gl.ui.UINode;
import com.xrbpowered.gl.ui.pane.UIOffscreen;
import com.xrbpowered.gl.ui.pane.UIPane;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.MouseInfo;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.base.UIButtonBase;
import com.xrbpowered.zoomui.std.UIButton;
import com.xrbpowered.zoomui.std.UIListBox;
import com.xrbpowered.zoomui.std.UIOptionBox;
import com.xrbpowered.zoomui.std.UIToolButton;
import com.xrbpowered.zoomui.std.text.UITextBox;

public class GLObjViewer extends UIClient {

	public static final String[] defaultTextureNames = { "checker.png", "#000000", "#8080ff" };
	
	public String objName = "test.obj";
	public int selectedTexture = 0;
	public String[] textureNames = new String[3];
	public boolean wrapTexture = true;
	public boolean filterTexture = true;
	public int cullMode = 0;
	public boolean wireframe = false;
	
	private StandardShader shader;
	private Texture[] textures = new Texture[3];
	private StaticMesh mesh;
	private StaticMeshActor meshActor;
	
	private CameraActor camera = null;
	private Controller controller;
	private LightController lightController;
	private Controller activeController = null;
	
	public static class UISwitch extends UIOptionBox<Boolean> {
		private final String[] labels;
		public UISwitch(UIContainer parent, String[] labels) {
			super(parent, new Boolean[] {false, true});
			this.labels = labels;
		}
		@Override
		protected String formatOption(Boolean value) {
			return labels[value ? 1 : 0];
		}
	}
	
	public class WidgetPane extends UIPane {
		
		public final UITextBox txtObjName, txtTextureName;
		public final UIButtonBase btnReloadObj, btnReloadTexture;
		public final UIButton btnCube, btnSphere;
		public final UISwitch optWrap, optFilter, optWireframe;
		public final UIOptionBox<String> optSelTexture, optCullMode;
		
		public WidgetPane(UIContainer parent) {
			super(parent, true);
			btnCube = new UIButton(this, "Cube") {
				@Override
				public void onAction() {
					setCubeMesh();
				}
			};
			btnCube.setSize((192-20)/2, btnCube.getHeight());
			btnCube.setPosition(8, 24);
			btnSphere = new UIButton(this, "Sphere") {
				@Override
				public void onAction() {
					setSphereMesh();
				}
			};
			btnSphere.setSize(btnCube.getWidth(), btnSphere.getHeight());
			btnSphere.setPosition(8+btnCube.getWidth()+4, 24);
			txtObjName = new UITextBox(this);
			txtObjName.editor.setText(objName);
			txtObjName.setSize(192-40, txtObjName.getHeight());
			txtObjName.setPosition(8, 48);
			btnReloadObj = new UIToolButton(this, UIToolButton.iconPath+"refresh.svg", 16, 2) {
				public void onAction() {
					if(!loadObject(txtObjName.editor.getText()))
						txtObjName.editor.colorBackground = new Color(0xffeeee);
					else
						txtObjName.editor.colorBackground = UIListBox.colorBackground;
					repaint();
				}
			};
			btnReloadObj.setPosition(txtObjName.getX()+txtObjName.getWidth()+4, txtObjName.getY());
			optSelTexture = new UIOptionBox<String>(this, new String[] {"Diffuse", "Specular", "Normal"}) {
				@Override
				protected void onOptionSelected(String value) {
					selectedTexture = getSelectedIndex();
					getRoot().resetFocus();
					txtTextureName.editor.setText(textureNames[selectedTexture]);
				}
			};
			optSelTexture.setSize(192-16, optSelTexture.getHeight());
			optSelTexture.setPosition(8, 96);
			txtTextureName = new UITextBox(this);
			txtTextureName.editor.setText(defaultTextureNames[selectedTexture]);
			txtTextureName.setSize(txtObjName.getWidth(), txtObjName.getHeight());
			txtTextureName.setPosition(8, 120);
			btnReloadTexture = new UIToolButton(this, UIToolButton.iconPath+"refresh.svg", 16, 2) {
				public void onAction() {
					String s = txtTextureName.editor.getText();
					if(s.isEmpty()) {
						s = defaultTextureNames[selectedTexture];
						txtTextureName.editor.setText(s);
					}
					if(!loadTexture(s))
						txtTextureName.editor.colorBackground = new Color(0xffeeee);
					else
						txtTextureName.editor.colorBackground = UIListBox.colorBackground;
					repaint();
				}
			};
			btnReloadTexture.setPosition(txtTextureName.getX()+txtTextureName.getWidth()+4, txtTextureName.getY());
			optWrap = new UISwitch(this, new String[] {"No wrap", "Wrap"}) {
				@Override
				protected void onOptionSelected(Boolean value) {
					wrapTexture = value;
				}
			};
			optWrap.selectOption(wrapTexture);
			optWrap.setSize(txtTextureName.getWidth(), optWrap.getHeight());
			optWrap.setPosition(8, 144);
			optFilter = new UISwitch(this, new String[] {"Pixelate", "Filter"}) {
				@Override
				protected void onOptionSelected(Boolean value) {
					filterTexture = value;
				}
			};
			optFilter.selectOption(filterTexture);
			optFilter.setSize(optWrap.getWidth(), optWrap.getHeight());
			optFilter.setPosition(8, 168);
			optCullMode = new UIOptionBox<String>(this, new String[] {"No culling", "Cull back", "Cull front"}) {
				@Override
				protected void onOptionSelected(String value) {
					cullMode = getSelectedIndex();
				}
			};
			optCullMode.setSize(192-16, optCullMode.getHeight());
			optCullMode.setPosition(8, 216);
			optWireframe = new UISwitch(this, new String[] {"Solid", "Wireframe"}) {
				@Override
				protected void onOptionSelected(Boolean value) {
					wireframe = value;
				}
			};
			optWireframe.selectOption(wireframe);
			optWireframe.setSize(192-16, optWireframe.getHeight());
			optWireframe.setPosition(8, 240);
		}
		@Override
		public void layout() {
			setPosition(16, 16);
			setSize(192, 280);
			super.layout();
		}
		@Override
		protected void paintBackground(GraphAssist g) {
			g.pixelBorder(this, 1, new Color(0xeeeeee), UIButton.colorBorder);
			g.setFont(UIButton.font);
			g.setColor(UIButton.colorText);
			g.drawString("Object:", 8, 20);
			g.drawString("Texture:", 8, 92);
			g.drawString("Render:", 8, 212);
		}
		@Override
		public boolean onMouseDown(float x, float y, MouseInfo mouse) {
			getRoot().resetFocus();
			return true;
		}
	}
	
	public class LightController extends Controller {
		private Vector4f v = new Vector4f();
		public LightController(ClientInput input) {
			super(input);
			this.actor = new Actor();
		}
		public void setRotation(float x, float y) {
			actor.rotation.x = x;
			actor.rotation.y = y;
			actor.updateTransform();
			setLightDir();
		}
		public void setLightDir() {
			v.set(0, 0, 1, 0);
			actor.getTransform().transform(v);
			shader.lightDir.set(v.x, v.y, v.z).normalize();
		}
		@Override
		public void update(float dt) {
			super.update(dt);
			setLightDir();
		}
	}
	
	private void setMesh(StaticMesh m) {
		if(mesh!=null)
			mesh.release();
		mesh = m;
		meshActor.setMesh(mesh);
	}
	
	private boolean loadObject(String name) {
		StaticMesh m = ObjMeshLoader.loadObj(name, 0, 1f, StandardShader.standardVertexInfo, null);
		if(m!=null) {
			setMesh(m);
			objName = name;
			return true;
		}
		else
			return false;
	}
	
	private void setCubeMesh() {
		setMesh(FastMeshBuilder.cube(1f, StandardShader.standardVertexInfo, null));
	}

	private void setSphereMesh() {
		setMesh(FastMeshBuilder.sphere(1f, 32, StandardShader.standardVertexInfo, null));
	}

	private void setTexture(Texture t) {
		if(textures[selectedTexture]!=null)
			textures[selectedTexture].release();
		textures[selectedTexture] = t;
	}
	
	private void setTextureColor(Color c) {
		setTexture(new Texture(c));
	}
	
	private boolean loadTexture(String name) {
		if(name.startsWith("#")) {
			try {
				Color c = new Color(Integer.parseInt(name.substring(1), 16));
				setTextureColor(c);
				textureNames[selectedTexture] = name;
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		else {
			try {
				BufferedImage img = AssetManager.defaultAssets.loadImage(name);
				Texture t = new Texture(img, wrapTexture, filterTexture);
				setTexture(t);
				textureNames[selectedTexture] = name;
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	public GLObjViewer() {
		super("OBJ viewer");
		AssetManager.defaultAssets = new FileAssetManager("example_assets", AssetManager.defaultAssets);
		
		new UIOffscreen(getContainer()) {
			@Override
			public void setSize(float width, float height) {
				super.setSize(width, height);
				camera.setAspectRatio(getWidth(), getHeight());
			}
			
			@Override
			public void setupResources() {
				clearColor = new Color(0xdddddd);
				camera = new CameraActor.Perspective().setRange(0.1f, 40f).setAspectRatio(getWidth(), getHeight());
				camera.position = new Vector3f(0, 0, 2);
				camera.updateTransform();
				controller = new Controller(input).setActor(camera);
				
				shader = StandardShader.getInstance();
				shader.setFog(((CameraActor.Perspective) camera).getFar()/2, ((CameraActor.Perspective) camera).getFar(),
						new Vector4f(clearColor.getRed()/255f, clearColor.getGreen()/255f, clearColor.getBlue()/255f, 0.0f));
				shader.ambientColor.set(0.3f, 0.3f, 0.3f, 1f);
				shader.lightColor.set(0.7f, 0.7f, 0.7f, 1f);

				lightController = new LightController(input);
				lightController.setRotation((float)Math.PI*0.25f, (float)Math.PI*0.75f);
				
				meshActor =  new StaticMeshActor();
				meshActor.setTextures(textures);
				meshActor.setShader(shader);
				meshActor.position = new Vector3f(0, 0, -2);
				meshActor.updateTransform();
				setSphereMesh();
				for(int i=0; i<textures.length; i++) {
					selectedTexture = i;
					textureNames[i] = defaultTextureNames[i];
					loadTexture(textureNames[i]);
				}
				selectedTexture = 0;

				super.setupResources();
			}
			
			@Override
			public boolean onMouseDown(float x, float y, MouseInfo mouse) {
				if(mouse.eventButton==RIGHT) {
					activeController = lightController;
					getRoot().resetFocus();
					activeController.setMouseLook(true);
				}
				else if(mouse.eventButton==LEFT) {
					activeController = controller;
					getRoot().resetFocus();
					activeController.setMouseLook(true);
				}
				return true;
			}
			
			@Override
			public void updateTime(float dt) {
				meshActor.updateTransform();
				if(activeController!=null) {
					if(input.isMouseDown(1) || input.isMouseDown(0))
						activeController.update(dt);
					else {
						activeController.setMouseLook(false);
						activeController = null;
					}
				}
				super.updateTime(dt);
			}
			
			@Override
			protected void renderBuffer(RenderTarget target) {
				super.renderBuffer(target);
				shader.setCamera(camera);
				if(cullMode>0)
					GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glCullFace(cullMode==2 ? GL11.GL_FRONT : GL11.GL_BACK);
				GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, wireframe ? GL11.GL_LINE : GL11.GL_FILL);
				meshActor.draw();
				GL11.glDisable(GL11.GL_CULL_FACE);
				GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
			}
		};
		
		UINode ui = new UINode(getContainer());
		new WidgetPane(ui);
	}

	public static void main(String[] args) {
		new GLObjViewer().run();
	}

}
