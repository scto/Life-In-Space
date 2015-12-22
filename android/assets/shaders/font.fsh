#ifdef GL_ES
precision highp float;
#endif

uniform sampler2D u_texture;
uniform float alph;//MY alph.
uniform float scale;//Scale the font is being drawn at.

varying vec4 v_color;
varying vec2 v_texCoord;

void main() {
 	float smoothing = 0.5/(5.0*scale);
    float distance = texture2D(u_texture, v_texCoord).a;
    float alpha = smoothstep(0.5 - smoothing, 0.5 + smoothing, distance);
    gl_FragColor = vec4(v_color.rgb, alpha*alph);
}