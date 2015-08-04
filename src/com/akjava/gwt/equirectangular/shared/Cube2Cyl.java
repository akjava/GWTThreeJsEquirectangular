package com.akjava.gwt.equirectangular.shared;


/*
 * original code is https://github.com/madwyn/libcube2cyl/blob/master/src/Cube2Cyl.hpp
 */

// original license

/*  Cube2Cyl v1.0.2 - 2012-05-29
*
*  Cube2Cyl is a cubic projection to cylindrical projection conversion lib.
*
*  Homepage: http://www.wenyanan.com/cube2cyl/
*  Please check the web page for further information, upgrades and bug fixes.
*
*  Copyright (c) 2012-2014 Yanan Wen (WenYananWork@gmail.com)
*
*  Permission is hereby granted, free of charge, to any person obtaining a copy
*  of this software and associated documentation files (the "Software"), to deal
*  in the Software without restriction, including without limitation the rights
*  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
*  copies of the Software, and to permit persons to whom the Software is
*  furnished to do so, subject to the following conditions:
*
*  The above copyright notice and this permission notice shall be included in
*  all copies or substantial portions of the Software.
*
*  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
*  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
*  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
*  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
*  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
*  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
*  THE SOFTWARE.
*
******************************************************************************/
public class Cube2Cyl {
	/*
	 pxCamV(0),
        pxCamH(0),
        diameter(0.0),
        rdPanoV(0.0),
        rdPanoH(0.0),
        pxPanoSizeV(0),
        pxPanoSizeH(0),
        cubeFaceId(CUBE_FRONT),
        mappedX(0.0),
        mappedY(0.0),
        normTheta(0.0),
        resCal(0.001),
        normFactorX(1.0),
        normFactorY(1.0),
        sizeRatio(1.0),
        tX(0.0),
        tY(0.0),
        tZ(0.0),
        tTheta(0.0),
        tPhi(0.0),
        phiThreshold(0.0),
        map(NULL) 
	 */
	
public static final double M_PI    =    3.14159265358979323846;
public static final double M_PI_2   =   1.57079632679489661923;
public static final double M_PI_4   =   0.78539816339744830962;

 int pxCamV;    /**< The vertical pixel of a camera */
 int pxCamH;    /**< The horizontal pixel of a camera */

double diameter;        /**< The diameter of the sphere */

double rdPanoV;        /**< The vertical view portion */
double rdPanoH;        /**< The horizontal view portion */

//-------- output information
 public int pxPanoSizeV;   /**< The vertical pixels of the panorama */
 public int pxPanoSizeH;   /**< The horizontal pixels of the panorama */

 CUBE_FACES    cubeFaceId=CUBE_FACES.CUBE_FRONT;    /**< The cube face to be read */
 double        mappedX;       /**< The x coordinate mapped on the cube face */
 double        mappedY;       /**< The y coordinate mapped on the cube face */
 
 double normTheta;   /**< The normalised theta */
 double resCal=0.001;      /**< The resolution used for calculation */
 double normFactorX=1.0; /**< The normalisation factor for x */
 double normFactorY=1.0; /**< The normalisation factor for y */

 double sizeRatio=1.0;   /**< The size ratio of the mapped x and the actual diameter */

 double tX;          /**< x coordinate in 3D space */
 double tY;          /**< y coordinate in 3D space */
 double tZ;          /**< z coordinate in 3D space */

 double tTheta;      /**< The radian horizontally */
 double tPhi;        /**< The radian vertically */

 double phiThreshold;    /**< The threshold of phi, it separates the top, middle and down
  of the cube, which are the edges of the top and down surface */

public CUBE_COORD[] map;    /**< The map from panorama coordinates to cubic coordinates */
public enum CUBE_FACES {
    CUBE_TOP,
    CUBE_LEFT,
    CUBE_FRONT,
    CUBE_RIGHT,
    CUBE_BACK,
    CUBE_DOWN,
    CUBE_FACE_NUM
};

public static final class CUBE_COORD{
	public CUBE_FACES face;
	public double x;
	public double y;
}

public void init( int pxInW, double rdInV,  double rdInH) {
     int pxPanoH = ( int)((rdInH/M_PI_2) * (double)pxInW);
     int pxPanoV = ( int)((rdInV/M_PI_2) * (double)pxInW);
    init(pxPanoH, pxPanoV, pxInW, rdInV, rdInH);
}

public void init(int pxPanoH,  int pxPanoV,  int pxInW,  double rdInV,  double rdInH) {
    // check parameters
    if (   (pxInW   == 0)
        || (pxPanoH == 0)
        || (pxPanoV == 0)) {
        return;
    }

    // check the view portion
    if (   ((rdInV < 0.01) || (rdInV > M_PI))
        || ((rdInH < 0.01) || (rdInH > (M_PI * 2.0)))) {
        return;
    }

    pxCamV = pxInW;
    pxCamH = pxInW;

    rdPanoV = rdInV;
    rdPanoH = rdInH;

    pxPanoSizeH = pxPanoH;
    pxPanoSizeV = pxPanoV;

    diameter = ((double)pxInW) / 2.0;

    // the actual calculation resolution is 10 times bigger than the texture resolution
    resCal = M_PI_4 / (double)pxCamH / 10.0;

    // the normalisation factors
    normFactorX = rdPanoH / (M_PI * 2);
    normFactorY = rdPanoV /  M_PI;
}

XY rotRad(double rad, double x, double y) {
   double temp = x;
   
    XY xy=new XY();
    xy.x = x * Math.cos(rad) - y * Math.sin(rad);
    xy.y = temp * Math.sin(rad) + y * Math.cos(rad);
    
   return xy;
}

void transDis(double dis, XY xy) {
    xy.x += dis;
    xy.y += dis;
}

 void calXY( int i, int j) {
    XYZ xyz=calXYZ(i, j);

    tX=xyz.x;
    tY=xyz.y;
    tZ=xyz.z;
    
    switch (cubeFaceId) {
        case CUBE_TOP: {
            locate(tY, tZ, tX, M_PI);
            break;
        }
        case CUBE_DOWN: {
            locate(tY, tX, tZ, -M_PI_2);
            break;
        }
        case CUBE_LEFT: {
            locate(tZ, tX, tY, M_PI);
            break;
        }
        case CUBE_RIGHT: {
            locate(tZ, tY, tX, M_PI_2);
            break;
        }
        case CUBE_FRONT: {
            locate(tX, tZ, tY, 0.0);
            break;
        }
        case CUBE_BACK: {
            locate(tX, tY, tZ, -M_PI_2);
            break;
        }
        default: {
            break;
        }
    }
}
 
 public void genMap() {

	    map = new CUBE_COORD[pxPanoSizeH*pxPanoSizeV];

	    int pos = 0;
	    
	    //warning,original code is but i preffer y-loop first
	    /*
	    for (unsigned int x = 0; x < pxPanoSizeH; ++x) {
        for (unsigned int y = 0; y < pxPanoSizeV; ++y) {
	     */
	    
	    /*
	     * to get x,y
	     * for(int i=0;i<map.length;i++){
			int x=i%w;
			int y=i/w;
			}
	     * 
	     */
	    
	   
	    for ( int y = 0; y < pxPanoSizeV; ++y) {
	    for ( int x = 0; x < pxPanoSizeH; ++x) {
	       
	        	
	            calXY(x, y);

	            map[pos]=new CUBE_COORD();
	            
	            map[pos].face = cubeFaceId;
	            map[pos].x    = mappedX;
	            map[pos].y    = mappedY;
	            
	            pos++;
	        }
	    }
	}

void locate( double axis,  double px,  double py,  double rad) {
    sizeRatio = diameter / axis;

    mappedX = sizeRatio * px;
    mappedY = sizeRatio * py;

    // rotate
    XY xy=rotRad(rad, mappedX, mappedY);

    // translate
    transDis(diameter, xy);
    
    mappedX=xy.x;
    mappedY=xy.y;
}

void calCubeFace( double theta,  double phi) {
	this.tTheta=theta;
	this.tPhi=phi;
    // Looking at the cube from top down
    // FRONT zone
    if (isDoubleInRange(theta, -M_PI_4, M_PI_4, resCal)) {
        cubeFaceId = CUBE_FACES.CUBE_FRONT;
        normTheta  = theta;
    }
    // LEFT zone
    else if (isDoubleInRange(theta, -(M_PI_2 + M_PI_4), -M_PI_4, resCal)) {
        cubeFaceId = CUBE_FACES.CUBE_LEFT;
        normTheta  = theta + M_PI_2;
    }
    // RIGHT zone
    else if (isDoubleInRange(theta, M_PI_4, M_PI_2 + M_PI_4, resCal)) {
        cubeFaceId = CUBE_FACES.CUBE_RIGHT;
        normTheta  = theta - M_PI_2;
    }
    else {
        cubeFaceId = CUBE_FACES.CUBE_BACK;

        if (theta > 0.0) {
            normTheta = theta - M_PI;
        }
        else {
            normTheta = theta + M_PI;
        }
    }

    // find out which segment the line strikes to
    phiThreshold = Math.atan2(1.0, 1.0 / Math.cos(normTheta));

    // in the top segment
    if (phi > phiThreshold) {
        cubeFaceId = CUBE_FACES.CUBE_DOWN;
    }
    // in the bottom segment
    else if (phi < -phiThreshold) {
        cubeFaceId = CUBE_FACES.CUBE_TOP;
    }
    // in the middle segment
    else {
        ;
    }
}

XYZ calXYZ(int i, int j) {
	XY xy=calNormXY(i, j);
    ThetaPhi tp=calThetaAndPhi(xy.x, xy.y);
    XYZ xyz=calXyzFromThetaPhi(tp.theta, tp.phi);

    calCubeFace(tp.theta, tp.phi);
    
    return xyz;
}

public final class XY{
	double x;
	double y;
}

XY calNormXY(int i, int j) {
	XY xy=new XY();
	xy. x = ((2.0 * i) / pxPanoSizeH - 1.0) * normFactorX;
	xy. y = ((2.0 * j) / pxPanoSizeV - 1.0) * normFactorY;
    // y = 1.0 - (2.0*j)/pxPanoSizeV;
    return xy;
}

ThetaPhi calThetaAndPhi( double x,  double y) {
    
	ThetaPhi thetaPhi=new ThetaPhi();
	thetaPhi.theta = x * M_PI;
	thetaPhi.phi   = y * M_PI_2;
	
    return thetaPhi;
}

public final class ThetaPhi{
	double theta;
	double phi;
}

public final class XYZ{
	double x;
	double y;
	double z;
}

XYZ calXyzFromThetaPhi(double theta,double phi) {
	XYZ xyz=new XYZ();
	xyz.x = Math.cos(phi) * Math.cos(theta);
	xyz.y = Math.sin(phi);
	xyz.z = Math.cos(phi) * Math.sin(theta);
	return xyz;
}

boolean cmpDoubleEqual(double a, double b, double epsilon) {
    return (Math.abs(a - b) < epsilon);
}

boolean cmpDoubleSmaller(double a, double b, double epsilon) {
    return ((a - b) < 0) && (!cmpDoubleEqual(a, b, epsilon));
}

boolean cmpDoubleEqualSmaller(double a, double b, double epsilon) {
    return ((a - b) < 0) || cmpDoubleEqual(a, b, epsilon);
}

boolean cmpDoubleLarger(double a, double b,double epsilon) {
    return ((a - b) > 0) && (!cmpDoubleEqual(a, b, epsilon));
}

boolean cmpDoubleEqualLarger(double a,double b,double epsilon) {
    return ((a - b) > 0) || cmpDoubleEqual(a, b, epsilon);
}

 boolean isDoubleInRange(double value, double small, double large,double epsilon) {
    return    cmpDoubleEqualLarger(value, small, epsilon)
           && cmpDoubleSmaller(    value, large, epsilon);
}
}
