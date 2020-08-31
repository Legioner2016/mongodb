package com.example.demo.model;

/**
 * Aggregation - user lastname + firstname and average age
 * 
 * @author legioner
 *
 */
public class UserAggr {
	private String _id;
	private String fullName;
	private Float avgAge;
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public Float getAvgAge() {
		return avgAge;
	}
	public void setAvgAge(Float avgAge) {
		this.avgAge = avgAge;
	}
	
	@Override
	public String toString() {
		return  this.fullName + " " + this.avgAge;
	}
	
	
	
}
