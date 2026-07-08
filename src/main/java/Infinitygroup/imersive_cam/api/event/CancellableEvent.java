package Infinitygroup.imersive_cam.api.event;

public class CancellableEvent implements Event {
	private boolean cancelled = false;
	
	public boolean isCancelled() {
		return this.cancelled;
	}
	
	public void cancel() {
		this.cancelled = true;
	}
}
