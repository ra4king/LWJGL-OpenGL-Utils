package com.ra4king.opengl.util.scene.binders;

import static org.lwjgl.opengl.GL20.*;

import com.ra4king.opengl.util.ShaderProgram;
import com.ra4king.opengl.util.math.Vector2;

import net.indiespot.struct.cp.CopyStruct;
import net.indiespot.struct.cp.Struct;

/**
 * @author Roi Atalla
 */
public class UniformVec2Binder extends UniformBinderBase {
	private Vector2 value = Struct.malloc(Vector2.class).set(0f);
	
	public UniformVec2Binder() {}
	
	public UniformVec2Binder(Vector2 vec) {
		setValue(vec);
	}
	
	public void setValue(Vector2 vec) {
		value.set(vec);
	}
	
	@CopyStruct
	public Vector2 getValue() {
		return value;
	}
	
	@Override
	public void bindState(ShaderProgram program) {
		glUniform2(getUniformLocation(program), value.toBuffer());
	}
	
	@Override
	public void unbindState(ShaderProgram program) {}
}
