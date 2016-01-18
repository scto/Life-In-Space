#ifdef GL_ES
precision highp float;
#endif

varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform sampler2D u_mask;

uniform float time;
uniform float bright;
uniform float opacity;
uniform float sharp;
uniform float gFac;
uniform float wiggleFac;
uniform float wiggleMag;

void main(void) {
	float wiggle = cos(time+v_texCoords.y*wiggleFac)*wiggleMag*(1.0-(v_texCoords.y+0.1));
	gl_FragColor = texture2D(u_texture, vec2(v_texCoords.x+wiggle, v_texCoords.y));
	gl_FragColor.rgb += gl_FragColor.rgb*(bright-1.0)*(v_texCoords.y) + (bright-1.0)*0.5;
	
	float smoothing = gFac/(5.0*sharp);
    float distance = texture2D(u_mask,vec2(v_texCoords.x+wiggle, v_texCoords.y)).r;
    float alpha = smoothstep(gFac - smoothing, gFac + smoothing, distance);
    gl_FragColor.a *= alpha*opacity;
}