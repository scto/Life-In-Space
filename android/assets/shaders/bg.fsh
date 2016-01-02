#version 100
#ifdef GL_ES
precision highp float;
#endif

varying vec2 v_texCoords;

//Shader uniforms
uniform sampler2D u_texture; //default GL_TEXTURE0, expected by SpriteBatch


//Colour-mapping
uniform vec4 colf;//final mix
uniform vec4 col4;
uniform vec4 col3;
uniform vec4 col2;
uniform vec4 col1;
uniform vec4 col0;
uniform vec4 tint;

uniform float s1;
uniform float s2;
uniform float s3;
uniform float s4;

uniform float alpha;
uniform float bright;
uniform float overlayAlpha;

#define BlendOverlayf(base, blend) 		(base < 0.5 ? (2.0 * base * blend) : (1.0 - 2.0 * (1.0 - base) * (1.0 - blend)))
#define Blend(base, blend, funcf) 		vec4(funcf(base.r, blend.r), funcf(base.g, blend.g), funcf(base.b, blend.b), 1.0)
#define BlendOverlay(base, blend) 		Blend(base, blend, BlendOverlayf)

void main(void) {
	//COLOUR-MAPPING
	vec4 init = texture2D(u_texture, v_texCoords);
	init.rgb *= bright;
	init.rgb += (bright-1.0)*0.5;
	vec4 fin = init;

	float grey = 0.299 * fin.r + 0.587 * fin.g + 0.114 * fin.b;

	if(grey <= s1){ fin = mix(col0, col1, grey/s1);}
	
	else if(grey <= s2){ fin = mix(col1, col2, (grey-s1)/(s2-s1));}
	
	else if(grey <= s3){ fin = mix(col2, col3, (grey-s2)/(s3-s2));}
	
	else if(grey <= s4){fin = mix(col3, col4, (grey-s3)/(s4-s3));}
	
	else { fin = mix(col4, colf, (grey-s4)/(1.00-s4));}
	
	fin = mix(fin, init, 1.0-alpha);

	gl_FragColor = fin;
	gl_FragColor.a = init.a;
	
	gl_FragColor = mix(gl_FragColor, BlendOverlay(gl_FragColor, tint), overlayAlpha)*bright;
}

