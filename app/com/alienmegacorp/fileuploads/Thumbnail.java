package com.alienmegacorp.fileuploads;

import com.alienmegacorp.fileuploads.AbstractUploadedImage.Variant;

public class Thumbnail implements Variant{

	@Override
	public int getMaxWidth() {
		return 260;
	}

	@Override
	public int getMaxHeight() {
		return 180;
	}

	@Override
	public String name() {
		return "Thumbnail";
	}
};