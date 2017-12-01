package com.example.gregg.myapplication;

public class Pin{
	public int pinID, votes;
	public String username, position, category, description;
	public long time;

	public Pin(String position, String category, String description, String user, int pin, int votes, long time){
		this.position=position;
		this.category=category;
		this.description=description;
		this.username=user;
		this.pinID=pin;
		this.votes=votes;
		this.time=time;
	}
}
