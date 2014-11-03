package com.ra4king.opengl.util.buffers;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;

/**
 * @author Roi Atalla
 */
public class MappedBuffer extends GLBuffer {
	public MappedBuffer(int type, int size, boolean isStreaming) {
		super(type, size, isStreaming);
	}
	
	@Override
	public ByteBuffer bind(int offset, int size) {
		glBindBuffer(type, name);
		return glMapBufferRange(type, offset, size, GL_MAP_WRITE_BIT | GL_MAP_INVALIDATE_RANGE_BIT | GL_MAP_UNSYNCHRONIZED_BIT, null);
	}
	
	@Override
	public void unbind() {
		glUnmapBuffer(type);
		glBindBuffer(type, 0);
	}
}
