#version 330 core

out vec4 FragmentColour;

in vec4 Colour;
in vec2 TexCoords;

uniform sampler2D u_texture;

void main() {
    vec4 col = texture(u_texture, TexCoords);
    FragmentColour = vec4(col);
}
