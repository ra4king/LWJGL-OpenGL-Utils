package com.ra4king.opengl.util.math;

/**
 * @author Roi Atalla
 */
public class MatrixStack {
	private Matrix4[] stack;
	private int currIdx;
	
	public MatrixStack() {
		stack = new Matrix4[10];
		clear();
	}
	
	public MatrixStack clear() {
		stack[0] = new Matrix4();
		currIdx = 0;
		return this;
	}
	
	public Matrix4 getTop() {
		return stack[currIdx];
	}
	
	public MatrixStack setTop(Matrix4 m) {
		stack[currIdx].set(m);
		return this;
	}
	
	public MatrixStack pushMatrix() {
		if(++currIdx == stack.length) {
			Matrix4[] temp = new Matrix4[stack.length << 1];
			System.arraycopy(stack, 0, temp, 0, stack.length);
			stack = temp;
		}
		
		stack[currIdx] = new Matrix4(stack[currIdx - 1]);
		
		return this;
	}
	
	public MatrixStack popMatrix() {
		if(currIdx == 0)
			throw new IllegalStateException("Already at the topmost matrix.");
		
		currIdx--;
		return this;
	}
}
