package com.ebridgecommerce.domain;

public class HourOfDayDTO {
	
	private String hourOfDay;
	private String hourOfDayLabel;
	
	public HourOfDayDTO(){
	}
		
	public HourOfDayDTO(String hourOfDay, String hourOfDayLabel) {
		this.hourOfDay = hourOfDay;
		this.hourOfDayLabel = hourOfDayLabel;
	}

	public String getHourOfDay() {
		return hourOfDay;
	}
	public void setHourOfDay(String hourOfDay) {
		this.hourOfDay = hourOfDay;
	}
	public String getHourOfDayLabel() {
		return hourOfDayLabel;
	}
	public void setHourOfDayLabel(String hourOfDayLabel) {
		this.hourOfDayLabel = hourOfDayLabel;
	}
	
	public String toString(){
		return hourOfDay + " - " + hourOfDayLabel;
	}
	
}
