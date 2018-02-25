package com.spacecat.fa25;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

/**
 * Created by Tunder on 2018-02-24.
 */

public class BackgroundPulse extends BaseBackground
{
	public BackgroundPulse( Resources resources )
	{
		super( resources );
	}

	@Override
	protected void initialize()
	{
		super.initialize();

		paint.setColor( Color.YELLOW );
		redrawDelay = 30;
	}

	@Override
	public void onDraw( Canvas canvas, Rect bounds )
	{
		super.onDraw( canvas, bounds );

		double sinValue = ( Math.sin( elapsed * 0.1 ) + 1.0 ) * 0.5;
		int colorValue = (int)(sinValue * 255.0);

		paint.setColor( Color.argb( 255, colorValue, colorValue, 0 ) );

		canvas.drawRect( bounds, paint );
	}
}
