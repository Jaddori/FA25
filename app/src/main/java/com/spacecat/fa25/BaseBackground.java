package com.spacecat.fa25;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by Tunder on 2018-02-24.
 */

public abstract class  BaseBackground
{
	protected Resources resources;
	protected Paint paint;
	protected int redrawDelay;
	protected int elapsed;

	public int getRedrawDelay() { return redrawDelay; }

	public BaseBackground( Resources resources )
	{
		this.resources = resources;
		initialize();
	}

	protected void initialize()
	{
		paint = new Paint();
	}

	public void reset()
	{
		elapsed = 0;
	}

	public void onDraw( Canvas canvas, Rect bounds )
	{
		elapsed++;
	}
}
