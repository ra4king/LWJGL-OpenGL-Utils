package com.ra4king.opengl.util.math;

import net.indiespot.struct.cp.TakeStruct;

/**
 * @author Roi Atalla
 */
public class MatrixStack {
	private Matrix4[] stack;
	private int currIdx;
	
	public MatrixStack() {
		stack = new Matrix4[10];
		stack[0] = new Matrix4();
		
		clear();
	}
	
	public MatrixStack clear() {
		stack[0].clearToIdentity();
		currIdx = 0;
		return this;
	}
	
	@TakeStruct
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
		
		stack[currIdx--] = null;
		return this;
	}
}
