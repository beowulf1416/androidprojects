package org.tomale.pm.android.activity;

import java.util.Date;
import java.util.GregorianCalendar;

import org.tomale.pm.Milestone;
import org.tomale.pm.Project;
import org.tomale.pm.Task;

import android.app.Activity;
import android.os.Bundle;

public class ProjectManagementActivity extends Activity {
	
	GanttView _view;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

        Date now = new Date();
        
        Milestone m1 = new Milestone();
        m1.set_title("Milestone 1");
        
        Task t1 = new Task();
        t1.set_title("Task 1");
        t1.set_projected_start(new Date(now.getYear(),now.getMonth(),now.getDay()+1));
        t1.set_projected_end(new Date(now.getYear(),now.getMonth(),now.getDay()+2));
        m1.add_task(t1);
        
        Task t2 = new Task();
        t2.set_title("Task 2");
        t2.set_projected_start(new Date(now.getYear(),now.getMonth(),now.getDay()+1));
        t2.set_projected_end(new Date(now.getYear(),now.getMonth(),now.getDay()+2));
        
        m1.add_task(t2);
        
        Milestone m2 = new Milestone();
        m2.set_title("Milestone 2");
        
        Task t3 = new Task();
        t3.set_title("task 3");
        m2.add_task(t3);
        
        Task t4 = new Task();
        t4.set_title("task 4");
        m2.add_task(t4);
        
        Task t5 = new Task();
        t5.set_title("task 5");
        m2.add_task(t5);
        
        Project p = new Project();
        p.add_milestone(m1);
        p.add_milestone(m2);
        
        _view = new GanttView(getApplicationContext());
        _view.set_project(p);
        
        
        setContentView(_view);
    }
}