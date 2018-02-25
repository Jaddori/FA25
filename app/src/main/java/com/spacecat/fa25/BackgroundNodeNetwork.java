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

public class BackgroundNodeNetwork extends BaseBackground
{
	private class Node
	{
		public Vec2 position;
		public boolean visited;
		public boolean targeted;

		public Node()
		{
			position = new Vec2();
			visited = false;
			targeted = false;
		}

		public void reset( Random random, Rect bounds )
		{
			position.x = random.nextFloat() * bounds.width();
			position.y = random.nextFloat() * bounds.height();

			visited = false;
			targeted = false;
		}
	}

	private class Edge
	{
		public Node a, b;
		public int color;
	}

	private class Worm
	{
		private final float WORM_SPEED = 4.0f;

		public Vec2 position;
		public Node origin;
		public Node target;
		public boolean alive;
		public int color;

		public Worm()
		{
			position = new Vec2();
			alive = false;
		}

		public boolean updatePosition()
		{
			boolean result = false;

			if( alive )
			{
				Vec2 dir = new Vec2( target.position.x - position.x, target.position.y - position.y );
				float mag = (float)Math.sqrt( dir.x * dir.x + dir.y * dir.y );
				if( mag < WORM_SPEED )
				{
					position.x = target.position.x;
					position.y = target.position.y;

					target.visited = true;

					result = true;
				}
				else
				{
					dir.x /= mag;
					dir.y /= mag;

					position.x += dir.x * WORM_SPEED;
					position.y += dir.y * WORM_SPEED;
				}
			}

			return result;
		}

		public void calculateTarget( Node[] nodes )
		{
			float minDistance = 99999.0f;
			origin = target;
			target = null;
			for( int i = 0; i < nodes.length; i++ )
			{
				if( !nodes[i].visited && !nodes[i].targeted )
				{
					Vec2 dif = new Vec2( nodes[i].position.x - position.x, nodes[i].position.y - position.y );
					float distance = (float)Math.sqrt( dif.x * dif.x + dif.y * dif.y );

					if( distance < minDistance )
					{
						minDistance = distance;
						target = nodes[i];
					}
				}
			}

			if( target != null )
				target.targeted = true;
			else
				alive = false;
		}
	}

	private final int NODES_MIN = 100;
	private final int NODES_MAX = 150;
	private final int NODE_SIZE = 4;
	private final float WORM_MIN_SPEED = 2.0f;
	private final float WORM_MAX_SPEED = 10.0f;

	private Random random;
	private Node[] nodes;
	private Edge[] edges;
	private int edgeCount;
	private ArrayList<Worm> worms;
	private boolean validBounds;
	private int lineColor;
	private int[] possibleColors;

	public BackgroundNodeNetwork( Resources resources )
	{
		super( resources );
	}

	@Override
	protected void initialize()
	{
		super.initialize();

		random = new Random();
		worms = new ArrayList<>();

		lineColor = resources.getColor( R.color.rain_acid );

		possibleColors = new int[6];
		possibleColors[0] = resources.getColor( R.color.nodes_one );
		possibleColors[1] = resources.getColor( R.color.nodes_two );
		possibleColors[2] = resources.getColor( R.color.nodes_three );
		possibleColors[3] = resources.getColor( R.color.nodes_four );
		possibleColors[4] = resources.getColor( R.color.nodes_five );
		possibleColors[5] = resources.getColor( R.color.nodes_six );
	}

	@Override
	public void reset()
	{
		super.reset();

		redrawDelay = 30;

		int nodeCount = random.nextInt( NODES_MAX - NODES_MIN ) + NODES_MIN;
		nodes = new Node[nodeCount];
		for( int i = 0; i < nodes.length; i++ )
		{
			nodes[i] = new Node();
		}

		edges = new Edge[nodeCount - 1];
		for( int i = 0; i < edges.length; i++ )
		{
			edges[i] = new Edge();
		}
		edgeCount = 0;

		validBounds = false;

		worms.clear();
	}

	@Override
	public void onDraw( Canvas canvas, Rect bounds )
	{
		super.onDraw( canvas, bounds );

		boolean dormant = true;
		for( int i=0; i<nodes.length && dormant; i++ )
			if( !nodes[i].visited )
				dormant = false;

		if( dormant )
			reset();

		// draw background
		paint.setColor( Color.BLACK );
		canvas.drawRect( bounds, paint );

		if( !validBounds )
		{
			for( int i = 0; i < nodes.length; i++ )
				nodes[i].reset( random, bounds );

			validBounds = true;

			// set starting node
			int startIndex = random.nextInt( nodes.length );
			addWorm( nodes[startIndex] );
			nodes[startIndex].visited = true;
		}

		// update and draw worms
		for( int i=0; i<worms.size(); i++ )
		{
			Worm w = worms.get( i );
			if( w.updatePosition() )
			{
				if( edgeCount < edges.length-1 )
				{
					edges[edgeCount].a = w.origin;
					edges[edgeCount].b = w.target;
					edges[edgeCount].color = w.color;
					edgeCount++;

					w.calculateTarget( nodes );

					if( w.alive )
						addWorm( w.origin );
				}
			}

			paint.setColor( w.color );
			canvas.drawLine( w.origin.position.x, w.origin.position.y, w.position.x, w.position.y, paint );
		}

		// draw edges
		for( int i=0; i<edgeCount; i++ )
		{
			Edge e = edges[i];
			paint.setColor( e.color );
			canvas.drawLine( e.a.position.x, e.a.position.y, e.b.position.x, e.b.position.y, paint );
		}

		// draw nodes
		paint.setColor( Color.WHITE );
		for( int i=0; i<nodes.length; i++ )
		{
			Vec2 pos = nodes[i].position;
			canvas.drawOval( pos.x - NODE_SIZE, pos.y - NODE_SIZE, pos.x + NODE_SIZE, pos.y + NODE_SIZE, paint );
		}
	}

	private void addWorm( Node node )
	{
		Worm worm = new Worm();
		worm.position.x = node.position.x;
		worm.position.y = node.position.y;
		worm.alive = true;

		worm.calculateTarget( nodes );
		worm.origin = node;

		worm.color = possibleColors[random.nextInt(possibleColors.length)];

		worms.add( worm );
	}
}
