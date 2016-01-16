#version 100
#ifdef GL_ES
precision highp float;
#endif

varying vec2 v_texCoords;

//Water uniforms
uniform vec2 iResolution;
uniform float iGlobalTime;
uniform float intensity;
const float emboss = 0.35;
const float delta = 200.0;

//Shader uniforms
uniform sampler2D u_texture; //default GL_TEXTURE0, expected by SpriteBatch
uniform sampler2D u_texture1;//USED FOR THE "sea" TEXTURE.

uniform vec2 u_size;
uniform vec2 cDis;

const float PI = 3.1415926535897932;

const float speed = 0.2;
const float speed_x = 0.01;
const float speed_y = 0.4;

const float reflectionCutOff = 0.0;
const float reflectionIntence = 20000.0;

const int steps = 8;
const float frequency = 3.8;
const int angle = 3;


//Colour-mapping
uniform vec4 colf;//final mix
uniform vec4 col4;
uniform vec4 col3;
uniform vec4 col2;
uniform vec4 col1;
uniform vec4 col0;

uniform float s1;
uniform float s2;
uniform float s3;
uniform float s4;

uniform float alpha;

//Function to get a float.
float col(vec2 coord)
  {
    float delta_theta = 2.0 * PI / float(angle);
    float col = 0.0;
    float theta = 0.0;
    for (int i = 0; i < steps; i++)
    {
      vec2 adjc = coord;
      theta = delta_theta*float(i);
      adjc.x += sin(theta)*iGlobalTime*speed + iGlobalTime * speed_x;
      adjc.y -= cos(theta)*iGlobalTime*speed - iGlobalTime * speed_y;
      col = col + cos( (adjc.x*sin(theta) - adjc.y*cos(theta))*frequency)*intensity;
    }

    return sin(col);
  }

void main(void) {
	//GETTING THE WATER
	vec2 p = v_texCoords*(iResolution/u_size), c1 = p, c2 = p;
	c1.x += iGlobalTime*0.1;
	float cc1 = col(c1);

	c2.x += iResolution.x/delta;
	float dx = emboss*(cc1-col(c2))/delta;

	c2.x = p.x;
	c2.y += iResolution.y/delta;
	float dy = emboss*(cc1-col(c2))/delta;

	c1.x += dx*2.;
	
	vec2 xx = v_texCoords*(iResolution/u_size)+(c1-v_texCoords*(iResolution/u_size))*0.5;
	xx.x += iGlobalTime*0.01;
	vec4 colTex = mix(texture2D(u_texture1, xx+cDis*0.3), texture2D(u_texture1, c1+cDis), cos(iGlobalTime)*0.1+0.7);
	
	//COLOUR-MAPPING
	vec4 init = colTex;
	vec4 fin = init;

	float grey = 0.299 * fin.r + 0.587 * fin.g + 0.114 * fin.b;

	if(grey <= s1){ fin = mix(col0, col1, grey/s1);}
	
	else if(grey <= s2){ fin = mix(col1, col2, (grey-s1)/(s2-s1));}
	
	else if(grey <= s3){ fin = mix(col2, col3, (grey-s2)/(s3-s2));}
	
	else if(grey <= s4){fin = mix(col3, col4, (grey-s3)/(s4-s3));}
	
	else { fin = mix(col4, colf, (grey-s4)/(1.00-s4));}
	
	fin = mix(fin, init, 1.0-alpha);

	gl_FragColor = fin;
}

