package com.zdhx.androidbase.util;

import android.content.Context;
import android.os.Looper;

public class ProgressThreadWrap {


	private RunnableWrap runnableWrap;
	private Thread d;


	public ProgressThreadWrap(Context context, RunnableWrap runnableWrap) {
		this.runnableWrap = runnableWrap;
	}

	public void start() {
		d = new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				runnableWrap.run();
				Looper.loop();
			}
		});
		d.start();
	}

	public void stop(){
//			d.interrupt();
		if (d != null&&d.isAlive()){
			while(true){
				try{
					synchronized (d)
					{
						System.out.println("waiting");
						d.wait();
					}
				}
				catch(InterruptedException e){
					e.printStackTrace();
				}
			}
//			d.stop();
		}
	}
}
