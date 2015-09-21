/*
 * Copyright LWJGL. All rights reserved.
 * License terms: http://lwjgl.org/license.php
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.opengl;

/**
 * Native bindings to the <a href="http://www.opengl.org/registry/specs/ATI/meminfo.txt">ATI_meminfo</a> extension.
 * 
 * <p>Traditionally, OpenGL has treated resource management as a task of hardware virtualization hidden from applications. While providing great portability,
 * this shielding of information can prevent applications from making intelligent decisions on the management of resources they create. For instance, an
 * core may be better served by choosing a different rendering method if there is not sufficient resources to efficiently utilize its preferred
 * method.</p>
 * 
 * <p>Requires {@link GL11 OpenGL 1.1}</p>
 */
public final class ATIMeminfo {

	/** Accepted by the {@code param} parameter of GetIntegerv. */
	public static final int
		GL_VBO_FREE_MEMORY_ATI          = 0x87FB,
		GL_TEXTURE_FREE_MEMORY_ATI      = 0x87FC,
		GL_RENDERBUFFER_FREE_MEMORY_ATI = 0x87FD;

	private ATIMeminfo() {}

}