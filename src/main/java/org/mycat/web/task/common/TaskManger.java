package org.mycat.web.task.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.mycat.web.task.server.CheckMycatSuspend;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class TaskManger {
	
	private static final Logger LOG = LoggerFactory.getLogger(TaskManger.class);
	
	private final static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10); 
	public final static Map<String, ScheduledFuture<?>> taskPool = new ConcurrentHashMap<String, ScheduledFuture<?>>();
	private static volatile TaskManger taskManger;
	private static final List<String> dbNames = Collections.synchronizedList(new ArrayList<String>());
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
	
	public void addTask(final ITask task, long period, String taskName){
		ScheduledFuture<?> sf = scheduler.scheduleAtFixedRate(new Runnable() {  
            public void run() {
            	Date nowDate = new Date();
            	LOG.info("定时任务中的dbName列表:" + dbNames.toString());
        		for(String dbName : dbNames){
        			try{
        				task.excute(dbName, nowDate);
        			}catch(Exception ex){
                		ex.printStackTrace();
                	}
        		}
            }  
        }, 0, period, TimeUnit.MILLISECONDS);  
		taskPool.put(taskName, sf);
	}

	public void cancelTask(String... taskNames){
		for(String taskName : taskNames){
			ScheduledFuture<?> sf = taskPool.get(taskName);
			if(sf != null){
				sf.cancel(true);
			}
		}
	}
	
	public void addDBName(String dbName){
		if(dbName != null){
			if(!dbNames.contains(dbName)){
				dbNames.add(dbName);
			}
		}
	}
	
	public void addTask(final ITask task,final long period, String taskName,final int timeout){
		//间隔一定时间，触发一次监测事件
		ScheduledFuture<?> sf = scheduler.scheduleAtFixedRate(new Runnable() {  
            public void run() {
            	//一次监测事件，根据DB数量，一个DB，开一个线程监测
				final Date nowDate = new Date();
        		for(final String dbName : dbNames){
        			try{
		        		ScheduledFuture<?> sf = scheduler.schedule(new Runnable() {  
		        			public void run() {
		                		task.excute(dbName, nowDate);		
		        			}
		        		}, 0, TimeUnit.MILLISECONDS);  
		        		try {
		        			if (timeout != -1)
		        				sf.get(timeout, TimeUnit.SECONDS);
		        		} catch (TimeoutException e) {
		        			//主线程等待计算结果超时，因此中断任务线程！
		        			e.printStackTrace();
		        			if (task instanceof CheckMycatSuspend){
		        				//输出Log
		        				LOG.error("DB:"+((CheckMycatSuspend)task).getDbName()+" 出现假死。");				
		        			}
		        		} catch (InterruptedException e) {
		        			// TODO Auto-generated catch block
		        			e.printStackTrace();
		        		} catch (ExecutionException e) {
		        			// TODO Auto-generated catch block
		        			e.printStackTrace();
		        		}
        			}catch(Exception ex){
                		ex.printStackTrace();
                	}
        		}
            	
            }  
        }, 0, period, TimeUnit.MILLISECONDS);  
		
		taskPool.put(taskName, sf);
	}
	
	
}
