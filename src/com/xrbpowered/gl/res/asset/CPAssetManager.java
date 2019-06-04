package com.xrbpowered.gl.res.asset;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class CPAssetManager extends AssetManager {

	public final String basePath;
	
	public CPAssetManager(String basePath, AssetManager fallbackAssets) {
		super(fallbackAssets);
		this.basePath = basePath;
	}

	@Override
	protected InputStream open(String path) throws IOException {
		InputStream s = ClassLoader.getSystemResourceAsStream(basePath==null ? path : basePath+"/"+path);
		if(s==null)
			throw new FileNotFoundException(path);
		return s;
	}

}
