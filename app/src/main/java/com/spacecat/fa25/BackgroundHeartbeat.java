package com.spacecat.fa25;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by Tunder on 2018-03-03.
 */

public class BackgroundHeartbeat extends BaseBackground
{
	private Vec2[] nodes;
	private boolean hasNodes;
	private int lineColor;

	BackgroundHeartbeat( Resources resources )
	{
		super( resources );
	}

	@Override
	protected void initialize()
	{
		super.initialize();

		redrawDelay = 30;
		paint.setStrokeWidth( 4.0f );
		paint.setTextSize( 32.0f );
		lineColor = Color.argb( 255, 140, 250, 70 );

		nodes = new Vec2[11];
		hasNodes = false;
	}

	@Override
	public void reset()
	{
		super.reset();
	}

	@Override
	public void onDraw( Canvas canvas, Rect bounds )
	{
		super.onDraw( canvas, bounds );

		if( !hasNodes )
		{
			hasNodes = true;

			float w = bounds.width();
			float h = bounds.height();

			nodes[0] = new Vec2( 0.0f, h*0.65f );
			nodes[1] = new Vec2( w*0.1f, h*0.65f );
			nodes[2] = new Vec2( w * 0.15f, h*0.60f );
			nodes[3] = new Vec2( w*0.2f, h*0.7f );
			nodes[4] = new Vec2( w*0.3f, h*0.35f );
			nodes[5] = new Vec2( w*0.4f, h*0.85f );
			nodes[6] = new Vec2( w*0.5f, h*0.65f );
			nodes[7] = new Vec2( w*0.55f, h*0.7f );
			nodes[8] = new Vec2( w*0.6f, h*0.55f );
			nodes[9] = new Vec2( w*0.65f, h*0.65f );
			nodes[10] = new Vec2( w*1.0f, h*0.65f );
		}

		// draw background
		canvas.drawColor( Color.BLACK );

		// draw lines
		paint.setColor( lineColor );
		int lastIndex = 0;
		for( int i=1; i<nodes.length; i++, lastIndex++ )
		{
			Vec2 prev = nodes[i-1];
			Vec2 cur = nodes[i];
			canvas.drawLine( prev.x, prev.y, cur.x, cur.y, paint );
		}

		// draw mask
		int offset = elapsed % ( bounds.width() / 25 );
		offset *= 25;

		paint.setColor( Color.BLACK );
		canvas.drawRect( offset, 0, bounds.right, bounds.bottom, paint );
	}
}
