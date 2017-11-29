package com.example.gregg.myapplication;

public class Pin{
	public int pinID, votes;
	public String username, position, category, description;
	public long time

	public Pin(String pos, String cat, String des, String usr, int uid, int vts, long tme){
		position=pos;
		category=cat;
		description=des;
		username=usr;
		pinID=uid;
		votes=vts;
		time=tme;
	}
}
