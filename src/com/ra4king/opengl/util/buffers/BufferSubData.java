package com.ra4king.opengl.util.buffers;

import static org.lwjgl.opengl.GL15.*;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

/**
 * @author Roi Atalla
 */
public class BufferSubData extends GLBuffer {
	private ByteBuffer buffer;
	private boolean orphan;
	
	private boolean isBound;
	
	public BufferSubData(int type, int size, boolean isStreaming, boolean orphan) {
		super(type, size, isStreaming);
		
		this.orphan = orphan;
		buffer = BufferUtils.createByteBuffer(size);
		isBound = false;
	}
	
	@Override
	public ByteBuffer bind(int offset, int size) {
		if(isBound)
			throw new IllegalStateException("Buffer is already bound.");
		
		isBound = true;
		
		glBindBuffer(type, name);
		
		buffer.limit(offset + size).position(offset);
		return buffer;
	}
	
	@Override
	public void unbind() {
		if(!isBound)
			throw new IllegalStateException("Buffer is not already bound.");
		
		isBound = false;
		
		if(!buffer.hasRemaining())
			return;
		
		buffer.flip();
		
		if(orphan)
			glBufferData(type, size, isStreaming ? GL_STREAM_DRAW : GL_STATIC_DRAW);
		
		glBufferSubData(type, buffer.position(), buffer);
		glBindBuffer(type, 0);
	}
}
