package me.devtec.theapi.scheduler;

public abstract class Tasker implements Runnable {
	private boolean cancel;
	private int task = -1;

	public final synchronized boolean isCancelled() {
		return cancel;
	}

	public final synchronized void cancel() {
		if(task==-1)return;
		cancel = true;
		Scheduler.cancelTask(task);
	}

	public final synchronized int getId() {
		return task;
	}

	//ASYNCHRONOUS
	
	public final int runTask() {
		cancel=false;
		return task = Scheduler.run(this);
	}

	public final int runRepeating(long delay, long period) {
		cancel=false;
		if (task == 0)
			return task = Scheduler.repeating(delay, period, this);
		return task;
	}

	public final int runTimer(long delay, long period, long times) {
		return runRepeatingTimes(delay, period, times, null);
	}

	public final int runRepeatingTimes(long delay, long period, long times) {
		return runRepeatingTimes(delay, period, times, null);
	}

	public final int runRepeatingTimes(long delay, long period, long times, Runnable onFinish) {
		cancel=false;
		cancel=false;
		if (task == 0)
			return task = Scheduler.repeatingTimes(delay, period, times, this, onFinish);
		return task;
	}

	public final int runLater(long delay) {
		cancel=false;
		if (task == 0)
			return task = Scheduler.later(delay, this);
		return task;
	}
	
	//SYNCHRONOUS WITH SERVER

	public final int runTaskSync() {
		cancel=false;
		return task = Scheduler.runSync(this);
	}

	public final int runRepeatingSync(long delay, long period) {
		cancel=false;
		if (task == 0)
			return task = Scheduler.repeatingSync(delay, period, this);
		return task;
	}

	public final int runTimerSync(long delay, long period, long times) {
		return runRepeatingTimesSync(delay, period, times, null);
	}

	public final int runRepeatingTimesSync(long delay, long period, long times) {
		return runRepeatingTimesSync(delay, period, times, null);
	}

	public final int runRepeatingTimesSync(long delay, long period, long times, Runnable onFinish) {
		cancel=false;
		if (task == 0)
			return task = Scheduler.repeatingTimesSync(delay, period, times, this, onFinish);
		return task;
	}

	public final int runLaterSync(long delay) {
		cancel=false;
		if (task == 0)
			return task = Scheduler.laterSync(delay, this);
		return task;
	}
}