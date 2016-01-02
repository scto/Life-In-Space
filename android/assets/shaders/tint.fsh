#ifdef GL_ES
precision highp float;
#endif

varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec4 tint;

uniform float bright;
uniform float alpha;
uniform float overlayAlpha;

#define BlendOverlayf(base, blend) 		(base < 0.5 ? (2.0 * base * blend) : (1.0 - 2.0 * (1.0 - base) * (1.0 - blend)))
#define Blend(base, blend, funcf) 		vec4(funcf(base.r, blend.r), funcf(base.g, blend.g), funcf(base.b, blend.b), 1.0)
#define BlendOverlay(base, blend) 		Blend(base, blend, BlendOverlayf)

void main(void) {
	gl_FragColor = texture2D(u_texture, v_texCoords);
	gl_FragColor.rgb *= bright;
	gl_FragColor.a *= alpha;
	gl_FragColor = mix(gl_FragColor, BlendOverlay(gl_FragColor, tint), overlayAlpha);
	
}