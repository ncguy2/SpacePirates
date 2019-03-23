highp float rand(vec2 co) {
    highp float a = 12.9898;
    highp float b = 78.233;
    highp float c = 43758.5453;
    highp float dt= dot(co.xy ,vec2(a,b));
    highp float sn= mod(dt,3.14);
    return fract(sin(sn) * c);
}

int fastrand() {
    rngSeed = (214013 * rngSeed + 2531011);
    return (rngSeed >> 16) & 0x7FFF;
}

float f_fastrand() {
    int r = fastrand();
    return mod(r, scalar) * factor;
}

highp float seededRandom(float seed) {
    vec2 co;
    co.x = float(particleId) / float(u_particleCount);
    co.y = seed;
    return rand(co);
}

highp float random() {
    return seededRandom(f_fastrand());
}