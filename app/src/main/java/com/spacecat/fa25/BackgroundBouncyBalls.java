package com.spacecat.fa25;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Tunder on 2018-02-25.
 */

public class BackgroundBouncyBalls extends BaseBackground
{
	private class Ball
	{
		private final float BALL_MIN_SIZE = 64.0f;
		private final float BALL_MAX_SIZE = 128.0f;
		private final float BALL_MIN_RESTITUTION = 0.5f;
		private final float BALL_MAX_RESTITUTION = 0.95f;

		public Vec2 position;
		public Vec2 velocity;
		public float radius;
		public boolean resting;
		public float restitution;

		public Ball()
		{
			position = new Vec2();
			velocity = new Vec2();
			resting = false;
			restitution = 0.9f;
		}

		public void reset( Random random, int width )
		{
			radius = ( random.nextFloat() * ( BALL_MAX_SIZE - BALL_MIN_SIZE ) ) + BALL_MIN_SIZE;

			position.x = random.nextInt( width );
			position.y = radius*-2.0f;

			velocity.x = random.nextFloat() * 100.0f - 50.0f;

			resting = false;

			float rdif = BALL_MAX_RESTITUTION - BALL_MIN_RESTITUTION;
			restitution = random.nextFloat() * rdif + BALL_MIN_RESTITUTION;
		}

		public void updatePosition( Rect bounds )
		{
			if( !resting )
			{
				position.x += velocity.x;
				position.y += velocity.y;

				velocity.x *= 0.95f;
				velocity.y *= 0.95f;

				if( (position.x - radius < 0 && velocity.x < 0.0f) || (position.x + radius > bounds.width() && velocity.x > 0.0f) )
					velocity.x *= -restitution;

				if( position.y + radius > bounds.height() )
				{
					if( velocity.y > 0.0f )
					{
						position.y = bounds.height() - radius;

						if( velocity.y < 9.82f )
							resting = true;
						else
							velocity.y *= -restitution;
					}
				}

				velocity.y += 9.82f;
			}
		}
	}

	private class Collision
	{
		public Ball a;
		public Ball b;
		public float depth;
		public Vec2 point;

		public Collision()
		{
			point = new Vec2();
			depth = 0.0f;
		}
	}

	private final int BALLS_MIN = 5;
	private final int BALLS_MAX = 15;

	private Random random;
	private ArrayList<Ball> balls;
	private ArrayList<Collision> collisions;
	private int backgroundColor;
	private int ballColor;
	private int width;
	private int respawnDelay;
	private int respawnElapsed;

	public BackgroundBouncyBalls( Resources resources )
	{
		super( resources );
	}

	@Override
	protected void initialize()
	{
		super.initialize();

		redrawDelay = 30;

		random = new Random();
		balls = new ArrayList<>();
		collisions = new ArrayList<>();
		width = 1;

		respawnDelay = 50;
		respawnElapsed = 0;

		backgroundColor = resources.getColor( R.color.bouncyballs_background );
		ballColor = resources.getColor( R.color.bouncyballs_ball );
	}

	@Override
	public void reset()
	{
		balls.clear();

		int ballCount = random.nextInt( BALLS_MAX - BALLS_MIN ) + BALLS_MIN;
		for( int i=0; i<ballCount; i++ )
		{
			Ball ball = new Ball();
			ball.reset( random, width );

			balls.add( ball );
		}

		respawnElapsed = 0;
	}

	@Override
	public void onDraw( Canvas canvas, Rect bounds )
	{
		super.onDraw( canvas, bounds );

		width = bounds.width();

		// draw background
		paint.setColor( backgroundColor );
		canvas.drawRect( bounds, paint );

		// update and draw balls
		paint.setColor( ballColor );
		for( int i = 0; i < balls.size(); i++ )
		{
			Ball b = balls.get( i );

			b.updatePosition( bounds );

			//canvas.drawOval( b.position.x - b.radius, b.position.y - b.radius, b.position.x + b.radius, b.position.y + b.radius, paint );
		}

		// update collision between balls
		collisions.clear();
		for( int i=0; i<balls.size(); i++ )
		{
			for( int j=i; j<balls.size(); j++ )
			{
				if( i == j )
					continue;

				Ball a = balls.get( i );
				Ball b = balls.get( j );

				Vec2 dif = new Vec2( a.position.x - b.position.x, a.position.y - b.position.y );
				float distance = (float)Math.sqrt( dif.x * dif.x + dif.y * dif.y );

				float depth = ( a.radius + b.radius ) - distance;
				if( depth > 0.0f )
				{
					Collision collision = new Collision();
					collision.a = a;
					collision.b = b;
					collision.depth = depth;

					Vec2 point = new Vec2( dif.x / distance, dif.y / distance );
					point.x = a.position.x + point.x * ( a.radius - depth * 0.5f );
					point.y = a.position.y + point.y * ( a.radius - depth * 0.5f );
					collision.point = point;

					collisions.add( collision );
				}
			}
		}

		for( int i=0; i<collisions.size(); i++ )
		{
			Collision c = collisions.get( i );

			Vec2 adir = new Vec2( c.a.position.x - c.point.x, c.a.position.y - c.point.y );
			float amag = (float)Math.sqrt( adir.x*adir.x + adir.y*adir.y );
			adir.x /= amag;
			adir.y /= amag;

			c.a.resting = false;

			c.a.velocity.x -= adir.x * c.depth;
			c.a.velocity.y -= adir.y * c.depth;

			c.a.position.x -= adir.x * c.depth;
			c.a.position.y -= adir.y * c.depth;

			Vec2 bdir = new Vec2( c.b.position.x - c.point.x, c.b.position.y - c.point.y );
			float bmag = (float)Math.sqrt( bdir.x*bdir.x + bdir.y*bdir.y );
			bdir.x /= bmag;
			bdir.y /= bmag;

			c.b.resting = false;

			c.b.velocity.x += bdir.x * c.depth;
			c.b.velocity.y += bdir.y * c.depth;

			c.b.position.x += bdir.x * c.depth;
			c.b.position.y += bdir.y * c.depth;
		}

		// draw balls
		for( int i=0; i<balls.size(); i++ )
		{
			Ball b = balls.get( i );
			canvas.drawOval( b.position.x - b.radius, b.position.y - b.radius, b.position.x + b.radius, b.position.y + b.radius, paint );
		}

		// check if we should respawn the balls
		boolean shouldRespawn = true;
		for( int i=0; i<balls.size() && shouldRespawn; i++ )
		{
			if( !balls.get( i ).resting )
				shouldRespawn = false;
		}

		if( shouldRespawn )
		{
			respawnElapsed++;

			if( respawnElapsed > respawnDelay )
				reset();
		}
	}
}
