package com.alienmegacorp.fileuploads;

import com.alienmegacorp.fileuploads.AbstractUploadedImage.Variant;

public class FullPicture implements Variant{

	@Override
	public int getMaxWidth() {
		return 640;
	}

	@Override
	public int getMaxHeight() {
		return 480;
	}

	@Override
	public String name() {
		return "Full";
	}
};