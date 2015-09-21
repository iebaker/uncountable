/*
 * Copyright LWJGL. All rights reserved.
 * License terms: http://lwjgl.org/license.php
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.opengl;

/**
 * Native bindings to the <a href="http://www.opengl.org/registry/specs/ARB/explicit_uniform_location.txt">ARB_explicit_uniform_location</a> extension.
 * 
 * <p>This extension provides a method to pre-assign uniform locations to uniform variables in the default uniform block, including subroutine uniforms. This
 * allows an core to modify the uniform values without requiring a GL query like {@link GL20#glGetUniformLocation GetUniformLocation}, {@link GL40#glGetSubroutineUniformLocation GetSubroutineUniformLocation} and
 * {@link GL40#glGetSubroutineIndex GetSubroutineIndex}.</p>
 * 
 * <p>Requires {@link GL33 OpenGL 3.3} or <a href="http://www.opengl.org/registry/specs/ARB/explicit_attrib_location.txt">ARB_explicit_attrib_location</a>. Promoted to core in {@link GL43 OpenGL 4.3}.</p>
 */
public final class ARBExplicitUniformLocation {

	/** Accepted by the {@code pname} parameter of GetBooleanv, GetIntegerv, GetFloatv, GetDoublev, and GetInteger64v. */
	public static final int GL_MAX_UNIFORM_LOCATIONS = 0x826E;

	private ARBExplicitUniformLocation() {}

}