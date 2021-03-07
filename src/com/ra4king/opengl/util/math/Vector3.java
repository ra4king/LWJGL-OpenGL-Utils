package com.ra4king.opengl.util.math;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

/**
 * @author Roi Atalla
 */
public class Vector3 {
	private float x;
	private float y;
	private float z;

	public static final Vector3 ZERO = new Vector3(0f);
	public static final Vector3 RIGHT = new Vector3(1, 0, 0);
	public static final Vector3 LEFT = new Vector3(-1, 0, 0);
	public static final Vector3 UP = new Vector3(0, 1, 0);
	public static final Vector3 DOWN = new Vector3(0, -1, 0);
	public static final Vector3 FORWARD = new Vector3(0, 0, -1);
	public static final Vector3 BACK = new Vector3(0, 0, 1);

	public Vector3() {
		set(0, 0, 0);
	}

	public Vector3(float v) {
		set(v, v, v);
	}

	public Vector3(float x, float y, float z) {
		set(x, y, z);
	}

	public Vector3(Vector2 vec, float z) {
		set(vec, z);
	}

	public Vector3(Vector3 vec) {
		set(vec);
	}

	public float x() {
		return x;
	}

	public Vector3 x(float x) {
		this.x = x;
		return this;
	}

	public float y() {
		return y;
	}

	public Vector3 y(float y) {
		this.y = y;
		return this;
	}

	public float z() {
		return z;
	}

	public Vector3 z(float z) {
		this.z = z;
		return this;
	}

	public boolean equals(Vector3 v) {
		return x == v.x && y == v.y && z == v.z;
	}

	@Override
	public int hashCode() {
		return (int)(x * (2 << 4) + y * (2 << 2) + z);
	}

	public Vector3 set(float f) {
		return set(f, f, f);
	}

	public Vector3 set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public Vector3 set2(Vector2 vec) {
		return set(vec.x(), vec.y(), 0);
	}

	public Vector3 set(Vector2 vec, float z) {
		return set(vec.x(), vec.y(), z);
	}

	public Vector3 set(Vector3 vec) {
		set(vec.x, vec.y, vec.z);
		return this;
	}

	public Vector3 set4(Vector4 vec) {
		return set(vec.x(), vec.y(), vec.z());
	}

	public float length() {
		return (float)Math.sqrt(lengthSquared());
	}

	public float lengthSquared() {
		return x * x + y * y + z * z;
	}

	public Vector3 normalize() {
		float length = 1f / length();
		x *= length;
		y *= length;
		z *= length;
		return this;
	}

	public float dot(Vector3 vec) {
		return x * vec.x + y * vec.y + z * vec.z;
	}

	public Vector3 cross(Vector3 vec, Vector3 res) {
		return res.set(y * vec.z - vec.y * z, z * vec.x - vec.z * x, x * vec.y - vec.x * y);
	}

	public Vector3 add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	public Vector3 add(Vector3 vec) {
		return add(vec.x, vec.y, vec.z);
	}

	public Vector3 sub(float x, float y, float z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}

	public Vector3 sub(Vector3 vec) {
		return sub(vec.x, vec.y, vec.z);
	}

	public Vector3 mult(float f) {
		return mult(f, f, f);
	}

	public Vector3 mult(float x, float y, float z) {
		this.x *= x;
		this.y *= y;
		this.z *= z;
		return this;
	}

	public Vector3 mult(Vector3 vec) {
		return mult(vec.x, vec.y, vec.z);
	}

	public Vector3 divide(float f) {
		return divide(f, f, f);
	}

	public Vector3 divide(float x, float y, float z) {
		this.x /= x;
		this.y /= y;
		this.z /= z;
		return this;
	}

	public Vector3 divide(Vector3 vec) {
		return divide(vec.x, vec.y, vec.z);
	}

	public Vector3 mod(float f) {
		x %= f;
		y %= f;
		z %= f;

		return this;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}

	private final static FloatBuffer direct = BufferUtils.createFloatBuffer(3);

	public FloatBuffer toBuffer() {
		direct.clear();
		direct.put(x).put(y).put(z);
		direct.flip();
		return direct;
	}
}
