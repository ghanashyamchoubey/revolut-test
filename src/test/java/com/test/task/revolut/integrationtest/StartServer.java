package com.test.task.revolut.integrationtest;

import com.main.task.revolut.main.App;

public class StartServer implements Runnable {

	@Override
	public void run() {
		try {
			App.startJetty();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
