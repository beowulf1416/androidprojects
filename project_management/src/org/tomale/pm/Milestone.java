package org.tomale.pm;

import java.util.Date;

public class Milestone {

	public String _title;
	public Date _date_due;
	
	public String get_title(){
		return _title;
	}
	
	public void set_title(final String title){
		_title = title;
	}
	
	public Date get_date_due(){
		return _date_due;
	}
	
	public void set_date_due(final Date due){
		_date_due = due;
	}
}
