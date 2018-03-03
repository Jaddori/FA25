package com.spacecat.fa25;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
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
		public boolean alive;

		public Snake()
		{
			parts = new ArrayList<>();
			processingParts = new ArrayDeque<>();
			path = new ArrayList<>();
			currentPath = 0;
			alive = true;
		}

		public void reset( Random random, int width, int height )
		{
			parts.clear();
			processingParts.clear();
			path.clear();
			currentPath = 0;
			alive = true;

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
			/*if( currentPath >= 0 && path.size() > 0 )
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
			else*/
			if( currentPath < 0 || path.size() <= 0 )
			{
				calculatePath( apple );

				if( path.size() <= 0 )
				{
					Point head = new Point( parts.get( 0 ) );

					ArrayList<Point> validMovements = new ArrayList<>();

					if( !occupied( new Point( head.x-1, head.y ) ) )
						validMovements.add( new Point( head.x-1, head.y ) );

					if( !occupied( new Point( head.x+1, head.y ) ) )
						validMovements.add( new Point( head.x+1, head.y ) );

					if( !occupied( new Point( head.x, head.y-1 ) ) )
						validMovements.add( new Point( head.x, head.y-1 ) );

					if( !occupied( new Point( head.x, head.y+1 ) ) )
						validMovements.add( new Point( head.x, head.y+1 ) );

					if( validMovements.size() > 0 )
					{
						Point movement = validMovements.get( random.nextInt( validMovements.size() ) );
						path.add( movement );

						currentPath = 0;
					}
					else
					{
						alive = false;
					}
				}
			}

			Point head = parts.get( 0 );
			for( int i=1; i<parts.size(); i++ )
			{
				if( head.equals( parts.get( i ) ) )
				{
					alive = false;
				}
			}

			if( alive )
			{
				Point next = path.get( currentPath );
				currentPath--;

				for( int i=partCount-1; i>0; i-- )
				{
					Point ahead = parts.get( i - 1 );
					Point current = parts.get( i );
					current.set( ahead );
				}

				head = parts.get( 0 );
				head.set( next );
			}
		}

		private boolean occupied( Point point )
		{
			boolean result = false;

			if( point.x < 0 || point.x >= width || point.y < 0 || point.y >= height )
				result = true;

			for( int i=0; i<parts.size() && !result; i++ )
			{
				Point part = parts.get( i );
				if( part.x == point.x && part.y == point.y )
					result = true;
			}

			return result;
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
			path.clear();

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
				while( endNode.from != null )
				{
					path.add( endNode.location );
					endNode = endNode.from;
				}

				currentPath = path.size() - 1;
			}
			else
				currentPath = -1;
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
	private int flashElapsed;
	private int respawnDelay;

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
		width = 10;
		height = 10;
		flashElapsed = 0;
		respawnDelay = 40;
	}

	@Override
	public void reset()
	{
		super.reset();

		apple.x = random.nextInt( width );
		apple.y = random.nextInt( height );

		snake.reset( random, width, height );

		flashElapsed = 0;
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
		if( snake.alive )
		{
			snake.updatePosition( apple, width, height );

			Point snakeHead = snake.parts.get( 0 );
			if( snakeHead.x == apple.x && snakeHead.y == apple.y )
			{
				boolean occupied = true;
				while( occupied )
				{
					apple.x = random.nextInt( width );
					apple.y = random.nextInt( height );

					occupied = false;
					for( int i = 0; i < snake.parts.size() && !occupied; i++ )
					{
						Point part = snake.parts.get( i );
						if( part.x == apple.x && part.y == apple.y )
							occupied = true;
					}
				}

				snake.addPart();
			}
		}
		else
		{
			flashElapsed++;

			if( flashElapsed > respawnDelay )
			{
				reset();
				return;
			}
		}

		if( snake.alive || flashElapsed % 6 < 3 )
		{
			// draw head
			paint.setColor( Color.GRAY );
			Point head = snake.parts.get( 0 );
			Rect headBounds = new Rect( head.x * tileWidth, head.y * tileHeight, (head.x+1) * tileWidth, (head.y+1) * tileHeight );

			if( !snake.processingParts.isEmpty() && snake.processingParts.getLast().equals( head ) )
			{
				headBounds.left -= 16;
				headBounds.right += 16;
				headBounds.top -= 16;
				headBounds.bottom += 16;
			}

			canvas.drawRect( headBounds, paint );

			// draw parts
			paint.setColor( Color.WHITE );
			for( int i = 1; i < snake.parts.size(); i++ )
			{
				Point part = snake.parts.get( i );
				Rect partBounds = new Rect( part.x * tileWidth, part.y * tileHeight, (part.x + 1) * tileWidth, (part.y + 1) * tileHeight );

				boolean isProcessing = false;
				if( !snake.processingParts.isEmpty() )
				{
					Iterator<Point> it = snake.processingParts.iterator();
					while( !isProcessing && it.hasNext() )
					{
						Point processedPart = it.next();
						if( processedPart.x == part.x && processedPart.y == part.y )
						{
							isProcessing = true;
						}
					}
				}

				if( isProcessing )
				{
					partBounds.left -= 16;
					partBounds.right += 16;
					partBounds.top -= 16;
					partBounds.bottom += 16;
				}

				canvas.drawRect( partBounds, paint );
			}
		}

		// draw apple
		paint.setColor( Color.GREEN );
		Rect appleBounds = new Rect( apple.x * tileWidth, apple.y * tileHeight, (apple.x+1)*tileWidth, (apple.y+1)*tileHeight );
		canvas.drawRect( appleBounds, paint );
	}
}
