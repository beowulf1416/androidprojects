package org.tomale.pm;

import java.util.ArrayList;

public class Task {

	Milestone _milestone = null;
	ArrayList<Task> _tasks = new ArrayList<Task>();
	
	public Milestone get_milestone(){
		return _milestone;
	}
	
	public void set_milestone(Milestone milestone){
		_milestone = milestone;
	}
	
	public final ArrayList<Task> get_tasks(){
		return _tasks;
	}
}
