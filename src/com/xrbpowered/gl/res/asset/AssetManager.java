package com.xrbpowered.gl.res.asset;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public abstract class AssetManager {

	protected AssetManager fallback = null;
	
	public AssetManager(AssetManager fallbackAssets) {
		this.fallback = fallbackAssets;
	}
	
	public InputStream openStream(String path) throws IOException {
		try {
			return open(path);
		}
		catch(IOException e) {
			if(fallback!=null)
				return fallback.openStream(path);
			else
				throw e;
		}
	}
	
	protected abstract InputStream open(String path) throws IOException;

	public byte[] loadBytes(String path) throws IOException {
		return IOUtils.loadBytes(openStream(path));
	}
	
	public String loadString(String path) throws IOException {
		return IOUtils.loadString(openStream(path));
	}
	
	public BufferedImage loadImage(String path) throws IOException {
		return IOUtils.loadImage(openStream(path));
	}
	
	public Font loadFont(String path) throws IOException {
		return IOUtils.loadFont(openStream(path));
	}
	
	public static String relativeTo(String src, String path) {
		Path base = new File(src).getParentFile().toPath();
		Path dst = base.resolve(path);
		return dst.toString().replace(File.separatorChar, '/');
	}
	
	public static AssetManager defaultAssets = new FileAssetManager(null, null);
	
}
