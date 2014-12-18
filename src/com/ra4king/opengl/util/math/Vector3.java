package com.ra4king.opengl.util.math;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import net.indiespot.struct.cp.CopyStruct;
import net.indiespot.struct.cp.Struct;
import net.indiespot.struct.cp.StructField;
import net.indiespot.struct.cp.StructType;
import net.indiespot.struct.cp.TakeStruct;

/**
 * @author Roi Atalla
 */
@StructType(sizeof = 12)
public class Vector3 {
	@StructField(offset = 0)
	private float x;
	
	@StructField(offset = 4)
	private float y;
	
	@StructField(offset = 8)
	private float z;
	
	public static final Vector3 ZERO = Struct.malloc(Vector3.class).set(0f);
	public static final Vector3 RIGHT = Struct.malloc(Vector3.class).set(1, 0, 0);
	public static final Vector3 LEFT = Struct.malloc(Vector3.class).set(-1, 0, 0);
	public static final Vector3 UP = Struct.malloc(Vector3.class).set(0, 1, 0);
	public static final Vector3 DOWN = Struct.malloc(Vector3.class).set(0, -1, 0);
	public static final Vector3 FORWARD = Struct.malloc(Vector3.class).set(0, 0, -1);
	public static final Vector3 BACK = Struct.malloc(Vector3.class).set(0, 0, 1);
	
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
	
	@TakeStruct
	public Vector3 x(float x) {
		this.x = x;
		return this;
	}
	
	public float y() {
		return y;
	}
	
	@TakeStruct
	public Vector3 y(float y) {
		this.y = y;
		return this;
	}
	
	public float z() {
		return z;
	}
	
	@TakeStruct
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
	
	@TakeStruct
	public Vector3 set(float f) {
		return set(f, f, f);
	}
	
	@TakeStruct
	public Vector3 set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	@TakeStruct
	public Vector3 set2(Vector2 vec) {
		return set(vec.x(), vec.y(), 0);
	}
	
	@TakeStruct
	public Vector3 set(Vector2 vec, float z) {
		return set(vec.x(), vec.y(), z);
	}
	
	@TakeStruct
	public Vector3 set(Vector3 vec) {
		return set(vec.x, vec.y, vec.z);
	}
	
	@TakeStruct
	public Vector3 set4(Vector4 vec) {
		return set(vec.x(), vec.y(), vec.z());
	}
	
	@TakeStruct
	public Vector3 reset() {
		x = y = z = 0;
		return this;
	}
	
	public float length() {
		return (float)Math.sqrt(lengthSquared());
	}
	
	public float lengthSquared() {
		return x * x + y * y + z * z;
	}
	
	@TakeStruct
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
	
	@CopyStruct
	public Vector3 cross(Vector3 vec) {
		return new Vector3(y * vec.z - vec.y * z, z * vec.x - vec.z * x, x * vec.y - vec.x * y);
	}
	
	@TakeStruct
	public Vector3 add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}
	
	@TakeStruct
	public Vector3 add(Vector3 vec) {
		return add(vec.x, vec.y, vec.z);
	}
	
	@TakeStruct
	public Vector3 sub(float x, float y, float z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}
	
	@TakeStruct
	public Vector3 sub(Vector3 vec) {
		return sub(vec.x, vec.y, vec.z);
	}
	
	@TakeStruct
	public Vector3 mult(float f) {
		return mult(f, f, f);
	}
	
	@TakeStruct
	public Vector3 mult(float x, float y, float z) {
		this.x *= x;
		this.y *= y;
		this.z *= z;
		return this;
	}
	
	@TakeStruct
	public Vector3 mult3(Vector3 vec) {
		return mult(vec.x, vec.y, vec.z);
	}
	
	@TakeStruct
	public Vector3 divide(float f) {
		return divide(f, f, f);
	}
	
	@TakeStruct
	public Vector3 divide(float x, float y, float z) {
		this.x /= x;
		this.y /= y;
		this.z /= z;
		return this;
	}
	
	@TakeStruct
	public Vector3 divide(Vector3 vec) {
		return divide(vec.x, vec.y, vec.z);
	}
	
	@TakeStruct
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
