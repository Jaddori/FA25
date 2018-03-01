package com.spacecat.fa25;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by Nix on 2018-03-01.
 */

public class BackgroundSlides extends BaseBackground
{
	private final int DIRECTION_RIGHT = 0;
	private final int DIRECTION_DOWN = 1;
	private final int DIRECTION_LEFT = 2;
	private final int DIRECTION_UP = 3;
	private final int DIRECTIONS = 4;

	private Random random;
	private int direction;
	private float offset;
	private int color;
	private int previousColor;

	BackgroundSlides( Resources resources )
	{
		super( resources );
	}

	@Override
	protected void initialize()
	{
		super.initialize();

		redrawDelay = 30;

		random = new Random();
	}

	@Override
	public void reset()
	{
		super.reset();

		direction = DIRECTION_RIGHT;
		offset = 0.0f;
		previousColor = Color.BLACK;
		color =  generateRandomColor();
	}

	private int generateRandomColor()
	{
		return Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
	}

	@Override
	public void onDraw( Canvas canvas, Rect bounds )
	{
		super.onDraw( canvas, bounds );

		// draw background
		paint.setColor( previousColor );
		canvas.drawRect( bounds, paint );

		// render rect
		int w = bounds.width();
		int h = bounds.height();
		int rw = (int)(w * offset);
		int rh = (int)(h * offset);

		Rect r = new Rect();
		switch( direction )
		{
			case DIRECTION_RIGHT: r.set( 0, 0, rw, h ); break;
			case DIRECTION_DOWN: r.set( 0, 0, w, rh ); break;
			case DIRECTION_LEFT: r.set( w-rw, 0, w, h ); break;
			case DIRECTION_UP: r.set( 0, h-rh, w, h ); break;
		}

		paint.setColor( color );
		canvas.drawRect( r, paint );

		// update offset
		offset += 0.05f;
		if( offset >= 1.0f )
		{
			offset = 0.0f;
			direction = ( direction + 1 ) % DIRECTIONS;
			previousColor = color;
			color = generateRandomColor();
		}
	}
}
