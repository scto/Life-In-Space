#ifdef GL_ES
precision highp float;
#endif

//"in" attributes from our vertex shader
varying vec4 vColor;
varying vec2 v_texCoords;


//our different texture units
uniform sampler2D u_texture; //default GL_TEXTURE0, expected by SpriteBatch
uniform sampler2D u_texture1;
uniform vec2 u_size;
uniform vec2 u_screensize;
uniform vec4 baseCol;
uniform float xdis;//Displacement of shadow on x-axis. Based on player's pos.x-the sol's pos.x.
uniform float ydis;//displacement on y-axis.
uniform float alpha;//Determines alpha for the shadow on this.

void main(void) {
    //sample the colour from the first texture
    vec4 texColor0 = baseCol;

    //sample the colour from the second texture
	vec2 coord = vec2(v_texCoords.x*(u_size.x/u_screensize.x)-xdis, (1.0-v_texCoords.y)*(u_size.y/u_screensize.y)+ydis);
    vec4 texColor1 = texture2D(u_texture1, coord);
	
	float mixAlpha = texColor1.a*0.8*alpha;

    //Interpolate the colours based on decalColour's alpha.
	gl_FragColor = mix(texColor0, texColor1*texColor0, mixAlpha);
	gl_FragColor.a = texColor0.a;

	//gl_FragColor = texColor1;
}