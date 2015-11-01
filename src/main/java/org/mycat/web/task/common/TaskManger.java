package org.mycat.web.task.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TaskManger {
	private final static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10); 
	private static volatile TaskManger taskManger;
	private static final List<String> dbNames = new ArrayList<String>();
	private TaskManger(){}

	
	public static TaskManger getInstance(){
		if(taskManger == null){
			synchronized (TaskManger.class) {
				if(taskManger == null){
					taskManger = new TaskManger();
				}
			}
		}
		return taskManger;
	} 
	
	public void addTask(final ITask task, long period){
		scheduler.scheduleAtFixedRate(new Runnable() {  
            public void run() {
            	Date nowDate = new Date();
        		for(String dbName : dbNames){
        			try{
        				System.out.println("dbName=============" + dbName);
        				task.excute(dbName, nowDate);
        			}catch(Exception ex){
                		ex.printStackTrace();
                	}
        		}
            }  
        }, 0, period, TimeUnit.MILLISECONDS);  
		
		
	}
	
	public void addDBName(String dbName){
		if(dbName != null){
			if(!dbNames.contains(dbName)){
				dbNames.add(dbName);
			}
		}
	}
	
}
