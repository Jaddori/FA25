package com.spacecat.fa25;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Nix on 2018-03-03.
 */

public class BackgroundSnake extends BaseBackground
{
	private class Node
	{
		public int score;
		public Node from;
		public Point location;

		public Node()
		{
			score = 0;
			from = null;
			location = new Point();
		}
	}

	private class Snake
	{
		public ArrayList<Point> parts;
		public ArrayDeque<Point> processingParts;
		public ArrayList<Point> path;
		public int currentPath;

		public Snake()
		{
			parts = new ArrayList<>();
			processingParts = new ArrayDeque<>();
			path = new ArrayList<>();
			currentPath = 0;
		}

		public void reset( Random random, int width, int height )
		{
			parts.clear();
			processingParts.clear();
			path.clear();
			currentPath = 0;

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
			/*Point movement = calculateMovement( apple );

			for( int i=partCount-1; i>0; i-- )
			{
				Point ahead = parts.get( i - 1 );
				Point current = parts.get( i );
				current.x = ahead.x;
				current.y = ahead.y;
			}

			Point head = parts.get( 0 );
			head.x += movement.x;
			head.y += movement.y;*/

			if( currentPath >= 0 && path.size() > 0 )
			{
				Point next = path.get( currentPath );
				currentPath--;

				for( int i=partCount-1; i>0; i-- )
				{
					Point ahead = parts.get( i - 1 );
					Point current = parts.get( i );
					current.set( ahead );
				}

				Point head = parts.get( 0 );
				head.set( next );
			}
			else
			{
				calculatePath( apple );
			}

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

		public void calculatePath( Point apple )
		{
			ArrayList<Node> closedSet = new ArrayList<>();
			ArrayList<Node> openSet = new ArrayList<>();

			Node startNode = new Node();
			startNode.location.set( parts.get( 0 ) );
			startNode.from = null;
			startNode.score = 0;
			openSet.add( startNode );

			for( int i=0; i<parts.size(); i++ )
			{
				Point part = parts.get( i );
				Node partNode = new Node();
				partNode.location.set( part );

				closedSet.add( partNode );
			}

			Node endNode = null;
			while( openSet.size() > 0 )
			{
				int lowestScore = 99999;
				Node nextNode = null;
				for( int i=0; i<openSet.size(); i++ )
				{
					Node curNode = openSet.get( i );
					if( curNode.score < lowestScore )
					{
						lowestScore = curNode.score;
						nextNode = curNode;
					}
				}

				if( nextNode.location.x == apple.x && nextNode.location.y == apple.y )
				{
					endNode = nextNode;
					break;
				}
				else
				{
					openSet.remove( nextNode );
					closedSet.add( nextNode );

					// check left
					Node leftNode = new Node();
					leftNode.location.set( nextNode.location.x - 1, nextNode.location.y );
					if( leftNode.location.x >= 0 )
					{
						addNode( apple, openSet, closedSet, leftNode, nextNode );
					}

					Node rightNode = new Node();
					rightNode.location.set( nextNode.location.x + 1, nextNode.location.y );
					if( rightNode.location.x < width )
					{
						addNode( apple, openSet, closedSet, rightNode, nextNode );
					}

					Node upNode = new Node();
					upNode.location.set( nextNode.location.x, nextNode.location.y - 1 );
					if( upNode.location.y >= 0 )
					{
						addNode( apple, openSet, closedSet, upNode, nextNode );
					}

					Node downNode = new Node();
					downNode.location.set( nextNode.location.x, nextNode.location.y + 1 );
					if( downNode.location.y < height )
					{
						addNode( apple, openSet, closedSet, downNode, nextNode );
					}
				}
			}

			if( endNode != null )
			{
				path.clear();

				while( endNode.from != null )
				{
					path.add( endNode.location );
					endNode = endNode.from;
				}

				currentPath = path.size() - 1;
			}
		}

		private void addNode( Point apple, ArrayList<Node> openSet, ArrayList<Node> closedSet, Node newNode, Node prevNode )
		{
			boolean alreadyExists = false;
			for( int i=0; i<closedSet.size() && !alreadyExists; i++ )
			{
				Node curNode = closedSet.get( i );
				if( newNode.location.x == curNode.location.x && newNode.location.y == curNode.location.y )
					alreadyExists = true;
			}

			if( !alreadyExists )
			{
				for( int i=0; i<openSet.size() && !alreadyExists; i++ )
				{
					Node curNode = openSet.get( i );
					if( newNode.location.x == curNode.location.x && newNode.location.y == curNode.location.y )
						alreadyExists = true;
				}
			}

			if( !alreadyExists )
			{
				newNode.score = prevNode.score + calculateDistance( newNode.location, apple );
				newNode.from = prevNode;
				openSet.add( newNode );
			}
		}

		private int calculateDistance( Point a, Point b )
		{
			Point dif = new Point( b.x - a.x, b.y - a.y );
			return (int)Math.sqrt( dif.x*dif.x + dif.y*dif.y );
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
		paint.setTypeface( Typeface.create( "Arial", Typeface.NORMAL ) );
		paint.setTextSize( 32.0f );

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

		// debug draw path
		/*paint.setColor( Color.RED );
		for( int i=0; i<snake.path.size(); i++ )
		{
			Point point = snake.path.get( i );
			Rect pathBounds = new Rect( point.x * tileWidth, point.y * tileHeight, (point.x+1)*tileWidth, (point.y+1)*tileHeight );

			paint.setColor( Color.argb( 255, 32 + i*32, 0, 0 ));
			canvas.drawRect( pathBounds, paint );
		}*/

		paint.setColor( Color.BLUE );
		for( int i=0; i<snake.path.size(); i++ )
		{
			Point point = snake.path.get( i );

			canvas.drawText( Integer.toString( i ), (point.x+0.5f) * tileWidth, (point.y+0.5f) * tileHeight, paint );
		}

		// draw apple
		paint.setColor( Color.GREEN );
		Rect appleBounds = new Rect( apple.x * tileWidth, apple.y * tileHeight, (apple.x+1)*tileWidth, (apple.y+1)*tileHeight );
		canvas.drawRect( appleBounds, paint );
	}
}
