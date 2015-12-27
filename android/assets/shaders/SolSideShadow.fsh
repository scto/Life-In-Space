#ifdef GL_ES
precision highp float;
#endif

//"in" attributes from our vertex shader
varying vec4 vColor;
varying vec2 v_texCoords;


//our different texture units
uniform sampler2D u_texture; //default GL_TEXTURE0, expected by SpriteBatch
uniform sampler2D u_texture1;
uniform vec2 u_size;//Size of this texture.
uniform vec2 u_screensize;
uniform vec4 baseCol;
uniform float fOn;//Whether or not to do fadeFactor. So this is a multi-purpose shader for the top(ft. no shadow) & side.
uniform float xdis;//Displacement of shadow on x-axis. Based on player's pos.x-the sol's pos.x.
uniform float ysamp;//The y where the shadow crosses the top/south edge.
uniform float alpha;//Determines alpha for the shadow on this.
uniform float fadeFactor;//Modifies how dark it fades.

const float reach = 1600.0;//How far down the shadow reaches.

void main(void) {
    //sample the colour from the first texture
    vec4 texColor0 = baseCol;

    //sample the colour from the second texture
	vec2 coord = vec2(v_texCoords.x*(u_size.x/u_screensize.x)-xdis, 1.0-ysamp);
    vec4 texColor1 = texture2D(u_texture1, coord);

	float mixAlpha = texColor1.a*0.9*alpha*(1.0-v_texCoords.y/(reach*(1.0-texColor1.r)/u_size.y));
	if(mixAlpha < 0.0){mixAlpha = 0.0;}
	
    //Interpolate the colours based on decalColour's alpha.
	gl_FragColor = mix(texColor0, texColor1*texColor0, mixAlpha);
	gl_FragColor.a = texColor0.a;
	gl_FragColor.g = gl_FragColor.g*0.5*(1.0-v_texCoords.y*fadeFactor)+gl_FragColor.g*0.5-0.1*fOn;
	gl_FragColor.b = gl_FragColor.b*0.2*(1.0-v_texCoords.y*fadeFactor)+gl_FragColor.b*0.8-0.05*fOn;
	gl_FragColor.r = gl_FragColor.r*0.15*(1.0-v_texCoords.y*fadeFactor)+gl_FragColor.r*0.85-0.05*fOn;

	//gl_FragColor = texColor1;
}