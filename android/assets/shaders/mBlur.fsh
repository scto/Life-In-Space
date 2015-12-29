#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform vec2 diff;//Difference between this frame and the last one.

const float blurSize = 1.0/100.0;

void main() {
    	vec4 texColor = vec4(0.0); // texture2D(u_texture, v_texCoords)
		//Adding samples from varying amounts in the direction forwards.
		texColor += texture2D(u_texture, v_texCoords + diff*5.0*blurSize)*0.02;
		texColor += texture2D(u_texture, v_texCoords + diff*4.0*blurSize)*0.05;
		texColor += texture2D(u_texture, v_texCoords + diff*3.0*blurSize)*0.09;
		texColor += texture2D(u_texture, v_texCoords + diff*2.0*blurSize)*0.12;
		texColor += texture2D(u_texture, v_texCoords + diff*blurSize)*0.15;
		texColor += texture2D(u_texture, v_texCoords)*0.16;//Taking sample from this actual fragment.
		//Adding samples from the direction backwards.
		texColor += texture2D(u_texture, v_texCoords - diff*5.0*blurSize)*0.02;
		texColor += texture2D(u_texture, v_texCoords - diff*4.0*blurSize)*0.05;
		texColor += texture2D(u_texture, v_texCoords - diff*3.0*blurSize)*0.09;
		texColor += texture2D(u_texture, v_texCoords - diff*2.0*blurSize)*0.12;
		texColor += texture2D(u_texture, v_texCoords - diff*blurSize)*0.15;

		gl_FragColor = texColor;
       
}