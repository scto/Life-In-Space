#ifdef GL_ES
precision highp float;
#endif

varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform sampler2D u_texture1;

uniform vec2 u_size;
uniform vec2 u_screensize;
uniform float xdis;
uniform float ydis;
uniform float time;
uniform float alpha;

#define BlendOverlayf(base, blend) 		(base < 0.5 ? (2.0 * base * blend) : (1.0 - 2.0 * (1.0 - base) * (1.0 - blend)))
#define Blend(base, blend, funcf) 		vec4(funcf(base.r, blend.r), funcf(base.g, blend.g), funcf(base.b, blend.b), 1.0)
#define BlendOverlay(base, blend) 		Blend(base, blend, BlendOverlayf)

void main(void) {
	vec4 col = texture2D(u_texture, v_texCoords);

	vec2 coord = vec2(v_texCoords.x*(u_size.x/u_screensize.x)-xdis+time, (1.0-v_texCoords.y)*(u_size.y/u_screensize.y)+ydis);
	vec4 hRefCol = texture2D(u_texture1, coord);

	gl_FragColor = mix(col, BlendOverlay(col, hRefCol), alpha);
	//gl_FragColor = mix(col, col*hRefCol, alpha);
	gl_FragColor.a = col.a;
	
}