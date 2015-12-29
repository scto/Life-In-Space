#ifdef GL_ES
precision highp float;
#endif

//"in" attributes from our vertex shader
varying vec4 vColor;
varying vec2 v_texCoords;


//our different texture units
uniform sampler2D u_texture; //default GL_TEXTURE0, expected by SpriteBatch
uniform sampler2D u_texture2;
uniform vec2 u_size;
uniform vec2 u_texSize;
uniform float fadeFactor;//Modifies how dark it fades.
uniform float fOn;//Whether or not to do fadeFactor. So this is a multi-purpose shader for the top(ft. no shadow) & side.
uniform float u_height;//Height of the sol. Used to make sampling actual textures easier by NOT squishing them and sampling them. It goes top-bottom because glsl's y-0 is the top.
uniform float u_texHeight;//Used for the same thing as u_height. The texture's height.
uniform vec4 baseCol;

const float reach = 1600.0;//How far down the shadow reaches.

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

	gl_FragColor = texColor0;
	gl_FragColor.g = gl_FragColor.g*0.5*(1.0-v_texCoords.y*fadeFactor)+gl_FragColor.g*0.5-0.03*fOn;
	gl_FragColor.b = gl_FragColor.b*0.2*(1.0-v_texCoords.y*fadeFactor)+gl_FragColor.b*0.8-0.02*fOn;
	gl_FragColor.r = gl_FragColor.r*0.15*(1.0-v_texCoords.y*fadeFactor)+gl_FragColor.r*0.85-0.05*fOn;

	//gl_FragColor = texColor1;
}