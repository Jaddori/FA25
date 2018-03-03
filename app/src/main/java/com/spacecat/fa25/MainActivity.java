package com.spacecat.fa25;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
	private Random random;
	private String[] reasons;

	private BackgroundView view_background;
	private TextView lbl_title;
	private TextView lbl_reason;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		view_background = (BackgroundView)findViewById( R.id.view_background );
		lbl_title = (TextView)findViewById( R.id.lbl_title );
		lbl_reason = (TextView)findViewById( R.id.lbl_reason );

		lbl_title.setVisibility( View.GONE );
		lbl_reason.setVisibility( View.GONE );

		view_background.setOnClickListener( this );

		random = new Random();

		generateReasons();
		updateText();
	}

	private void updateText()
	{
		int index = random.nextInt( reasons.length );

		view_background.updateBackground( index );
		lbl_title.setText( "Reason #" + Integer.toString( index ) );
		lbl_reason.setText( reasons[index] );
	}

	private void generateReasons()
	{
		reasons = new String[]
				{
						"A",
						"B",
						"C",
						"D",
						"E",
						"F",
						"G",
						"H",
						"I",
						"J",
						"K",
						"L",
						"M",
						"N",
						"O",
						"P",
						"Q",
						"R",
						"S",
						"T",
						"U",
						"V",
						"W",
				};
	}

	@Override
	public void onClick( View view )
	{
		updateText();
	}
}
