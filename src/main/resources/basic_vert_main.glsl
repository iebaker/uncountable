void main() {
    Color = rn_VertexColor;
    gl_Position = rn_Projection * rn_View * rn_EntityModel * rn_PrimitiveModel * vec4(rn_VertexPosition, 1);
}