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
@StructType
public class Matrix4 {
	public static final int LENGTH = 16;
	
	@StructField(length = LENGTH)
	private float[] matrix;
	
	public Matrix4() {
		clear();
	}
	
	public Matrix4(float[] m) {
		this();
		
		if(m.length < LENGTH)
			throw new IllegalArgumentException("float array must have at least " + LENGTH + " values.");
		
		set(m);
	}
	
	public Matrix4(Matrix4 m) {
		this();
		set(m);
	}
	
	@TakeStruct
	public Matrix4 clear() {
		for(int a = 0; a < LENGTH; a++)
			matrix[a] = 0;
		
		return this;
	}
	
	@TakeStruct
	public Matrix4 clearToIdentity() {
		return clear().put(0, 1)
				.put(5, 1)
				.put(10, 1)
				.put(15, 1);
	}
	
	@TakeStruct
	public Matrix4 clearToOrtho(float left, float right, float bottom, float top, float near, float far) {
		return clear().put(0, 2 / (right - left))
				.put(5, 2 / (top - bottom))
				.put(10, -2 / (far - near))
				.put(12, -(right + left) / (right - left))
				.put(13, -(top + bottom) / (top - bottom))
				.put(14, -(far + near) / (far - near))
				.put(15, 1);
	}
	
	@TakeStruct
	public Matrix4 clearToPerspective(float fovRad, float width, float height, float near, float far) {
		float fov = 1 / (float)Math.tan(fovRad / 2);
		return clear().put(0, fov * (height / width))
				.put(5, fov)
				.put(10, (far + near) / (near - far))
				.put(14, (2 * far * near) / (near - far))
				.put(11, -1);
	}
	
	@TakeStruct
	public Matrix4 clearToPerspectiveDeg(float fov, float width, float height, float near, float far) {
		return clearToPerspective((float)Math.toRadians(fov), width, height, near, far);
	}
	
	public float get(int index) {
		return matrix[index];
	}
	
	public float get(int col, int row) {
		return matrix[col * 4 + row];
	}
	
	@CopyStruct
	public Vector4 getColumn(int index) {
		return new Vector4(get(index, 0), get(index, 1), get(index, 2), get(index, 3));
	}
	
	@TakeStruct
	public Matrix4 put(int index, float f) {
		matrix[index] = f;
		return this;
	}
	
	@TakeStruct
	public Matrix4 put(int col, int row, float f) {
		matrix[col * 4 + row] = f;
		return this;
	}
	
	@TakeStruct
	public Matrix4 putColumn(int index, Vector4 v) {
		put(index, 0, v.x());
		put(index, 1, v.y());
		put(index, 2, v.z());
		put(index, 3, v.z());
		return this;
	}
	
	@TakeStruct
	public Matrix4 putColumn3(int index, Vector3 v) {
		put(index, 0, v.x());
		put(index, 1, v.y());
		put(index, 2, v.z());
		return this;
	}
	
	@TakeStruct
	public Matrix4 putColumn(int index, Vector3 v, float w) {
		put(index, 0, v.x());
		put(index, 1, v.y());
		put(index, 2, v.z());
		put(index, 3, w);
		return this;
	}
	
	@TakeStruct
	public Matrix4 set(float[] m) {
		if(m.length < LENGTH)
			throw new IllegalArgumentException("float array must have at least " + LENGTH + " values.");
		
		for(int a = 0; a < m.length && a < LENGTH; a++) {
			matrix[a] = m[a];
		}
		
		return this;
	}
	
	@TakeStruct
	public Matrix4 set(Matrix4 m) {
		Struct.copy(Matrix4.class, m, this);
		return this;
	}
	
	@TakeStruct
	public Matrix4 set3x3(Matrix3 m) {
		for(int a = 0; a < 3; a++) {
			put(a, 0, m.get(a, 0));
			put(a, 1, m.get(a, 1));
			put(a, 2, m.get(a, 2));
		}
		
		return this;
	}
	
	@TakeStruct
	public Matrix4 mult(float f) {
		for(int a = 0; a < LENGTH; a++)
			put(a, get(a) * f);
		
		return this;
	}
	
	@TakeStruct
	public Matrix4 mult(float[] m) {
		if(m.length < LENGTH)
			throw new IllegalArgumentException("float array must have at least " + LENGTH + " values.");
		
		return mult(new Matrix4(m));
	}
	
	@TakeStruct
	public Matrix4 mult(Matrix4 m) {
		Matrix4 temp = new Matrix4();
		
		for(int a = 0; a < 4; a++) {
			temp.put(a, 0, get(0) * m.get(a, 0) + get(4) * m.get(a, 1) + get(8) * m.get(a, 2) + get(12) * m.get(a, 3));
			temp.put(a, 1, get(1) * m.get(a, 0) + get(5) * m.get(a, 1) + get(9) * m.get(a, 2) + get(13) * m.get(a, 3));
			temp.put(a, 2, get(2) * m.get(a, 0) + get(6) * m.get(a, 1) + get(10) * m.get(a, 2) + get(14) * m.get(a, 3));
			temp.put(a, 3, get(3) * m.get(a, 0) + get(7) * m.get(a, 1) + get(11) * m.get(a, 2) + get(15) * m.get(a, 3));
		}
		
		return set(temp);
	}
	
	@CopyStruct
	public Vector3 mult3(Vector3 vec) {
		return mult(vec, 1);
	}
	
	@CopyStruct
	public Vector3 mult(Vector3 vec, float w) {
		return new Vector3(get(0) * vec.x() + get(4) * vec.y() + get(8) * vec.z() + get(12) * w,
				get(1) * vec.x() + get(5) * vec.y() + get(9) * vec.z() + get(13) * w,
				get(2) * vec.x() + get(6) * vec.y() + get(10) * vec.z() + get(14) * w);
	}
	
	@CopyStruct
	public Vector4 mult4(Vector4 vec) {
		return new Vector4(matrix[0] * vec.x() + matrix[4] * vec.y() + matrix[8] * vec.z() + matrix[12] * vec.w(),
				matrix[1] * vec.x() + matrix[5] * vec.y() + matrix[9] * vec.z() + matrix[13] * vec.w(),
				matrix[2] * vec.x() + matrix[6] * vec.y() + matrix[10] * vec.z() + matrix[14] * vec.w(),
				matrix[3] * vec.x() + matrix[7] * vec.y() + matrix[11] * vec.z() + matrix[15] * vec.w());
	}
	
	@TakeStruct
	public Matrix4 transpose() {
		float old = get(1);
		put(1, get(4));
		put(4, old);
		
		old = get(2);
		put(2, get(8));
		put(8, old);
		
		old = get(3);
		put(3, get(12));
		put(12, old);
		
		old = get(7);
		put(7, get(13));
		put(13, old);
		
		old = get(11);
		put(11, get(14));
		put(14, old);
		
		old = get(6);
		put(6, get(9));
		put(9, old);
		
		return this;
	}
	
	@TakeStruct
	public Matrix4 translate(float x, float y, float z) {
		Matrix4 temp = new Matrix4();
		
		temp.put(0, 1);
		temp.put(5, 1);
		temp.put(10, 1);
		temp.put(15, 1);
		
		temp.put(12, x);
		temp.put(13, y);
		temp.put(14, z);
		
		return mult(temp);
	}
	
	@TakeStruct
	public Matrix4 translate(Vector3 vec) {
		return translate(vec.x(), vec.y(), vec.z());
	}
	
	@TakeStruct
	public Matrix4 scale(float f) {
		return scale(f, f, f);
	}
	
	@TakeStruct
	public Matrix4 scale(float x, float y, float z) {
		Matrix4 temp = new Matrix4();
		
		temp.put(0, x);
		temp.put(5, y);
		temp.put(10, z);
		temp.put(15, 1);
		
		return mult(temp);
	}
	
	@TakeStruct
	public Matrix4 scale(Vector3 vec) {
		return scale(vec.x(), vec.y(), vec.z());
	}
	
	@TakeStruct
	public Matrix4 rotate(float angle, float x, float y, float z) {
		float cos = (float)Math.cos(angle);
		float sin = (float)Math.sin(angle);
		float oneMinusCos = 1 - cos;
		
		float len = (float)Math.sqrt(x * x + y * y + z * z);
		x /= len;
		y /= len;
		z /= len;
		
		Matrix4 temp = new Matrix4();
		
		temp.put(0, x * x * oneMinusCos + cos);
		temp.put(4, x * y * oneMinusCos - z * sin);
		temp.put(8, x * z * oneMinusCos + y * sin);
		
		temp.put(1, y * x * oneMinusCos + z * sin);
		temp.put(5, y * y * oneMinusCos + cos);
		temp.put(9, y * z * oneMinusCos - x * sin);
		
		temp.put(2, z * x * oneMinusCos - y * sin);
		temp.put(6, z * y * oneMinusCos + x * sin);
		temp.put(10, z * z * oneMinusCos + cos);
		
		temp.put(15, 1);
		
		return mult(temp);
	}
	
	@TakeStruct
	public Matrix4 rotate(float angle, Vector3 vec) {
		return rotate(angle, vec.x(), vec.y(), vec.z());
	}
	
	@TakeStruct
	public Matrix4 rotateDeg(float angle, float x, float y, float z) {
		return rotate((float)Math.toRadians(angle), x, y, z);
	}
	
	@TakeStruct
	public Matrix4 rotateDeg(float angle, Vector3 vec) {
		return rotate((float)Math.toRadians(angle), vec);
	}
	
	public float determinant() {
		float a = get(5) * get(10) * get(15) + get(9) * get(14) * get(7) + get(13) * get(6) * get(11) - get(7) * get(10) * get(13) - get(11) * get(14) * get(5) - get(15) * get(6) * get(9);
		float b = get(1) * get(10) * get(15) + get(9) * get(14) * get(3) + get(13) * get(2) * get(11) - get(3) * get(10) * get(13) - get(11) * get(14) * get(1) - get(15) * get(2) * get(9);
		float c = get(1) * get(6) * get(15) + get(5) * get(14) * get(3) + get(13) * get(2) * get(7) - get(3) * get(6) * get(13) - get(7) * get(14) * get(1) - get(15) * get(2) * get(5);
		float d = get(1) * get(6) * get(11) + get(5) * get(10) * get(3) + get(9) * get(2) * get(7) - get(3) * get(6) * get(9) - get(7) * get(10) * get(1) - get(11) * get(2) * get(5);
		
		return get(0) * a - get(4) * b + get(8) * c - get(12) * d;
	}
	
	@TakeStruct
	public Matrix4 inverse() {
		Matrix4 inv = new Matrix4();
		
		inv.put(0, +(get(5) * get(10) * get(15) + get(9) * get(14) * get(7) + get(13) * get(6) * get(11) - get(7) * get(10) * get(13) - get(11) * get(14) * get(5) - get(15) * get(6) * get(9)));
		inv.put(1, -(get(4) * get(10) * get(15) + get(8) * get(14) * get(7) + get(12) * get(6) * get(11) - get(7) * get(10) * get(12) - get(11) * get(14) * get(4) - get(15) * get(6) * get(8)));
		inv.put(2, +(get(4) * get(9) * get(15) + get(8) * get(13) * get(7) + get(12) * get(5) * get(11) - get(7) * get(9) * get(12) - get(11) * get(13) * get(4) - get(15) * get(5) * get(8)));
		inv.put(3, -(get(4) * get(9) * get(14) + get(8) * get(13) * get(6) + get(12) * get(5) * get(10) - get(6) * get(9) * get(12) - get(10) * get(13) * get(4) - get(14) * get(5) * get(8)));
		
		inv.put(4, -(get(1) * get(10) * get(15) + get(9) * get(14) * get(3) + get(13) * get(2) * get(11) - get(3) * get(10) * get(13) - get(11) * get(14) * get(1) - get(15) * get(2) * get(9)));
		inv.put(5, +(get(0) * get(10) * get(15) + get(8) * get(14) * get(3) + get(12) * get(2) * get(11) - get(3) * get(10) * get(12) - get(11) * get(14) * get(0) - get(15) * get(2) * get(8)));
		inv.put(6, -(get(0) * get(9) * get(15) + get(8) * get(13) * get(3) + get(12) * get(1) * get(11) - get(3) * get(9) * get(12) - get(11) * get(13) * get(0) - get(15) * get(1) * get(8)));
		inv.put(7, +(get(0) * get(9) * get(14) + get(8) * get(13) * get(2) + get(12) * get(1) * get(10) - get(2) * get(9) * get(12) - get(10) * get(13) * get(0) - get(14) * get(1) * get(8)));
		
		inv.put(8, +(get(1) * get(6) * get(15) + get(5) * get(14) * get(3) + get(13) * get(2) * get(7) - get(3) * get(6) * get(13) - get(7) * get(14) * get(1) - get(15) * get(2) * get(5)));
		inv.put(9, -(get(0) * get(6) * get(15) + get(4) * get(14) * get(3) + get(12) * get(2) * get(7) - get(3) * get(6) * get(12) - get(7) * get(14) * get(0) - get(15) * get(2) * get(4)));
		inv.put(10, +(get(0) * get(5) * get(15) + get(4) * get(13) * get(3) + get(12) * get(1) * get(7) - get(3) * get(5) * get(12) - get(7) * get(13) * get(0) - get(15) * get(1) * get(4)));
		inv.put(11, -(get(0) * get(5) * get(14) + get(4) * get(13) * get(2) + get(12) * get(1) * get(6) - get(2) * get(5) * get(12) - get(6) * get(13) * get(0) - get(14) * get(1) * get(4)));
		
		inv.put(12, -(get(1) * get(6) * get(11) + get(5) * get(10) * get(3) + get(9) * get(2) * get(7) - get(3) * get(6) * get(9) - get(7) * get(10) * get(1) - get(11) * get(2) * get(5)));
		inv.put(13, +(get(0) * get(6) * get(11) + get(4) * get(10) * get(3) + get(8) * get(2) * get(7) - get(3) * get(6) * get(8) - get(7) * get(10) * get(0) - get(11) * get(2) * get(4)));
		inv.put(14, -(get(0) * get(5) * get(11) + get(4) * get(9) * get(3) + get(8) * get(1) * get(7) - get(3) * get(5) * get(8) - get(7) * get(9) * get(0) - get(11) * get(1) * get(4)));
		inv.put(15, +(get(0) * get(5) * get(10) + get(4) * get(9) * get(2) + get(8) * get(1) * get(6) - get(2) * get(5) * get(8) - get(6) * get(9) * get(0) - get(10) * get(1) * get(4)));
		
		return set(inv.transpose().mult(1 / determinant()));
	}
	
	@CopyStruct
	public Quaternion toQuaternion() {
		float x = get(0) - get(5) - get(10);
		float y = get(5) - get(0) - get(10);
		float z = get(10) - get(0) - get(5);
		float w = get(0) + get(5) + get(10);
		
		int biggestIndex = 0;
		float biggest = w;
		
		if(x > biggest) {
			biggest = x;
			biggestIndex = 1;
		}
		
		if(y > biggest) {
			biggest = y;
			biggestIndex = 2;
		}
		
		if(z > biggest) {
			biggest = z;
			biggestIndex = 3;
		}
		
		float biggestVal = (float)(Math.sqrt(biggest + 1) * 0.5);
		float mult = 0.25f / biggestVal;
		
		Quaternion res = new Quaternion();
		
		switch(biggestIndex) {
			case 0:
				res.w(biggestVal);
				res.x((get(6) - get(9)) * mult);
				res.y((get(8) - get(2)) * mult);
				res.z((get(1) - get(4)) * mult);
				break;
			case 1:
				res.w((get(6) - get(9)) * mult);
				res.x(biggestVal);
				res.y((get(1) + get(4)) * mult);
				res.z((get(8) + get(2)) * mult);
				break;
			case 2:
				res.w((get(8) - get(2)) * mult);
				res.x((get(1) + get(4)) * mult);
				res.y(biggestVal);
				res.z((get(6) + get(9)) * mult);
				break;
			case 3:
				res.w((get(1) - get(4)) * mult);
				res.x((get(8) + get(2)) * mult);
				res.y((get(6) + get(9)) * mult);
				res.z(biggestVal);
				break;
		}
		
		return res;
	}
	
	private final static FloatBuffer direct = BufferUtils.createFloatBuffer(16);
	
	public FloatBuffer toBuffer() {
		direct.clear();
		for(int a = 0; a < LENGTH; a++) {
			direct.put(matrix[a]);
		}
		direct.flip();
		return direct;
	}
}
