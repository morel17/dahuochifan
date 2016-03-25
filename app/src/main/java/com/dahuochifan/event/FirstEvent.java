package com.dahuochifan.event;

import com.dahuochifan.model.userinfo.PersonInfoDetail;

import java.util.List;

public class FirstEvent {
	private int type;
	private String mMsg;
	private List<String> list;
	private PersonInfoDetail detial;
	private int posititon;
	private String locaiton;
	public FirstEvent(int type) {
		this.type = type;
	}
	public FirstEvent(int type,int code) {
		this.type = type;
		this.posititon=code;
	}
	public FirstEvent(String msg) {
		mMsg = msg;
	}
	public FirstEvent(List<String> list,String msg){
		mMsg = msg;
		this.list=list;
	}
	public FirstEvent(PersonInfoDetail detial,String msg){
		mMsg = msg;
		this.detial=detial;
	}
	public FirstEvent(int type,String msg){
		mMsg = msg;
		this.type=type;
	}
	public FirstEvent(String msg,List<String> list){
		mMsg = msg;
		this.list=list;
	}
	public FirstEvent(String msg,String location){
		mMsg = msg;
		this.locaiton=location;
	}
	public String getMsg(){
		return mMsg;
	}
	public List<String> getList(){
		return list;
		
	}
	public int getType(){
		return type;
	}
	public PersonInfoDetail getDetail(){
		return detial;
	}
	public int getPosition(){
		return posititon;
	}

	public String getLocaiton() {
		return locaiton;
	}
}
