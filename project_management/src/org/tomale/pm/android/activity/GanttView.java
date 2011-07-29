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
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GanttView extends SurfaceView
	implements SurfaceHolder.Callback {

	Project _project;
	
	Calendar _start = Calendar.getInstance();
	Calendar _end = Calendar.getInstance();
	
	ArrayList<Item> _items = new ArrayList<GanttView.Item>();
	
	DrawControllerThread _thread;
	
	Bitmap _buffer;
	
	final int HEADER_HEIGHT = 60;
	
	final int ITEM_HEIGHT_NORMAL = 50;
	final int ITEM_HEIGHT_EXPANDED = 100;
	
	int _item_width = 100;
	int _period_width = 100;
	
	Paint _pTask = new Paint();
	
	public GanttView(Context context) {
		super(context);
		getHolder().addCallback(this);
		_thread = new DrawControllerThread(getHolder(), this);
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
			
			// draw header
			paint.setColor(Color.BLUE);
			bCanvas.drawRect(new Rect(0, 0, width, HEADER_HEIGHT), paint);
			
			// draw periods
			_end = _start;
			for(int i = _period_width; i < width; i += _period_width){
				paint.setColor(Color.LTGRAY);
				bCanvas.drawLine(i, 0, i, height, paint);
				_end.add(Calendar.DATE, 1);
				
				paint.setColor(Color.WHITE);
				bCanvas.drawText(
						String.format("%d/%d/%d", new Object[]{_end.get(Calendar.MONTH), _end.get(Calendar.DAY_OF_MONTH), _end.get(Calendar.YEAR)}), 
						i, 
						40, 
						paint);
			}
			
//			int bar_start = _item_width;
//			int bar_end = width;
			
//			long time_start = _start.getTimeInMillis()/1000;
//			long time_end = _end.getTimeInMillis()/1000;
			
//			float slope = (bar_end - bar_start)/(time_end - time_start);
//			long item_start;
//			long item_end;
			
			// draw items
			int y = HEADER_HEIGHT;
			int y1;
			int y2;
//			boolean item_task = false;
			for(Item i : _items){

				y1 = y;
				
				i.set_y1(y1);
				i.set_y2(i.is_expanded() ? (y += ITEM_HEIGHT_EXPANDED) : (y += ITEM_HEIGHT_NORMAL));
	
				y2 = y;
				
//				String title = "";
				Object data = i.get_data();

//				item_start = time_end;
//				item_end = time_end;
				
				if(data instanceof Milestone){
					Milestone m = (Milestone) data;
					draw_milestone(bCanvas, m, new Rect(0, y1, width, y2));
				}
				
				if(data instanceof Task){
					Task t = (Task) data;
					draw_task(bCanvas, t, new Rect(0, y1, width, y2));
				}
				
				
				// draw bar
//				paint.setColor(Color.WHITE);
//				bCanvas.drawRoundRect(
//					new RectF(
//						(item_start*bar_start)/time_start, 
//						y1+10, 
//						(item_end*bar_start)/time_start, 
//						y2-10), 
//					2, 
//					2, 
//					paint
//				);
				
//				paint.setColor(Color.WHITE);
//				bCanvas.drawText(title, item_task ? 20 : 10, y2-10, paint);
				
//				bCanvas.drawText(String.format("%d %d %d %d", new Object[]{
//						time_start, 
//						time_end,
//						(item_start*bar_start)/time_start,
//						(item_end*bar_end)/time_end}),
//					150, y2, paint);
					
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
		
		canvas.drawText(task.get_title(), rect.left+20, (rect.top+rect.bottom)/2, _pTask);
		
	}
	
	private void draw_milestone(Canvas canvas, Milestone milestone, Rect rect){
		
		canvas.drawText(milestone.get_title(), rect.left+10, (rect.top+rect.bottom)/2, _pTask);
		
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
