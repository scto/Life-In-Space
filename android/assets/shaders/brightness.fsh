#ifdef GL_ES
precision highp float;
#endif

varying vec2 v_texCoords;

uniform sampler2D u_texture;

uniform float bright;
uniform float alpha = 1.0;

void main(void) {
	gl_FragColor = texture2D(u_texture, v_texCoords);
	gl_FragColor.rgb *= bright;
	gl_FragColor.a *= alpha;
	
}