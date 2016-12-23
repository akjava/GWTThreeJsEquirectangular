package com.akjava.gwt.equirectangular.server;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.io.BaseEncoding;
import com.google.common.io.Files;

public class Base64FileSaveServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/*
	 * clear only .png file
	 */
	private boolean clearImages(){
		File file=new File(getBaseDirectory());
		if(!file.exists()){
			System.out.println("Base64FileSaveServlet-faild clear images:not exist:"+getBaseDirectory());
			return false;
		}

		String[] names=file.list();
		for(String name:names){
			if(name.toLowerCase().endsWith(".png")){
				new File(file,name).delete();
			}
		}
		
		
		return true;
	}
	
	private String getBaseDirectory(){
		String directory=getInitParameter("directory");
		String fileSeparator=System.getProperty("file.separator");
		if(directory==null){
			directory=System.getProperty("user.home")+fileSeparator+"gwtthreejsequirectangular";
		}
		
		if(!directory.endsWith(fileSeparator)){
			//TODO check endless contnue "\\\\\\\"
			directory=directory+fileSeparator;
		}
		return directory;
	}
	
	//for debug otherwise 405 error
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		doPost(req,resp);
	}
	
	/**
	 * name must be end with .png for clear by command
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String directory=getBaseDirectory();
		//System.out.println(directory);//debug
		
		String command=req.getParameter("command");
		if(command!=null && command.equals("clear")){
			boolean result=clearImages();
			resp.getWriter().println("clearImages:"+result);
			return;
		}
		
		
		//just send filename & data & simply write it
		String name=req.getParameter("name");
		if(name==null){
			resp.sendError(500,"no name");
			return;
		}
		String data=req.getParameter("data");
		
		if(data==null){
			resp.sendError(500,"no data");
			return;
		}
		byte[] bytes=null;
		if(data.startsWith("data:")){
			//TODO more check
			int index=data.indexOf(";base64,");
			if(index==-1){
				resp.sendError(500,"not base64");
				return;
				
			}
			bytes=BaseEncoding.base64().decode(data.substring(index+";base64,".length()));
		}else{
			bytes=data.getBytes();
		}
		
		//don't care encode
		
		File file=new File(directory+name);
		
		Files.write(bytes, file);
		
		
		resp.getWriter().println("Base64FileSaveServlet:written "+bytes.length);
		//TODO
		//call nona,optional 
	}
}
