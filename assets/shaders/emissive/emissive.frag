#version 330 core

out vec4 FragmentColour;

in vec4 Colour;
in vec2 TexCoords;

uniform sampler2D u_texture;
uniform float u_threshold = 0.7f;
uniform float u_intensityScale = 1f;

// All components are in the range [0…1], including hue.
vec3 rgb2hsv(vec3 c)
{
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

float getIntensity(vec3 col) {
    vec3 hsv = rgb2hsv(col);
    return hsv.b;
}

void main() {
    vec4 col = texture(u_texture, TexCoords);

    float intensity = getIntensity(col.rgb);

    if(intensity < u_threshold) {
        discard;
    }

    col.rgb *= u_intensityScale;

    FragmentColour = vec4(col);
}
