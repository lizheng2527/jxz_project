package com.zdhx.androidbase.ui.paint.interfaces;

import android.graphics.Path;

import com.zdhx.androidbase.ui.paint.painttools.FirstCurrentPosition;


public interface Shapable {
	public Path getPath();

	public FirstCurrentPosition getFirstLastPoint();

	void setShap(ShapesInterface shape);
}
