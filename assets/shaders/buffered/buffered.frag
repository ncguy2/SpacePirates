#version 330 core

layout (location = 0) out vec4 Diffuse;
layout (location = 1) out vec4 Normal;
layout (location = 2) out vec4 Emissive;
layout (location = 3) out vec4 Metadata;

in vec4 Colour;
in vec2 TexCoords;

uniform sampler2D u_texture;

void main() {
    vec4 col = texture(u_texture, TexCoords);
    Diffuse = vec4(col);

    Normal = vec4(0.0);
    Emissive = vec4(0.0);
    Metadata = vec4(0.0);

    Metadata.rg = TexCoords;
    Metadata.b = 1.0;
}
