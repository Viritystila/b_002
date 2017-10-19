// Doodle based on Sound Visualizer https://www.shadertoy.com/view/Xds3Rr
// and http://vimeo.com/51993089 @ the 0min 44s mark
// For Shadertone, tap into Overtone's volume...
uniform float iOvertoneVolume;
uniform vec2 resolution;
uniform float iA;
uniform float iB;
//uniform sampler2D iChannel0;
#define PI 3.1416

void main(void)
{
    float a  = (iA - 300.0)/50.0/2.0 + 0.5;
    float b = (iB - 300.0)/100.0/2.0 + 0.5;
    vec2 uv = a*3.0*(gl_FragCoord.xy/iResolution.xy) - 1.0;
    vec2 uvcam=b*(gl_FragCoord.xy/iResolution.xy);
    // equvalent to the video's spec.y, I thinkq
    float spec_y = 0.01 + 5.0*iOvertoneVolume;
    float col = 0.0;
    uv.x += sin(uv.x*1.5)*spec_y;
    col += abs(0.66/uv.x) * spec_y;
    vec4 c1 = texture2D(iCam3,uvcam);
    vec4 c2 = texture2D(iCam4, uvcam);
    vec4 ss= vec4(col, col, col, 21.0);
    c1.x=uv.x;
    vec4 c = mix(c1,ss,iA/iB); // alpha blend between two textures
    vec4 cm=mix(c, c2, 0.5);
    gl_FragColor = cm;
    //float offset = texture(iChannel0, 18.0).r * 0.5;


    //gl_FragColor=fract(texture2D(camera, uv) * 3.);
    //gl_FragColor.r *=.7;
}
