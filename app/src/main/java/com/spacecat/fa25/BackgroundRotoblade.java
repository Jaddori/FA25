package com.spacecat.fa25;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import java.util.ArrayList;

/**
 * Created by Tunder on 2018-02-24.
 */

public class BackgroundRotoblade extends BaseBackground
{
	private Paint secondaryPaint;
	private int lineCount;
	private float speed;

	public BackgroundRotoblade( Resources resources )
	{
		super( resources );
	}

	@Override
	protected void initialize()
	{
		super.initialize();

		paint.setColor( resources.getColor( R.color.rotoblade_primary ) );
		secondaryPaint = new Paint();
		secondaryPaint.setColor( resources.getColor( R.color.rotoblade_secondary ) );

		redrawDelay = 30;
		lineCount = 12;
		speed = 2;
	}

	@Override
	public void onDraw( Canvas canvas, Rect bounds )
	{
		super.onDraw( canvas, bounds );

		Vec2 center = new Vec2();
		center.x = bounds.exactCenterX();
		center.y = bounds.exactCenterY();

		float startValue = (float)( elapsed % 360 );
		startValue = (float)Math.toRadians( startValue );

		ArrayList<Vec2> points = new ArrayList<>();
		for( int i=0; i<=360; i += 360 / lineCount )
		{
			float rad = (float)Math.toRadians( i ) + startValue * speed;

			Vec2 point = new Vec2();
			point.x = center.x + (float)Math.cos( rad ) * 1000.0f;
			point.y = center.y + (float)Math.sin( rad ) * 1000.0f;

			points.add( point );
		}

		for( int i=1; i<points.size(); i++ )
		{
			Vec2 a = points.get( i-1 );
			Vec2 b = points.get( i );

			Path path = new Path();
			path.moveTo( center.x, center.y );
			path.lineTo( a.x, a.y );
			path.lineTo( b.x, b.y );

			canvas.drawPath( path, ( i % 2 == 0 ? paint : secondaryPaint ));
		}
	}
}
