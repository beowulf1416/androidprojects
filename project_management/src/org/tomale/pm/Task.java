package org.tomale.pm;

import java.util.ArrayList;
import java.util.Date;

public class Task {

	String _id;
	String _title;
	String _desc;
	
	Milestone _milestone = null;
	
	Task _parent_task = null;
	ArrayList<Task> _tasks = new ArrayList<Task>();
	
	Date _projected_start = new Date();
	Date _projected_end;
	
	Date _actual_start;
	Date _actual_end;
	
	public Task(){
		_projected_end = new Date(_projected_start.getYear(), _projected_start.getMonth(), _projected_start.getDate()+1);
	}
	
	public String get_id(){
		return _id;
	}
	
	public void set_id(final String id){
		_id = id;
	}
	
	public String get_title(){
		return _title;
	}
	
	public void set_title(final String title){
		_title = title;
	}
	
	public String get_description(){
		return  _desc;
	}
	
	public void set_description(final String description){
		_desc = description;
	}
	
	public Milestone get_milestone(){
		return _milestone;
	}
	
	public void set_milestone(Milestone milestone){
		_milestone = milestone;
	}
	
	public boolean has_parent_task(){
		return _parent_task != null;
	}
	
	public Task get_parent_task(){
		return _parent_task;
	}
	
	public void set_parent_task(Task task){
		_parent_task = task;
	}
	
	public final ArrayList<Task> get_tasks(){
		return _tasks;
	}
	
	public void add_task(Task task){
		_tasks.add(task);
	}
	
	public Date get_start_date(){
		return _actual_start == null ? _projected_start : _actual_start;
	}
	
	public Date get_end_date(){
		return _actual_end == null ? _projected_end : _actual_end;
	}
	
	public Date get_actual_start(){
		return _actual_start;
	}
	
	public void set_actual_start(Date value){
		_actual_start = value;
	}
	
	public Date get_actual_end(){
		return _actual_end;
	}
	
	public Date get_projected_start(){
		return _projected_start;
	}
	
	public void set_projected_start(Date value){
		if(value.after(_projected_start)){
			long duration = _projected_end.getTime() - _projected_start.getTime();
			_projected_end = new Date(value.getTime() + duration);
		}
		_projected_start = value;
		
	}
	
	public Date get_projected_end(){
		return _projected_end;
	}
	
	public void set_projected_end(Date value){
		if(value.before(_projected_start)){
			long duration = _projected_end.getTime() - _projected_start.getTime();
			_projected_start = new Date(value.getTime() + duration);
		}
		_projected_end = value;
	}
	
}
