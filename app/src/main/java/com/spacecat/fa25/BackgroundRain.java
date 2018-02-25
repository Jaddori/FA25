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

public class BackgroundRain extends BaseBackground
{
	private class Drop
	{
		public int color;
		public Vec2 currentPosition;
		public Vec2 previousPosition;
		public Vec2 velocity;

		public Drop( int color )
		{
			this.color = color;
			currentPosition = new Vec2();
			previousPosition = new Vec2();
			velocity = new Vec2();
		}

		public void updatePosition( Vec2 force )
		{
			previousPosition.x = currentPosition.x;
			previousPosition.y = currentPosition.y;

			currentPosition.x += velocity.x;
			currentPosition.y += velocity.y;

			velocity.x += force.x;
			velocity.y += force.y;
		}
	}

	private ArrayList<Drop> drops;
	private Random random;
	private int frequency;
	private Vec2 gravity;
	private Vec2 wind;

	public BackgroundRain( Resources resources )
	{
		super( resources );
	}

	@Override
	protected void initialize()
	{
		super.initialize();

		drops = new ArrayList<Drop>();
		random = new Random();

		redrawDelay = 30;

		reset();
	}

	@Override
	public void reset()
	{
		frequency = random.nextInt(85) + 15;
		gravity = new Vec2( 0.0f, 9.82f );
		wind = new Vec2( (random.nextFloat()*2.0f)-1.0f, (random.nextFloat()*2.0f)-1.0f );
	}

	@Override
	public void onDraw( Canvas canvas, Rect bounds )
	{
		super.onDraw( canvas, bounds );

		// draw background
		paint.setColor( Color.BLACK );
		canvas.drawRect( bounds, paint );

		// check if we should remove drops
		boolean removed = false;
		do
		{
			removed = false;
			int removeIndex = -1;
			for( int i=0; i<drops.size(); i++ )
			{
				Vec2 pos = drops.get( i ).previousPosition;

				if( pos.x < bounds.left || pos.x > bounds.right || pos.y > bounds.bottom )
					removeIndex = i;
			}

			if( removeIndex >= 0 )
			{
				drops.remove( removeIndex );
				removed = true;
			}
		} while( removed );

		// check if we should spawn a new drop
		int value = random.nextInt( 100 );
		if( value < frequency )
			createDrop( bounds );

		// update and render drops
		for( int i=0; i<drops.size(); i++ )
		{
			Vec2 force = new Vec2( gravity.x + wind.x, gravity.y + wind.y );
			drops.get( i ).updatePosition( force );

			Vec2 beginning = drops.get( i ).previousPosition;
			Vec2 end = drops.get( i ).currentPosition;

			paint.setColor( drops.get( i ).color );
			paint.setStrokeWidth( 5.0f );
			canvas.drawLine( beginning.x, beginning.y, end.x, end.y, paint );
		}
	}

	private void createDrop( Rect bounds )
	{
		Drop drop = new Drop( resources.getColor( R.color.rain_acid ) );

		drop.currentPosition.x = random.nextInt( bounds.width() );
		drop.currentPosition.y = 0;

		drops.add( drop );
	}
}
