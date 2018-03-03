package com.spacecat.fa25;

/**
 * Created by Nix on 2018-03-03.
 */

public class Point
{
	public int x;
	public int y;

	public Point()
	{
		x = 0;
		y = 0;
	}

	public Point( int x, int y )
	{
		this.x = x;
		this.y = y;
	}

	public Point( Point ref )
	{
		x = ref.x;
		y = ref.y;
	}

	public void set( int x, int y )
	{
		this.x = x;
		this.y = y;
	}

	public void set( Point ref )
	{
		x = ref.x;
		y = ref.y;
	}

	public boolean equals( Point ref )
	{
		return ( x == ref.x && y == ref.y );
	}
}
