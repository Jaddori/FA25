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

public class BackgroundHyperspace extends BaseBackground
{
	private class Particle
	{
		private final float ACCELERATION = 2.0f;

		public Vec2 previousPosition;
		public Vec2 currentPosition;
		public Vec2 direction;
		public float speed;
		public boolean alive;

		public Particle()
		{
			previousPosition = new Vec2();
			currentPosition = new Vec2();
			direction = new Vec2();
			alive = false;
		}

		public void reset( Vec2 center )
		{
			float value = random.nextFloat() * 2.0f * (float)Math.PI;
			direction.x = (float)Math.cos( value );
			direction.y = (float)Math.sin( value );

			currentPosition.x = center.x + direction.x * 100.0f;
			currentPosition.y = center.y + direction.y * 100.0f;

			previousPosition.x = currentPosition.x;
			previousPosition.y = currentPosition.y;

			alive = true;
			speed = 0.0f;
		}

		public void updatePosition()
		{
			previousPosition.x = currentPosition.x;
			previousPosition.y = currentPosition.y;

			currentPosition.x += direction.x * speed;
			currentPosition.y += direction.y * speed;

			speed += ACCELERATION;
		}
	}

	private final int PARTICLE_COUNT = 100;

	private Particle[] particles;
	private Random random;
	private int frequency;

	public BackgroundHyperspace( Resources resources )
	{
		super( resources );
	}

	@Override
	protected void initialize()
	{
		super.initialize();

		redrawDelay = 30;
		random = new Random();

		particles = new Particle[PARTICLE_COUNT];
		for( int i=0; i<particles.length; i++ )
		{
			particles[i] = new Particle();
		}
	}

	@Override
	public void reset()
	{
		frequency = 5;
	}

	@Override
	public void onDraw( Canvas canvas, Rect bounds )
	{
		super.onDraw( canvas, bounds );

		// draw background
		paint.setColor( Color.BLACK );
		canvas.drawRect( bounds, paint );

		// kill particles
		for( int i=0; i<particles.length; i++ )
		{
			Particle p = particles[i];

			if( p.previousPosition.x < bounds.left || p.previousPosition.x > bounds.right ||
					p.previousPosition.y < bounds.top || p.previousPosition.y > bounds.bottom )
			{
				p.alive = false;
			}
		}

		// spawn particle
		int spawns = 0;
		for( int i = 0; i < particles.length && spawns < frequency; i++ )
		{
			Particle p = particles[i];
			if( !p.alive )
			{
				p.reset( new Vec2( bounds.exactCenterX(), bounds.exactCenterX() ) );
				spawns++;
			}
		}

		// update and render particles
		paint.setColor( Color.WHITE );
		for( int i=0; i<particles.length; i++ )
		{
			Particle p = particles[i];

			if( p.alive )
			{
				p.updatePosition();
				canvas.drawLine( p.previousPosition.x, p.previousPosition.y, p.currentPosition.x, p.currentPosition.y, paint );
			}
		}
	}
}
