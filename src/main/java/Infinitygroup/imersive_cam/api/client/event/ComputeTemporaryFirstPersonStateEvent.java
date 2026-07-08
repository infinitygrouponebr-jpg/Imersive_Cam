package Infinitygroup.imersive_cam.api.client.event;

import Infinitygroup.imersive_cam.api.event.CancellableEvent;

/**
 * This event can be used to trigger temporary first person.
 *
 * @since 5.0.0
 */
public class ComputeTemporaryFirstPersonStateEvent extends CancellableEvent {
	private boolean result;
	
	public boolean getResult() {
		return this.result;
	}
	
	public void setResult(boolean result) {
		this.result = result;
	}
}
