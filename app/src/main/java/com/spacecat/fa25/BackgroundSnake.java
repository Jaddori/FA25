package com.spacecat.fa25;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Nix on 2018-03-03.
 */

public class BackgroundSnake extends BaseBackground
{
	private class Snake
	{
		public ArrayList<Point> parts;
		public ArrayDeque<Point> processingParts;

		public Snake()
		{
			parts = new ArrayList<>();
			processingParts = new ArrayDeque<>();
		}

		public void reset( Random random, int width, int height )
		{
			parts.clear();
			processingParts.clear();

			Point head = new Point( random.nextInt( width ), random.nextInt( height )-1 );
			Point tail = new Point( head.x, head.y + 1 );

			parts.add( head );
			parts.add( tail );
		}

		public void updatePosition( Point apple, int width, int height )
		{
			int partCount = parts.size();

			// update processing of parts
			Point tail = parts.get( partCount - 1 );
			if( !processingParts.isEmpty() )
			{
				Point processedPart = processingParts.getFirst();
				if( processedPart.x == tail.x && processedPart.y == tail.y )
				{
					processingParts.removeFirst();
					parts.add( processedPart );
				}
			}

			// update movement of parts
			Point movement = calculateMovement( apple );

			for( int i=partCount-1; i>0; i-- )
			{
				Point ahead = parts.get( i - 1 );
				Point current = parts.get( i );
				current.x = ahead.x;
				current.y = ahead.y;
			}

			Point head = parts.get( 0 );
			head.x += movement.x;
			head.y += movement.y;
		}

		public Point calculateMovement( Point apple )
		{
			Point result = new Point();

			Point curPosition = parts.get( 0 );
			Point prevPosition = parts.get( 1 );

			int xdif = apple.x - curPosition.x;
			int ydif = apple.y - curPosition.y;

			int absxdif = Math.abs( xdif );
			int absydif = Math.abs( ydif );

			if( absxdif > absydif )
			{
				if( xdif > 0 )
				{
					if( prevPosition.x == curPosition.x+1 )
					{
						if( ydif > 0 )
							result.y = 1;
						else
							result.y = -1;
					}
					else
						result.x = 1;
				}
				else
				{
					if( prevPosition.x == curPosition.x-1 )
					{
						if( ydif > 0 )
							result.y = 1;
						else
							result.y = -1;
					}
					else
						result.x = -1;
				}
			}
			else
			{
				if( ydif > 0 )
				{
					if( prevPosition.y == curPosition.y+1 )
					{
						if( xdif > 0 )
							result.x = 1;
						else
							result.x = -1;
					}
					else
						result.y = 1;
				}
				else
				{
					if( prevPosition.y == curPosition.y-1 )
					{
						if( xdif > 0 )
							result.x = 1;
						else
							result.x = -1;
					}
					else
						result.y = -1;
				}
			}

			return result;
		}

		public void addPart()
		{
			Point head = parts.get( 0 );

			Point part = new Point( head.x, head.y );
			processingParts.addLast( part );
		}
	}

	private Random random;
	private Snake snake;
	private Point apple;
	private int width;
	private int height;

	BackgroundSnake( Resources resources )
	{
		super( resources );
	}

	@Override
	protected void initialize()
	{
		super.initialize();

		redrawDelay = 100;

		random = new Random();
		snake = new Snake();
		apple = new Point();
		width = 20;
		height = 20;
	}

	@Override
	public void reset()
	{
		super.reset();

		apple.x = random.nextInt( width );
		apple.y = random.nextInt( height );

		snake.reset( random, width, height );
	}

	@Override
	public void onDraw( Canvas canvas, Rect bounds )
	{
		super.onDraw( canvas, bounds );

		int tileWidth = bounds.width() / width;
		int tileHeight = bounds.height() / height;

		// draw background
		paint.setColor( Color.BLACK );
		canvas.drawRect( bounds, paint );

		// update and draw snake
		snake.updatePosition( apple, width, height );
		Point snakeHead = snake.parts.get( 0 );
		if( snakeHead.x == apple.x && snakeHead.y == apple.y )
		{
			apple.x = random.nextInt( width );
			apple.y = random.nextInt( height );

			snake.addPart();
		}

		paint.setColor( Color.WHITE );
		for( int i=0; i<snake.parts.size(); i++ )
		{
			Point part = snake.parts.get( i );
			Rect partBounds = new Rect( part.x * tileWidth, part.y * tileHeight, (part.x+1)*tileWidth, (part.y+1)*tileHeight );

			if( !snake.processingParts.isEmpty() )
			{
				Point processedPart = snake.processingParts.getFirst();
				if( part.x == processedPart.x && part.y == processedPart.y )
				{
					partBounds.set( partBounds.left - 5, partBounds.top - 5, partBounds.right + 5, partBounds.bottom + 5 );
				}
			}

			canvas.drawRect( partBounds, paint );
		}

		// draw apple
		paint.setColor( Color.GREEN );
		Rect appleBounds = new Rect( apple.x * tileWidth, apple.y * tileHeight, (apple.x+1)*tileWidth, (apple.y+1)*tileHeight );
		canvas.drawRect( appleBounds, paint );
	}
}
