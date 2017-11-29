package com.example.gregg.myapplication;

public class Pin{
	public int pinID, votes;
	public String username, position, category, description;

	public Pin(String pos, String cat, String des, String usr, int id, int vts){
		position=pos;
		category=cat;
		description=des;
		username=usr;
		pinID=id;
		votes=vts;
	}
}
