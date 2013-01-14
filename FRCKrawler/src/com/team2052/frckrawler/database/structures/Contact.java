package com.team2052.frckrawler.database.structures;

/*****
 * Class: Contact
 * 
 * @author Charles Hofer
 *****/

public class Contact implements Structure {
	
	private int team;
	private int contactID;
	private String name;
	private String email;
	private String address;
	private String phoneNumber;
	
	public Contact(String _name, String _email, String _address, String _phoneNumber) {
		
		this(-1, -1, _name, _email, _address, _phoneNumber);
	}
	
	public Contact(int _team, int _contactID, String _name, String _email, 
			String _address, String _phoneNumber) {
		
		team = _team;
		contactID = _contactID;
		name = _name;
		email = _email;
		address = _address;
		phoneNumber = _phoneNumber;
	}
	
	public int getTeamNumber() {
		return team;
	}
	
	public int getContactID() {
		return contactID;
	}
	
	public String getName() {
		return name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getAddress() {
		return address;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
}
