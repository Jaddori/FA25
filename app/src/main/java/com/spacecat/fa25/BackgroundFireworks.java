package com.spacecat.fa25;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by Tunder on 2018-02-25.
 */

public class BackgroundFireworks extends BaseBackground
{
	private class Particle
	{
		private final int PARTICLE_MIN_LIFETIME = 85;
		private final int PARTICLE_MAX_LIFETIME = 150;

		public Vec2 previousPosition;
		public Vec2 currentPosition;
		public Vec2 velocity;
		public int lifetime;

		public Particle()
		{
			previousPosition = new Vec2();
			currentPosition = new Vec2();
			velocity = new Vec2();
			lifetime = 0;
		}

		public void reset( Random random, Vec2 position, Vec2 force )
		{
			currentPosition.x = position.x;
			currentPosition.y = position.y;

			previousPosition.x = currentPosition.x;
			previousPosition.y = currentPosition.y;

			velocity.x += force.x;
			velocity.y += force.y;

			lifetime = random.nextInt( PARTICLE_MAX_LIFETIME - PARTICLE_MIN_LIFETIME ) + PARTICLE_MIN_LIFETIME;
		}

		public void updatePosition( Vec2 force )
		{
			previousPosition.x = currentPosition.x;
			previousPosition.y = currentPosition.y;

			currentPosition.x += velocity.x;
			currentPosition.y += velocity.y;

			velocity.x *= 0.95f;
			velocity.y *= 0.95f;

			velocity.x += force.x;
			velocity.y += force.y * 0.35f;
		}
	}

	private class Firework
	{
		private final int FUSE_MIN = 35;
		private final int FUSE_MAX = 60;

		private final int[] FIREWORK_COLORS =
				{
						Color.RED,
						Color.BLUE,
						Color.YELLOW,
						Color.GREEN,
				};

		public int fuse;
		public int elapsed;
		public boolean exploded;
		public Vec2 previousPosition;
		public Vec2 currentPosition;
		public Vec2 velocity;
		public int color;
		public boolean alive;

		public Particle[] particles;

		public Firework()
		{
			previousPosition = new Vec2();
			currentPosition = new Vec2();
			velocity = new Vec2();

			particles = new Particle[30];
			for( int i=0; i<particles.length; i++ )
				particles[i] = new Particle();

			alive = false;
		}

		public void reset( Random random, int width, int height )
		{
			fuse = random.nextInt(FUSE_MAX-FUSE_MIN) + FUSE_MIN;
			elapsed = 0;
			exploded = false;

			currentPosition.x = (float)random.nextInt( width );
			currentPosition.y = height;

			velocity.x = random.nextFloat() * 20.0f - 10.0f;
			velocity.y = -50.0f;

			float chunk = ( 2.0f * (float)Math.PI ) / particles.length;
			for( int i=0; i<particles.length; i++ )
			{
				particles[i].velocity.x = (float)Math.cos( chunk * i ) * 10.0f;
				particles[i].velocity.y = (float)Math.sin( chunk * i ) * 10.0f;
			}

			color = FIREWORK_COLORS[random.nextInt( FIREWORK_COLORS.length )];
			alive = true;
		}

		public void update( Random random, Vec2 force )
		{
			if( !exploded )
			{
				previousPosition.x = currentPosition.x;
				previousPosition.y = currentPosition.y;

				currentPosition.x += velocity.x;
				currentPosition.y += velocity.y;

				velocity.x += force.x;
				velocity.y += force.y;

				if( elapsed > fuse )
				{
					exploded = true;

					for( int i=0; i<particles.length; i++ )
					{
						particles[i].reset( random, currentPosition, velocity );
					}
				}
			}
			else
			{
				int aliveParticles = 0;
				for( int i=0; i<particles.length; i++ )
				{
					if( particles[i].lifetime > elapsed )
					{
						particles[i].updatePosition( force );
						aliveParticles++;
					}
				}

				if( aliveParticles <= 0 )
					alive = false;
			}

			elapsed++;
		}
	}

	private Firework[] fireworks;
	private Random random;
	private Vec2 gravity;
	private int width;
	private int height;
	private int delay;
	private int lastSpawn;

	public BackgroundFireworks( Resources resources )
	{
		super( resources );
	}

	@Override
	protected void initialize()
	{
		super.initialize();

		redrawDelay = 30;

		random = new Random();

		fireworks = new Firework[10];
		for( int i=0; i<fireworks.length; i++ )
		{
			fireworks[i] = new Firework();
			fireworks[i].reset( random, 500, 1500 );
			fireworks[i].alive = false;
		}

		gravity = new Vec2( 0.0f, 0.982f );

		width = 500;
		height = 1500;
		delay = 15;
		lastSpawn = 0;
	}

	@Override
	public void reset()
	{
		for( int i=0; i<fireworks.length; i++ )
		{
			fireworks[i].reset( random, width, height );
			fireworks[i].alive = false;
		}
	}

	@Override
	public void onDraw( Canvas canvas, Rect bounds )
	{
		super.onDraw( canvas, bounds );

		width = bounds.width();
		height = bounds.height();

		// draw background
		paint.setColor( Color.BLACK );
		canvas.drawRect( bounds, paint );

		Vec2 force = new Vec2( gravity.x, gravity.y );

		for( int curFirework = 0; curFirework < fireworks.length; curFirework++ )
		{
			Firework firework = fireworks[curFirework];

			if( !firework.alive && ( elapsed - lastSpawn ) > delay )
			{
				firework.reset( random, width, height );
				lastSpawn = elapsed;
			}

			if( firework.alive )
			{
				firework.update( random, force );

				if( firework.currentPosition.x < bounds.left || firework.currentPosition.x > bounds.right )
				{
					firework.alive = false;
				}
				else
				{
					if( firework.exploded )
					{
						paint.setColor( firework.color );
						for( int i = 0; i < firework.particles.length; i++ )
						{
							Particle p = firework.particles[i];

							if( p.lifetime > firework.elapsed )
							{
								canvas.drawLine(
										p.previousPosition.x,
										p.previousPosition.y,
										p.currentPosition.x,
										p.currentPosition.y,
										paint
								);
							}
						}
					}
					else
					{
						paint.setColor( Color.LTGRAY );

						Vec2 prev = firework.previousPosition;
						Vec2 cur = firework.currentPosition;
						Vec2 dif = new Vec2( cur.x - prev.x, cur.y - prev.y );

						float len = (float)Math.sqrt( dif.x*dif.x + dif.y*dif.y );
						dif.x /= len;
						dif.y /= len;

						prev.x = cur.x + dif.x * 12.0f;
						prev.y = cur.y + dif.y * 12.0f;

						canvas.drawLine(
								firework.previousPosition.x,
								firework.previousPosition.y,
								firework.currentPosition.x,
								firework.currentPosition.y,
								paint
						);
					}
				}
			}
		}
	}
}
