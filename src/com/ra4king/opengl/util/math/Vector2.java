package com.ra4king.opengl.util.math;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

/**
 * @author Roi Atalla
 */
public class Vector2 {
	private float x;
	private float y;

	public static final Vector2 RIGHT = new Vector2(1, 0);
	public static final Vector2 LEFT = new Vector2(-1, 0);
	public static final Vector2 UP = new Vector2(0, 1);
	public static final Vector2 DOWN = new Vector2(0, -1);

	public Vector2() {
		this(0, 0);
	}

	public Vector2(float v) {
		this(v, v);
	}

	public Vector2(float x, float y) {
		set(x, y);
	}

	public Vector2(Vector2 vec) {
		set(vec);
	}

	public float x() {
		return x;
	}

	public Vector2 x(float x) {
		this.x = x;
		return this;
	}

	public float y() {
		return y;
	}

	public Vector2 y(float y) {
		this.y = y;
		return this;
	}

	public boolean equals(Vector2 v) {
		return x == v.x && y == v.y;
	}

	public Vector2 set(float f) {
		return set(f, f);
	}

	public Vector2 set(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public Vector2 set(Vector2 vec) {
		set(vec.x, vec.y);
		return this;
	}

	public Vector2 set3(Vector3 vec) {
		return set(vec.x(), vec.y());
	}

	public Vector2 set4(Vector4 vec) {
		return set(vec.x(), vec.y());
	}

	public float length() {
		return (float)Math.sqrt(lengthSquared());
	}

	public float lengthSquared() {
		return x * x + y * y;
	}

	public Vector2 normalize() {
		float length = 1f / length();
		x *= length;
		y *= length;
		return this;
	}

	public float dot(Vector2 vec) {
		return x * vec.x + y * vec.y;
	}

	public Vector2 add(float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public Vector2 add(Vector2 vec) {
		return add(vec.x, vec.y);
	}

	public Vector2 sub(float x, float y) {
		this.x -= x;
		this.y -= y;
		return this;
	}

	public Vector2 sub(Vector2 vec) {
		return sub(vec.x, vec.y);
	}

	public Vector2 mult(float f) {
		return mult(f, f);
	}

	public Vector2 mult(float x, float y) {
		this.x *= x;
		this.y *= y;
		return this;
	}

	public Vector2 mult(Vector2 vec) {
		return mult(vec.x, vec.y);
	}

	public Vector2 divide(float f) {
		return divide(f, f);
	}

	public Vector2 divide(float x, float y) {
		this.x /= x;
		this.y /= y;
		return this;
	}

	public Vector2 divide(Vector2 vec) {
		return divide(vec.x, vec.y);
	}

	public Vector2 mod(float f) {
		x %= f;
		y %= f;

		return this;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	private final static FloatBuffer direct = BufferUtils.createFloatBuffer(2);

	public FloatBuffer toBuffer() {
		direct.clear();
		direct.put(x).put(y);
		direct.flip();
		return direct;
	}
}
