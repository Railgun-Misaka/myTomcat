package com.fly.mytomcat.application;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadPoolExecutor;

import com.fly.mytomcat.exception.TcontinueException;
import com.fly.mytomcat.http.Request;
import com.fly.mytomcat.http.Response;

/**
 * 运行时主进程
 * @author Administrator
 *
 */
final class MainThread extends Thread {
	
	//ServerSocket
	private final ServerSocket ss ;
		
	//线程池
	private final ThreadPoolExecutor threadPool ;
	
	public MainThread(ServerSocket ss, ThreadPoolExecutor threadPool) {
		this.ss = ss;
		this.threadPool = threadPool;
	}
	
	/**
	 * 接收socket并向线程池中添加任务
	 */
	@Override
	public void run() {
		while (ss != null && !ss.isClosed()) {
			Socket s;
			try {
				s = ss.accept();
			} catch (IOException e) {
				continue ;
				//e.printStackTrace();
			}
			//向线程池中添加任务
			threadPool.execute(new Runnable() {
				@Override
				public void run(){
					try {
						distributeIO(s);
					} catch (TcontinueException e) {
						//中断当前线程
						Thread.currentThread().interrupt();
						e.printStackTrace();
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	private void distributeIO(Socket s) throws TcontinueException {
		try {
			
			Request request = new Request(s.getInputStream());
			Response response = new Response(s.getOutputStream());
			
			DistributeIO.execute(request, response);
		
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}
}
