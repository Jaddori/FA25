package com.spacecat.fa25;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Tunder on 2018-02-25.
 */

public class BackgroundSnow extends BaseBackground
{
	private class Particle
	{
		public Vec2 currentPosition;
		public int elapsed;
		public float cycle;
		public boolean alive;

		public Particle()
		{
			currentPosition = new Vec2();
			elapsed = 0;
			alive = false;
		}

		public void reset( Random random, int width )
		{
			currentPosition.x = random.nextInt( width );
			currentPosition.y = -8;

			elapsed = random.nextInt();
			cycle = random.nextFloat() * 0.03f + 0.02f;

			alive = true;
		}

		public void updatePosition( Vec2 force )
		{
			elapsed++;
			Vec2 finalForce = new Vec2( force.x, force.y );

			float sinValue = (float)Math.sin( (double)elapsed * cycle );
			finalForce.x += sinValue;

			currentPosition.x += finalForce.x;
			currentPosition.y += finalForce.y;
		}
	}

	private Random random;
	private Vec2 gravity;
	private Particle[] particles;
	private int width;
	private float particleSize;
	private int delay;
	private boolean whiteSnow;
	private int snowLevel;

	public BackgroundSnow( Resources resources )
	{
		super( resources );
	}

	@Override
	protected void initialize()
	{
		super.initialize();

		redrawDelay = 30;

		random = new Random();
		gravity = new Vec2( 0.0f, 9.82f );
		width = 1;
		particleSize = 10.0f;
		delay = 25;
		whiteSnow = true;
		snowLevel = 0;

		particles = new Particle[20];
		for( int i=0; i<particles.length; i++ )
			particles[i] = new Particle();

		paint.setStrokeWidth( 10 );
	}

	@Override
	public void reset()
	{
		for( int i=0; i<particles.length; i++ )
		{
			particles[i].reset( random, width );
			particles[i].alive = false;
		}

		whiteSnow = random.nextInt() % 2 == 0;
		snowLevel = 0;

		gravity.y = ( random.nextFloat() + 0.05f ) * 9.82f;
	}

	@Override
	public void onDraw( Canvas canvas, Rect bounds )
	{
		super.onDraw( canvas, bounds );

		width = bounds.width();
		int height = bounds.height();

		// draw background
		paint.setColor( ( whiteSnow ? Color.BLACK : Color.WHITE ) );
		canvas.drawRect( bounds, paint );

		// spawn new particles
		if( elapsed % delay == 0 )
		{
			for( int i=0; i<particles.length; i++ )
			{
				if( !particles[i].alive )
				{
					particles[i].reset( random, width );
					break;
				}
			}
		}

		// update and draw snow particles
		paint.setColor( ( whiteSnow ? Color.WHITE : Color.BLACK ) );
		for( int i=0; i<particles.length; i++ )
		{
			Particle p = particles[i];
			if( p.alive )
			{
				p.updatePosition( gravity );

				// reset the particle if it is outside the screen
				if( p.currentPosition.y > height )
				{
					p.reset( random, width );
					snowLevel++;
				}

				canvas.drawOval( p.currentPosition.x, p.currentPosition.y, p.currentPosition.x + particleSize, p.currentPosition.y + particleSize, paint );
			}
		}

		// update snow level
		paint.setColor( ( whiteSnow ? Color.WHITE : Color.BLACK ) );
		int top = height - snowLevel;
		if( top < 0 )
			top = 0;
		Rect snowBounds = new Rect( 0, top, width, height );

		canvas.drawRect( snowBounds, paint );

		if( snowLevel >= height )
		{
			whiteSnow = !whiteSnow;
			snowLevel = 0;

			for( int i=0; i<particles.length; i++ )
			{
				particles[i].reset( random, width );
				particles[i].alive = false;
			}
		}
	}
}
