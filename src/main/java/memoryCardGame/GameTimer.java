package memoryCardGame;

import javafx.animation.*;
import javafx.beans.property.*;
import javafx.util.Duration;

/**
 * This class is used for the game timer.
 */
public class GameTimer {

	private Timeline timeline;
	private LongProperty secProperty;

	/**
	 * Create a GameTimer.
	 */
	public GameTimer() {
		secProperty = new SimpleLongProperty(0);

		timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
			long pre = secProperty.get();
			secProperty.set(++pre);
		}));
		timeline.setCycleCount(Timeline.INDEFINITE);
	}

	/**
	 * Start the timer.
	 */
	public void start() {
		timeline.play();
	}

	/**
	 * Pause the timer.
	 */
	public void pause() {
		timeline.pause();
	}

	/**
	 * Stop the timer.
	 */
	public void stop() {
		timeline.stop();
		secProperty.set(0);
	}

	/**
	 * Return the ellapsed time as second.
	 * @return The elapsed time.
	 */
	public ReadOnlyLongProperty secProperty() {
		return secProperty;
	}

	/**
	 * Return the ellapsed time as second.
	 * @return The ellapsed time.
	 */
	public long getCurrentSec() {
		return secProperty.get();
	}
}
