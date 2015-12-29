#ifdef GL_ES
precision highp float;
#endif

//"in" attributes from our vertex shader
varying vec4 vColor;
varying vec2 v_texCoords;


//our different texture units
uniform sampler2D u_texture; //default GL_TEXTURE0, expected by SpriteBatch
uniform sampler2D u_texture1;
uniform sampler2D u_texture2;
uniform vec2 u_size;
uniform vec2 u_texSize;
uniform vec2 u_screensize;
uniform vec4 baseCol;
uniform float xdis;//Displacement of shadow on x-axis. Based on player's pos.x-the sol's pos.x.
uniform float ydis;//displacement on y-axis.
uniform float alpha;//Determines alpha for the shadow on this.

#define BlendOverlayf(base, blend) 		(base < 0.5 ? (2.0 * base * blend) : (1.0 - 2.0 * (1.0 - base) * (1.0 - blend)))
#define Blend(base, blend, funcf) 		vec4(funcf(base.r, blend.r), funcf(base.g, blend.g), funcf(base.b, blend.b), 1.0)
#define BlendOverlay(base, blend) 		Blend(base, blend, BlendOverlayf)

void main(void) {
    //sample the colour from the first texture
    vec4 texColor0 = baseCol;

	//Sample from the third texture
	vec2 ctCoord = v_texCoords;
	ctCoord.x = ctCoord.x*(u_size.x/u_texSize.x);
	ctCoord.y = ctCoord.y*(u_size.y/u_texSize.y);
	vec4 colTex = texture2D(u_texture2, ctCoord);

	texColor0 = BlendOverlay(texColor0, colTex);

    //sample the colour from the second texture
	vec2 coord = vec2(v_texCoords.x*(u_size.x/u_screensize.x)-xdis, (1.0-v_texCoords.y)*(u_size.y/u_screensize.y)+ydis);
    vec4 texColor1 = texture2D(u_texture1, coord);
	
	float mixAlpha = texColor1.a*0.8*alpha;

    //Interpolate the colours based on decalColour's alpha.
	gl_FragColor = mix(texColor0, texColor1*texColor0, mixAlpha);
	gl_FragColor.a = texColor0.a;

	//gl_FragColor = texColor1;
}