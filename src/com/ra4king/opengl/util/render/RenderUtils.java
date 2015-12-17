package com.ra4king.opengl.util.render;

import org.lwjgl.opengl.APPLEVertexArrayObject;
import org.lwjgl.opengl.ARBDrawInstanced;
import org.lwjgl.opengl.ARBInstancedArrays;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GLContext;

import com.ra4king.opengl.util.math.Matrix4;
import com.ra4king.opengl.util.math.Vector3;
import com.ra4king.opengl.util.math.Vector4;

import net.indiespot.struct.cp.Struct;

/**
 * @author Roi Atalla
 */
public final class RenderUtils {
	public static final int GL_VERSION;
	public static final boolean IS_MAC;
	
	private static int queryObject;
	
	static final String SHADERS_PATH = "/com/ra4king/opengl/util/render/shaders/";
	
	static {
		GL_VERSION = GLContext.getCapabilities().OpenGL33 ? 33 : GLContext.getCapabilities().OpenGL32 ? 32 : GLContext.getCapabilities().OpenGL30 ? 30 : GLContext.getCapabilities().OpenGL21 ? 21 : 0;
		
		IS_MAC = System.getProperty("os.name").toLowerCase().contains("mac");
		
		queryObject = GL15.glGenQueries();
	}
	
	/**
	 * Dummy init method to force initialization of GLUtils singleton instance.
	 */
	public static void init() {}
	
	public static int getWidth() {
		return Display.getWidth();
	}
	
	public static int getHeight() {
		return Display.getHeight();
	}
	
	public static void glBindVertexArray(int vao) {
		if(GL_VERSION >= 30) {
			GL30.glBindVertexArray(vao);
		}
		else if(IS_MAC) {
			APPLEVertexArrayObject.glBindVertexArrayAPPLE(vao);
		}
		else if(GLContext.getCapabilities().GL_ARB_vertex_array_object) {
			ARBVertexArrayObject.glBindVertexArray(vao);
		}
		else {
			throw new UnsupportedOperationException("VAOs not supported on this system.");
		}
	}
	
	public static int glGenVertexArrays() {
		if(GL_VERSION >= 30) {
			return GL30.glGenVertexArrays();
		}
		else if(IS_MAC) {
			return APPLEVertexArrayObject.glGenVertexArraysAPPLE();
		}
		else if(GLContext.getCapabilities().GL_ARB_vertex_array_object) {
			return ARBVertexArrayObject.glGenVertexArrays();
		}
		else {
			throw new UnsupportedOperationException("VAOs not supported on this system.");
		}
	}
	
	public static void glDrawArraysInstanced(int mode, int first, int count, int primcount) {
		if(GL_VERSION >= 31) {
			GL31.glDrawArraysInstanced(mode, first, count, primcount);
		}
		else if(GLContext.getCapabilities().GL_ARB_draw_instanced) {
			ARBDrawInstanced.glDrawArraysInstancedARB(mode, first, count, primcount);
		}
		else {
			throw new UnsupportedOperationException("GL_ARB_draw_instanced not supported on this system.");
		}
	}
	
	public static void glDrawElementsInstanced(int mode, int indices_count, int type, long indices_buffer_offset, int primcount) {
		if(GL_VERSION >= 31) {
			GL31.glDrawElementsInstanced(mode, indices_count, type, indices_buffer_offset, primcount);
		}
		else if(GLContext.getCapabilities().GL_ARB_draw_instanced) {
			ARBDrawInstanced.glDrawElementsInstancedARB(mode, indices_count, type, indices_buffer_offset, primcount);
		}
		else {
			throw new UnsupportedOperationException("GL_ARB_draw_instanced not supported on this system.");
		}
	}
	
	public static void glVertexAttribDivisor(int index, int divisor) {
		if(GL_VERSION >= 33) {
			GL33.glVertexAttribDivisor(index, divisor);
		} else if(GLContext.getCapabilities().GL_ARB_instanced_arrays) {
			ARBInstancedArrays.glVertexAttribDivisorARB(index, divisor);
		} else {
			throw new UnsupportedOperationException("GL_ARB_instanced_arrays not supported on this system.");
		}
	}
	
	public static long getTimeStamp() {
		GL33.glQueryCounter(queryObject, GL33.GL_TIMESTAMP);
		return GL33.glGetQueryObjecti64(queryObject, GL15.GL_QUERY_RESULT);
	}
	
	public static class FrustumCulling {
		private enum Plane {
			LEFT, RIGHT, BOTTOM, TOP, NEAR, FAR;
			
			static Plane[] values = values();
		}
		
		private Vector4[] planes = Struct.mallocArray(Vector4.class, Plane.values.length);
		
		public float distanceFromPoint(Plane p, Vector3 point) {
			Vector4 plane = planes[p.ordinal()];
			return new Vector3().set4(plane).dot(point) + plane.w();
		}
		
		public void setupPlanes(Matrix4 matrix) {
			getPlane(matrix, 1, planes[Plane.LEFT.ordinal()]);
			getPlane(matrix, -1, planes[Plane.RIGHT.ordinal()]);
			getPlane(matrix, 2, planes[Plane.BOTTOM.ordinal()]);
			getPlane(matrix, -2, planes[Plane.TOP.ordinal()]);
			getPlane(matrix, 3, planes[Plane.NEAR.ordinal()]);
			getPlane(matrix, -3, planes[Plane.FAR.ordinal()]);
		}
		
		private void getPlane(Matrix4 matrix, int row, Vector4 plane) {
			int scale = row < 0 ? -1 : 1;
			row = Math.abs(row) - 1;
			
			plane.set(matrix.get(3) + scale * matrix.get(row),
			           matrix.get(7) + scale * matrix.get(row + 4),
			           matrix.get(11) + scale * matrix.get(row + 8),
			           matrix.get(15) + scale * matrix.get(row + 12)).normalize();
		}
		
		public boolean isCubeInsideFrustum(Vector3 center, float sideLength) {
			float half = sideLength * 0.5f;
			return isRectPrismInsideFrustum(new Vector3(center).sub(half, half, half), sideLength, sideLength, sideLength);
		}
		
		public boolean isRectPrismInsideFrustum(Vector3 corner, float width, float height, float depth) {
			for(Plane p : Plane.values) {
				if(distanceFromPoint(p, corner) >= 0)
					continue;
				if(distanceFromPoint(p, new Vector3(corner).add(width, 0, 0)) >= 0)
					continue;
				if(distanceFromPoint(p, new Vector3(corner).add(0, height, 0)) >= 0)
					continue;
				if(distanceFromPoint(p, new Vector3(corner).add(0, 0, depth)) >= 0)
					continue;
				if(distanceFromPoint(p, new Vector3(corner).add(width, height, 0)) >= 0)
					continue;
				if(distanceFromPoint(p, new Vector3(corner).add(width, 0, depth)) >= 0)
					continue;
				if(distanceFromPoint(p, new Vector3(corner).add(0, height, depth)) >= 0)
					continue;
				if(distanceFromPoint(p, new Vector3(corner).add(width, height, depth)) >= 0)
					continue;
				
				return false;
			}
			
			return true;
		}
		
		public boolean isPointInsideFrustum(Vector3 point) {
			boolean isIn = true;
			
			for(Plane p : Plane.values)
				isIn &= distanceFromPoint(p, point) >= 0;
			
			return isIn;
		}
	}
}
