package com.spacecat.fa25;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Tunder on 2018-02-24.
 */

public class BackgroundView extends View implements View.OnClickListener
{
	private Random random;
	private Timer drawTimer;
	private boolean timerActive;
	private int currentBackground;
	private Rect bounds;

	private BaseBackground[] backgrounds;

	public BackgroundView( Context context )
	{
		super( context );
		initialize();
	}

	public BackgroundView( Context context, AttributeSet attrs )
	{
		super( context, attrs );
		initialize();
	}

	public void initialize()
	{
		random = new Random();
		currentBackground = 0;

		Resources r = getResources();
		backgrounds = new BaseBackground[]
				{
						new BackgroundPulse( r ),
						new BackgroundRotoblade( r ),
						new BackgroundRain( r ),
						new BackgroundHyperspace( r ),
						new BackgroundFireworks( r ),
						new BackgroundSnow( r ),
						new BackgroundBouncyBalls( r ),
						new BackgroundNodeNetwork( r ),
						new BackgroundSlides( r ),
						new BackgroundSnake( r ),
						new BackgroundRadar( r ),
						new BackgroundHeartbeat( r ),
				};

		timerActive = false;
	}

	public void updateBackground( int index )
	{
		currentBackground = index % backgrounds.length;

		if( timerActive )
		{
			drawTimer.cancel();
			drawTimer.purge();
			timerActive = false;
		}

		backgrounds[currentBackground].reset();
		if( backgrounds[currentBackground].getRedrawDelay() > 0 )
		{
			drawTimer = new Timer();
			drawTimer.scheduleAtFixedRate( new TimerTask(){public void run(){postInvalidate(); } },
				0,
				backgrounds[currentBackground].getRedrawDelay()
			);

			timerActive = true;
		}
		else
			postInvalidate();
	}

	@Override
	protected void onDraw( Canvas canvas )
	{
		super.onDraw( canvas );

		Rect bounds = new Rect( 0, 0, getWidth(), getHeight() );
		backgrounds[currentBackground].onDraw( canvas, bounds );
	}

	@Override
	public void onClick( View view )
	{
	}
}
