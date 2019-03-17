struct ParticleData {
    vec2 Position;
    vec2 Velocity;
    vec4 Colour;
    float Life;
    float MaxLife;
    vec2 Scale;
    vec2 TexCoords;
    bool Alive;
    float _Padding;
};

#ifndef INDEX_BINDING_POINT
#define INDEX_BINDING_POINT 2
#endif

#ifdef EXCLUDE_BUFFERS
#define EXCLUDE_PARTICLE_BUFFER
#define EXCLUDE_DEAD_BUFFER
#define EXCLUDE_INDEX_BUFFER
#endif

#ifndef EXCLUDE_PARTICLE_BUFFER
layout(std430, binding = 0) buffer Particles {
    ParticleData Data[];
};
#endif

#ifndef EXCLUDE_DEAD_BUFFER
layout(std430, binding = 1) buffer DeadList {
    uint Dead_CurrentIndex;
    uint Dead_Indices[];
};
#endif

#ifndef EXCLUDE_INDEX_BUFFER
layout (std430, binding = INDEX_BINDING_POINT) buffer Indices {
    uint Idx_Indices[];
};
#endif