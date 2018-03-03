package com.spacecat.fa25;

/**
 * Created by Tunder on 2018-02-25.
 */

public class Vec2
{
	public float x;
	public float y;

	public Vec2()
	{
		x = 0.0f;
		y = 0.0f;
	}

	public Vec2( float x, float y )
	{
		this.x = x;
		this.y = y;
	}

	public Vec2( Vec2 ref )
	{
		x = ref.x;
		y = ref.y;
	}

	public void set( float x, float y )
	{
		this.x = x;
		this.y = y;
	}

	public void set( Vec2 ref )
	{
		x = ref.x;
		y = ref.y;
	}
}