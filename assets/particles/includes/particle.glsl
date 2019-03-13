struct ParticleData {
    vec2 Position;
    vec2 Velocity;
    vec4 Colour;
    float Life;
    float MaxLife;
    vec2 Size;
    vec2 TexCoords;
    vec2 _Padding;
};

#ifndef EXCLUDE_PARTICLE_BUFFER
layout(std430, binding = 0) buffer Particles {
    ParticleData Data[];
};
#endif

#ifndef EXCLUDE_DEAD_BUFFER
layout(std430, binding = 1) buffer DeadList {
    uint CurrentIndex;
    uint Indices[];
};
#endif