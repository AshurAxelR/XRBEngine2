package com.xrbpowered.gl.res.asset;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileAssetManager extends AssetManager {

	public final String basePath; 
	
	public FileAssetManager(String basePath, AssetManager fallbackAssets) {
		super(fallbackAssets);
		this.basePath = basePath;
	}

	@Override
	protected InputStream open(String path) throws IOException {
		return new FileInputStream(new File(basePath, path));
	}

}
