package com.ra4king.opengl.util.math;

import net.indiespot.struct.cp.Struct;
import net.indiespot.struct.cp.TakeStruct;

/**
 * @author Roi Atalla
 */
public class MatrixStack {
	private Matrix4[] stack;
	private int currIdx;
	
	public MatrixStack() {
		stack = Struct.malloc(Matrix4.class, 10);
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
			Matrix4[] temp = Struct.malloc(Matrix4.class, stack.length << 1);
			for(int a = 0; a < currIdx; a++)
				temp[a].set(stack[a]);
			
			Struct.free(stack);
			stack = temp;
		}
		
		Struct.copy(Matrix4.class, stack[currIdx - 1], stack[currIdx]);
		
		return this;
	}
	
	public MatrixStack popMatrix() {
		if(currIdx == 0)
			throw new IllegalStateException("Already at the topmost matrix.");
		
		currIdx--;
		return this;
	}
}
