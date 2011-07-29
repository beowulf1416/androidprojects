package org.tomale.pm;

import java.util.ArrayList;

public class Project {

	ArrayList<Milestone> _milestones = new ArrayList<Milestone>();
	
	public Project(){
		
	}
	
	public final ArrayList<Milestone> get_milestones(){
		return _milestones;
	}
	
	public void add_milestone(Milestone milestone){
		_milestones.add(milestone);
	}
	
}
