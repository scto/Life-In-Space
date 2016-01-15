#ifdef GL_ES
precision highp float;
#endif

varying vec2 v_texCoords;

uniform sampler2D u_texture;

uniform float bright;
uniform float alpha = 1.0;
uniform vec4 col;

#define BlendOverlayf(base, blend) 		(base < 0.5 ? (2.0 * base * blend) : (1.0 - 2.0 * (1.0 - base) * (1.0 - blend)))
#define Blend(base, blend, funcf) 		vec4(funcf(base.r, blend.r), funcf(base.g, blend.g), funcf(base.b, blend.b), base.a)
#define BlendOverlay(base, blend) 		Blend(base, blend, BlendOverlayf)

void main(void) {
	gl_FragColor = BlendOverlay(texture2D(u_texture, v_texCoords), col);
	gl_FragColor.rgb *= bright;
	gl_FragColor.a *= alpha;
	
}