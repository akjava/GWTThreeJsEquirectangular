package com.akjava.gwt.equirectangular.client;

import java.util.ArrayList;
import java.util.List;

public  class SixCubicImageDataUrl{
	private List<String> all=new ArrayList<String>();
	
	/**
	 * 
	 * @param up negative Y
	 * @param down Positive Y
	 * @param front negative Z
	 * @param back positive Z
	 * @param right positive X
	 * @param left negative X
	 */
	public SixCubicImageDataUrl(String up, String down, String front, String back, String right, String left) {
		super();
		all.add(up);
		all.add(down);
		all.add(front);
		all.add(back);
		all.add(right);
		all.add(left);
	}
	
	public String getUp() {
		return all.get(0);
	}
	public String getDown() {
		return all.get(1);
	}
	public String getFront() {
		return all.get(2);
	}
	public String getBack() {
		return all.get(3);
	}
	public String getRight() {
		return all.get(4);
	}
	public String getLeft() {
		return all.get(5);
	}
	public List<String> getAll(){
		return all;
	}
	
	
}