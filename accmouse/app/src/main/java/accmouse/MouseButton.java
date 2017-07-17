package accmouse;

public class MouseButton {

	private boolean state = false;

	public MouseButton() {
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public boolean getState() {
		return state;
	}
}
