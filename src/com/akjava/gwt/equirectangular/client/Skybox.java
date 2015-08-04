package com.akjava.gwt.equirectangular.client;

import com.akjava.gwt.lib.client.CanvasUtils;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.ImageElement;

public class Skybox {
private ImageElement imageElement;

public Skybox(ImageElement imageElement) {
	super();
	this.imageElement = imageElement;
	cubicSize=imageElement.getWidth()/4;
}

//TODO change naming
public Canvas getUp(){
	return getAt(1,0);
}
public Canvas getDown(){
	return getAt(1,2);
}

public Canvas getLeft(){
	return getAt(0,1);
}

public Canvas getRight(){
	return getAt(2,1);
}

public Canvas getFront(){
	return getAt(1,1);
}
public Canvas getBack(){
	return getAt(3,1);
}




private Canvas getAt(int x,int y){
	Canvas canvas=CanvasUtils.createCanvas(cubicSize, cubicSize);
	canvas.getContext2d().drawImage(imageElement, -cubicSize*x, -cubicSize*y);
	return canvas;
}




 int cubicSize;//TODO


public static boolean isValidImage(ImageElement element){
	if(element.getWidth()==0 || element.getHeight()==0){
		return false;
	}
	return element.getWidth()/4*3==element.getHeight();
}

}
