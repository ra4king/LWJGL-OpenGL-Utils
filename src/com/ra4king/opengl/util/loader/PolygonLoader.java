package com.ra4king.opengl.util.loader;

import com.ra4king.opengl.util.math.Matrix4;
import com.ra4king.opengl.util.math.Vector2;
import com.ra4king.opengl.util.math.Vector3;
import com.ra4king.opengl.util.math.Vector4;

/**
 * @author Roi Atalla
 */
public class PolygonLoader {
	private PolygonLoader() {}
	
	public static float[] loadCube(float sideLength, Vector3 center, boolean interleaved, boolean vec4) {
		return loadCube(sideLength, center, interleaved, vec4, null);
	}
	
	public static float[] loadCube(float sideLength, Vector3 center, boolean interleaved, boolean vec4, Matrix4 modelMatrix) {
		return loadCube(new Vector3(sideLength), center, interleaved, vec4, modelMatrix);
	}
	
	public static float[] loadCube(Vector3 sideLength, Vector3 center, boolean interleaved, boolean vec4) {
		return loadCube(sideLength, center, interleaved, vec4, null);
	}
	
	public static float[] loadCube(Vector3 sideLength, Vector3 center, boolean interleaved, boolean vec4, Matrix4 modelMatrix) {
		float[] buffer = vec4 ? (interleaved ? cubeVec4interleaved : cubeVec4) : (interleaved ? cubeVec3interleaved : cubeVec3);
		
		for(int a = 0; a < cubeData.length / 2; a += 3) {
			int position = (a / 3) * ((interleaved ? 3 : 0) + (vec4 ? 4 : 3));
			
			Vector4 pos;
			if(modelMatrix == null)
				pos = new Vector4(center.x() + cubeData[a] * sideLength.x(), center.y() + cubeData[a + 1] * sideLength.y(), center.z() + cubeData[a + 2] * sideLength.z(), 1);
			else
				pos = modelMatrix.mult4(new Vector4(center.x() + cubeData[a] * sideLength.x(), center.y() + cubeData[a + 1] * sideLength.y(), center.z() + cubeData[a + 2] * sideLength.z(), 1));
			
			buffer[position + 0] = pos.x();
			buffer[position + 1] = pos.y();
			buffer[position + 2] = pos.z();
		}
		
		return buffer;
	}
	
	public static float[] loadUnitCube(boolean interleaved, boolean vec4) {
		if(interleaved)
			return vec4 ? cubeVec4interleaved : cubeVec3interleaved;
		else
			return vec4 ? cubeVec4 : cubeVec3;
	}
	
	public static float[] loadPlane(float sideLength, Vector3 center, boolean interleaved, boolean vec4) {
		return loadPlane(sideLength, center, interleaved, vec4, null);
	}
	
	public static float[] loadPlane(float sideLength, Vector3 center, boolean interleaved, boolean vec4, Matrix4 modelMatrix) {
		return loadPlane(new Vector2(sideLength), center, interleaved, vec4);
	}
	
	public static float[] loadPlane(Vector2 sideLength, Vector3 center, boolean interleaved, boolean vec4) {
		return loadPlane(sideLength, center, interleaved, vec4, null);
	}
	
	public static float[] loadPlane(Vector2 sideLength, Vector3 center, boolean interleaved, boolean vec4, Matrix4 modelMatrix) {
		float[] buffer = vec4 ? (interleaved ? planeVec4interleaved : planeVec4) : (interleaved ? planeVec3interleaved : planeVec3);
		
		for(int a = 0; a < planeData.length / 2; a += 3) {
			int position = (a / 3) * ((interleaved ? 3 : 0) + (vec4 ? 4 : 3));
			
			Vector4 pos;
			if(modelMatrix == null)
				pos = new Vector4(center.x() + cubeData[a] * sideLength.x(), center.y() + cubeData[a + 1] * sideLength.y(), center.z() + cubeData[a + 2], 1);
			else
				pos = modelMatrix.mult4(new Vector4(center.x() + cubeData[a] * sideLength.x(), center.y() + cubeData[a + 1] * sideLength.y(), center.z() + cubeData[a + 2], 1));
			
			buffer[position + 0] = pos.x();
			buffer[position + 1] = pos.y();
			buffer[position + 2] = pos.z();
		}
		
		return buffer;
	}
	
	private static float[] cubeData = {
			-0.5f, 0.5f, 0.5f,
			0.5f, 0.5f, 0.5f,
			0.5f, -0.5f, 0.5f,
			0.5f, -0.5f, 0.5f,
			-0.5f, -0.5f, 0.5f,
			-0.5f, 0.5f, 0.5f,
			
			0.5f, 0.5f, -0.5f,
			-0.5f, 0.5f, -0.5f,
			-0.5f, -0.5f, -0.5f,
			-0.5f, -0.5f, -0.5f,
			0.5f, -0.5f, -0.5f,
			0.5f, 0.5f, -0.5f,
			
			-0.5f, 0.5f, -0.5f,
			0.5f, 0.5f, -0.5f,
			0.5f, 0.5f, 0.5f,
			0.5f, 0.5f, 0.5f,
			-0.5f, 0.5f, 0.5f,
			-0.5f, 0.5f, -0.5f,
			
			-0.5f, -0.5f, 0.5f,
			0.5f, -0.5f, 0.5f,
			0.5f, -0.5f, -0.5f,
			0.5f, -0.5f, -0.5f,
			-0.5f, -0.5f, -0.5f,
			-0.5f, -0.5f, 0.5f,
			
			0.5f, 0.5f, 0.5f,
			0.5f, 0.5f, -0.5f,
			0.5f, -0.5f, -0.5f,
			0.5f, -0.5f, -0.5f,
			0.5f, -0.5f, 0.5f,
			0.5f, 0.5f, 0.5f,
			
			-0.5f, 0.5f, -0.5f,
			-0.5f, 0.5f, 0.5f,
			-0.5f, -0.5f, 0.5f,
			-0.5f, -0.5f, 0.5f,
			-0.5f, -0.5f, -0.5f,
			-0.5f, 0.5f, -0.5f,
			
			0, 0, 1,
			0, 0, 1,
			0, 0, 1,
			0, 0, 1,
			0, 0, 1,
			0, 0, 1,
			
			0, 0, -1,
			0, 0, -1,
			0, 0, -1,
			0, 0, -1,
			0, 0, -1,
			0, 0, -1,
			
			0, 1, 0,
			0, 1, 0,
			0, 1, 0,
			0, 1, 0,
			0, 1, 0,
			0, 1, 0,
			
			0, -1, 0,
			0, -1, 0,
			0, -1, 0,
			0, -1, 0,
			0, -1, 0,
			0, -1, 0,
			
			1, 0, 0,
			1, 0, 0,
			1, 0, 0,
			1, 0, 0,
			1, 0, 0,
			1, 0, 0,
			
			-1, 0, 0,
			-1, 0, 0,
			-1, 0, 0,
			-1, 0, 0,
			-1, 0, 0,
			-1, 0, 0,
	};
	
	private static final float[] cubeVec3 = new float[cubeData.length];
	private static final float[] cubeVec3interleaved = new float[cubeData.length];
	
	private static final float[] cubeVec4 = new float[cubeData.length + cubeData.length / 6];
	private static final float[] cubeVec4interleaved = new float[cubeData.length + cubeData.length / 6];
	
	static {
		for(int a = 0; a < cubeData.length / 2; a += 3) {
			System.arraycopy(cubeData, cubeData.length / 2 + a, cubeVec3interleaved, 3 + a * 2, 3);
			
			cubeVec4interleaved[4 + (a / 3) * 7 - 1] = 1;
			System.arraycopy(cubeData, cubeData.length / 2 + a, cubeVec4interleaved, 4 + (a / 3) * 7, 3);
		}
		
		System.arraycopy(cubeData, cubeData.length / 2, cubeVec3, cubeData.length / 2, cubeData.length / 2);
		
		System.arraycopy(cubeData, cubeData.length / 2, cubeVec4, 2 * cubeData.length / 3, cubeData.length / 2);
	}
	
	private static float[] planeData = {
			-0.5f, 0, -0.5f,
			0.5f, 0, -0.5f,
			0.5f, 0, 0.5f,
			0.5f, 0, 0.5f,
			-0.5f, 0, 0.5f,
			-0.5f, 0, -0.5f,
			
			-0.5f, 0, 0.5f,
			0.5f, 0, 0.5f,
			0.5f, 0, -0.5f,
			0.5f, 0, -0.5f,
			-0.5f, 0, -0.5f,
			-0.5f, 0, 0.5f,
			
			0, 1, 0,
			0, 1, 0,
			0, 1, 0,
			0, 1, 0,
			0, 1, 0,
			0, 1, 0,
			
			0, -1, 0,
			0, -1, 0,
			0, -1, 0,
			0, -1, 0,
			0, -1, 0,
			0, -1, 0,
	};
	
	private static final float[] planeVec3 = new float[planeData.length];
	private static final float[] planeVec3interleaved = new float[planeData.length];
	
	private static final float[] planeVec4 = new float[planeData.length + planeData.length / 6];
	private static final float[] planeVec4interleaved = new float[planeData.length + planeData.length / 6];
	
	static {
		for(int a = 0; a < planeData.length / 2; a += 3) {
			System.arraycopy(planeData, planeData.length / 2 + a, planeVec3interleaved, 3 + a * 2, 3);
			
			planeVec4interleaved[4 + (a / 3) * 7 - 1] = 1;
			System.arraycopy(planeData, planeData.length / 2 + a, planeVec4interleaved, 4 + (a / 3) * 7, 3);
		}
		
		System.arraycopy(planeData, planeData.length / 2, planeVec3, planeData.length / 2, planeData.length / 2);
		
		System.arraycopy(planeData, planeData.length / 2, planeVec4, 2 * planeData.length / 3, planeData.length / 2);
	}
}
