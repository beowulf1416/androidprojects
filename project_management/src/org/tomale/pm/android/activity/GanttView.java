package org.tomale.pm.android.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.tomale.pm.Milestone;
import org.tomale.pm.Project;
import org.tomale.pm.Task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RemoteViews.ActionException;

public class GanttView extends SurfaceView
	implements SurfaceHolder.Callback {

	Project _project;
	
	ArrayList<Item> _items = new ArrayList<GanttView.Item>();
	
	DrawControllerThread _thread;
	
	Bitmap _buffer;
	
	final int HEADER_HEIGHT = 60;
	
	final int ITEM_HEIGHT_NORMAL = 50;
	final int ITEM_HEIGHT_EXPANDED = 100;
	
	int _item_width = 120;
	int _period_width = 100;
	float _slope;
	
	Calendar _current_date = Calendar.getInstance();
	int _current_period = 0;
	
	Paint _pTask = new Paint();

	boolean _scroll = false;
	
	float _offset_x = 0;
	
	float _action_down_pt_x;
	float _action_down_pt_y;
	
	float _action_up_pt_x;
	float _action_up_pt_y;
	
	public GanttView(Context context) {
		super(context);
		getHolder().addCallback(this);
		_thread = new DrawControllerThread(getHolder(), this);
		
		// attach event handlers
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:{
				_action_up_pt_x = event.getX();
				_action_up_pt_y = event.getY();
				break;
			}
			case MotionEvent.ACTION_MOVE:{
				
				float x = event.getX();
				float y = event.getY();
				
				int d = (int) (x - _action_down_pt_x)/_period_width;
				_current_date.add(Calendar.DAY_OF_MONTH, d);
				
				break;
			}
			case MotionEvent.ACTION_UP:{
				_action_down_pt_x = event.getX();
				_action_down_pt_y = event.getY();
				
				int d = (int) (_action_down_pt_x - _action_down_pt_x)/_period_width;
				_current_date.add(Calendar.DAY_OF_MONTH, d);
				
				break;
			}
		}
		
		return true;
	}

	public Project get_project(){
		return _project;
	}
	
	public void set_project(Project project){
		_project = project;
		
		_items.clear();
		for(Milestone m : _project.get_milestones()){
			_items.add(new Item(m));
			for(Task t : m.get_tasks()){
				_items.add(new Item(t));
			}
			
		}
	}
	
	public int get_current_period(){
		return _current_period;
	}
	
	public void set_current_period(int value){
		_current_period = value;
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

		_buffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		_thread.set_running(true);
		_thread.start();
	}
	
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		_thread.set_running(false);
		while(retry){
			try {
				_thread.join();
				retry = false;
			}
			catch(InterruptedException e){
				// do nothing
			}
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
//		super.onDraw(canvas);
		
		try {
			Canvas bCanvas = new Canvas(_buffer);
			// draw background color
			bCanvas.drawColor(Color.BLACK);
			
			int width = _buffer.getWidth();
			int height = _buffer.getHeight();
	
			Paint paint = new Paint();
			_pTask.setColor(Color.WHITE);
			
			// draw items column
			paint.setColor(Color.DKGRAY);
			bCanvas.drawRect(new Rect(0, 0, _item_width, height), paint);
			paint.setColor(Color.RED);
			bCanvas.drawLine(_item_width, 0, _item_width, height, paint);
			
			// draw header
			paint.setColor(Color.BLUE);
			bCanvas.drawRect(new Rect(0, 0, width, HEADER_HEIGHT), paint);
			
			// draw periods
			int period = 0;
			Calendar date = (Calendar) _current_date.clone();
			date.add(Calendar.DATE, _current_period);
			for(int i = 0; i < width; i += _period_width){
				paint.setColor(Color.LTGRAY);
				bCanvas.drawLine(i+_item_width, 0, i+_item_width, height, paint);
				
				paint.setColor(Color.WHITE);

				// draw year and month only once
				if(period == 0 || date.get(Calendar.MONTH) == Calendar.JANUARY){
					bCanvas.drawText(String.format("%d", new Object[]{date.get(Calendar.YEAR)}), i+_item_width+5, 15, paint);
				}
				
				if(period == 0 || date.get(Calendar.DAY_OF_MONTH) == 1){
					bCanvas.drawText(String.format("%d", new Object[]{date.get(Calendar.MONTH)}), i+_item_width+5, 30, paint);
				}
				
				bCanvas.drawText(
						String.format("%d", new Object[]{date.get(Calendar.DAY_OF_MONTH)}), 
						i+_item_width+5, 
						50, 
						paint);
				
				date.add(Calendar.DATE, 1);
				period++;
			}
			
			// draw items
			int y = HEADER_HEIGHT;
			int y1;
			int y2;
			for(Item i : _items){
				y1 = y;
				i.set_y1(y1);
				i.set_y2(i.is_expanded() ? (y += ITEM_HEIGHT_EXPANDED) : (y += ITEM_HEIGHT_NORMAL));
				y2 = y;
				
				Object data = i.get_data();
				
				if(data instanceof Milestone){
					Milestone m = (Milestone) data;
					draw_milestone(bCanvas, m, new Rect(0, y1, width, y2));
				}
				
				if(data instanceof Task){
					Task t = (Task) data;
					draw_task(bCanvas, t, new Rect(0, y1, width, y2));
				}
				
				// draw line at bottom of item
				paint.setColor(Color.GRAY);
				bCanvas.drawLine(0, y2, width, y2, paint);
			}
			
			// draw footer
			paint.setColor(Color.BLUE);
			bCanvas.drawRect(new Rect(0, height - HEADER_HEIGHT, width, height), paint);
			
			Matrix matrix = new Matrix();
			
			canvas.drawBitmap(_buffer, matrix, null);
		} catch(Exception e){
			Log.d("onDraw", e.getMessage());
		}
		
		Log.d("onDraw", "end Draw");
	}
	
	private void draw_task(Canvas canvas, Task task, Rect rect){
		
		canvas.drawText(task.get_title(), rect.left+_offset_x+20, (rect.top+rect.bottom)/2, _pTask);

		int start = (int) (task.get_start_date().getTime() - _current_date.getTimeInMillis())/86400000;
		int end = (int) (task.get_end_date().getTime() - _current_date.getTimeInMillis())/86400000;
		
		canvas.drawText(String.format("%d", new Object[]{start}), 100, rect.bottom, _pTask);
		
		if(start > 0 && end > 0){
			canvas.drawRoundRect(new RectF(
					(start*_period_width)+_item_width+_offset_x, 
					rect.top+20, 
					(end*_period_width)+_item_width, 
					rect.bottom-20
				), 3, 3, _pTask);
		}
		
	}
	
	private void draw_milestone(Canvas canvas, Milestone milestone, Rect rect){
		
		canvas.drawText(milestone.get_title(), rect.left+_offset_x+10, (rect.top+rect.bottom)/2, _pTask);
	
		int start = (int) (milestone.get_start_date().getTime() - _current_date.getTimeInMillis())/86400000;
		int end = (int) (milestone.get_end_date().getTime() - _current_date.getTimeInMillis())/86400000;

		if(start > 0 && end > 0){
			canvas.drawRoundRect(new RectF(
					(start*_period_width)+_item_width+_offset_x, 
					rect.top+20, 
					(end*_period_width)+_item_width, 
					rect.bottom-20
				), 3, 3, _pTask);
		}
		
	}
	
	public class Item {
		
		int _y1;
		int _y2;
		boolean _is_expanded = false;
		
		Object _data;
		
		public Item(Object data){
			_data = data;
		}
		
		public int get_y1(){
			return _y1;
		}
		
		public void set_y1(final int y){
			_y1 = y;
		}
		
		public int get_y2(){
			return _y2;
		}
		
		public void set_y2(final int y){
			_y2 = y;
		}
		
		public boolean is_expanded(){
			return _is_expanded;
		}
		
		public void set_expanded(boolean value){
			_is_expanded = value;
		}
		
		public Object get_data(){
			return _data;
		}
		
		public void set_data(Object data){
			_data = data;
		}
		
	}
	
	public class DrawControllerThread extends Thread {

		SurfaceHolder _holder;
		GanttView _view;
		boolean _is_running = false;
		
		public DrawControllerThread(final SurfaceHolder holder, GanttView view){
			_holder = holder;
			_view = view;
		}
		
		public void set_running(boolean value){
			_is_running = value;
		}
		
		@Override
		public void run() {
			Canvas canvas;
			while(_is_running){
				canvas = null;
				try {
					canvas = _holder.lockCanvas();
					synchronized (_holder) {
						_view.onDraw(canvas);
					}
				} catch(Exception e){
					// todo
				} finally {
					if(canvas != null){
						_holder.unlockCanvasAndPost(canvas);
					}
				}
			}
		}
		
	}

}
