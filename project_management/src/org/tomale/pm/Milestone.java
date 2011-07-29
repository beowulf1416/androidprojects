package org.tomale.pm;

import java.util.ArrayList;
import java.util.Date;

public class Milestone {

	String _title;
	
	Date _start = new Date();
	Date _end = new Date();
	
	Date _date_due;
	
	ArrayList<Task> _tasks = new ArrayList<Task>();
	
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
	
	public final ArrayList<Task> get_tasks(){
		return _tasks;
	}
	
	public void add_task(Task task){
		
		if(_start.after(task.get_start_date())){
			_start = task.get_start_date();
		}
		
		if(_end.before(task.get_end_date())){
			_end = task.get_end_date();
		}
		
		_tasks.add(task);
	}
	
	public Date get_start_date(){
		return _start;
	}
	
	public Date get_end_date(){
		return _end;
	}
	
}
