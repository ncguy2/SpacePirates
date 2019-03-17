#version 430

#define INDEX_BINDING_POINT 4

layout (location = 0) in vec3 a_position;
layout (location = 1) in vec2 a_texCoord0;

#define EXCLUDE_DEAD_BUFFER
#pragma include("/particles/includes/particle.glsl")

uniform mat4 u_projViewTrans;

out flat ParticleData datum;

out vec2 TexCoords;

void main() {
    TexCoords = a_texCoord0;

    ParticleData d = Data[Idx_Indices[gl_InstanceID]];

    if(!d.Alive) {
        // Put vertex outside clip-space
        gl_Position = vec4(2.0, 2.0, 2.0, 1.0);
        return;
    }

    datum = d;

    vec2 scl = (d.Scale * abs(normalize(d.Velocity))) + d.Scale;

    vec2 worldPos = a_position.xy * scl;
    worldPos += d.Position;

    gl_Position = u_projViewTrans * vec4(worldPos, 0.0, 1.0);
}
