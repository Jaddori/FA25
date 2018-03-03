package com.spacecat.fa25;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Tunder on 2018-03-03.
 */

public class BackgroundRadar extends BaseBackground
{
	private class Dot
	{
		public Vec2 position;
		public int fadeElapsed;

		public Dot()
		{
			position = new Vec2();
			fadeElapsed = 0;
		}

		public Dot( Vec2 position )
		{
			this.position = new Vec2( position );
			fadeElapsed = 0;
		}
	}

	private final int DOTS_MIN_COUNT = 3;
	private final int DOTS_MAX_COUNT = 7;
	private final int FADE_TIME = 30;

	private Random random;
	private int rings;
	private int gridLines;
	private ArrayList<Dot> dots;
	private int backgroundColor;
	private int circleBackgroundColor;
	private int lineColor;

	BackgroundRadar( Resources resources )
	{
		super( resources );
	}

	@Override
	protected void initialize()
	{
		super.initialize();

		redrawDelay = 30;

		random = new Random();
		rings = 4;
		gridLines = 10;
		dots = new ArrayList<>();

		paint.setTextSize( 32.0f );

		backgroundColor = Color.argb( 255, 25, 40, 20 );
		circleBackgroundColor = Color.argb( 64, 130, 240, 60 );
		lineColor = Color.argb( 255, 130, 240, 60 );
	}

	@Override
	public void reset()
	{
		super.reset();

		dots.clear();
	}

	@Override
	public void onDraw( Canvas canvas, Rect bounds )
	{
		super.onDraw( canvas, bounds );

		// add dots, if we have none
		if( dots.size() <= 0 )
		{
			int dotCount = random.nextInt( DOTS_MAX_COUNT - DOTS_MIN_COUNT ) + DOTS_MIN_COUNT;
			for( int i = 0; i < dotCount; i++ )
			{
				Dot dot = new Dot();

				float centerX = bounds.width() * 0.5f;
				float topBorder = ( bounds.height() - bounds.width() ) * 0.5f;
				float centerY = topBorder + centerX;

				float cos = random.nextFloat() * 2.0f - 1.0f;
				float sin = random.nextFloat() * 2.0f - 1.0f;

				float len = random.nextFloat() * centerX;

				dot.position.set( centerX + cos*len, centerY + sin*len );

				dots.add( dot );
			}
		}

		// draw background
		paint.setColor( backgroundColor );
		canvas.drawColor( backgroundColor );

		// draw grid
		paint.setColor( lineColor );
		for( int i = 0; i < gridLines; i++ )
		{
			float hoffset = i * ( bounds.width() / gridLines );
			canvas.drawLine( hoffset, 0, hoffset, bounds.height(), paint );

			float voffset = i * ( bounds.height() / gridLines );
			canvas.drawLine( 0, voffset, bounds.width(), voffset, paint );
		}

		// draw circles
		float bigRadius = bounds.width() * 0.5f;
		float topBorder = ( bounds.height() - bounds.width() ) * 0.5f;

		paint.setColor( circleBackgroundColor );
		canvas.drawOval( 0.0f, topBorder, bounds.right, bounds.bottom - topBorder, paint );

		float chunkSize = bigRadius / rings;
		float originalStrokeWidth = paint.getStrokeWidth();
		paint.setStyle( Paint.Style.STROKE );
		paint.setColor( lineColor );
		paint.setStrokeWidth( 4.0f );
		for( int i=0; i<rings; i++ )
		{
			float radius = ( i * chunkSize );
			canvas.drawOval( radius, topBorder+radius, bounds.right-radius, bounds.bottom - topBorder - radius, paint );
		}
		paint.setStyle( Paint.Style.FILL );

		// draw pan line
		float prevAngle = (float)Math.toRadians( ( elapsed - 3 ) % 360 );
		float curAngle = (float)Math.toRadians( elapsed % 360 );

		float x = (float)Math.cos( curAngle );
		float y = (float)Math.sin( curAngle );

		float centerX = bigRadius;
		float centerY = topBorder + bigRadius;
		canvas.drawLine( centerX, centerY, centerX + ( x * bigRadius ), centerY + ( y * bigRadius ), paint );

		paint.setStrokeWidth( originalStrokeWidth );

		// update and draw dots
		Vec2 dir = new Vec2( x, y );
		for( int i=0; i<dots.size(); i++ )
		{
			// NOTE: Who knows what the fuck is going here? It sure aint proper dot products
			Dot dot = dots.get( i );
			Vec2 a = new Vec2( centerX, centerY );
			Vec2 b = new Vec2( dot.position );

			Vec2 dif = new Vec2( b.x - a.x, b.y - a.y );

			float angle = (float)Math.atan2( dif.y, dif.x );
			if( angle < 0.0f )
				angle += Math.PI * 2.0;

			if( angle > prevAngle && angle < curAngle )
			{
				dot.fadeElapsed = FADE_TIME;
			}

			if( dot.fadeElapsed > 0 )
			{
				dot.fadeElapsed--;
				paint.setAlpha( ( 255 / FADE_TIME ) * dot.fadeElapsed );

				canvas.drawOval( dot.position.x-16, dot.position.y-16, dot.position.x+16, dot.position.y+16, paint );
			}
		}
	}
}
