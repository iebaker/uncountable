package xyz.izaak.uncountable.rendering;

import xyz.izaak.radon.Resource;
import xyz.izaak.radon.shading.ShaderVariableType;
import xyz.izaak.radon.shading.annotation.FragmentShaderBlock;
import xyz.izaak.radon.shading.annotation.ProvidesShaderComponents;
import xyz.izaak.radon.shading.annotation.VertexShaderBlock;
import xyz.izaak.radon.shading.annotation.VertexShaderOutput;

/**
 * Created by ibaker on 27/11/2016.
 */
@ProvidesShaderComponents
@VertexShaderOutput(type = ShaderVariableType.VEC3, identifier = "Color")
@SuppressWarnings("unused")
public class BasicShaderComponents {
    @VertexShaderBlock
    public static String getVertexShaderMain() {
        return Resource.stringFromFile("basic_vert_main.glsl");
    }

    @FragmentShaderBlock
    public static String getFragmentShaderMain() {
        return Resource.stringFromFile("basic_frag_main.glsl");
    }
}