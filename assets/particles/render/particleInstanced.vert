#version 430

#define INDEX_BINDING_POINT 4

layout (location = 0) in vec3 a_position;

#define EXCLUDE_DEAD_BUFFER
#pragma include("/particles/includes/particle.glsl")

uniform mat4 u_projViewTrans;
uniform int u_particleCount;

out flat ParticleData datum;

out vec2 TexCoords;

void main() {

    vec2 coords = a_position.xy;

    TexCoords = coords * 0.5 + 0.5;

    ParticleData d = Data[Idx_Indices[gl_InstanceID]];

    if(!d.Alive || d.Colour.a < 0.2) {
        // Put vertex outside clip-space
        gl_Position = vec4(16.0);
        return;
    }

    float depth = float(gl_InstanceID) / float(u_particleCount);

    depth = -depth;

    datum = d;
    gl_PointSize = 4;

    vec2 scl = (d.Scale * abs(normalize(d.Velocity))) + d.Scale;

    vec2 worldPos = a_position.xy * scl;
    worldPos += d.Position;

    gl_Position = u_projViewTrans * vec4(worldPos, depth, 1.0);
}
