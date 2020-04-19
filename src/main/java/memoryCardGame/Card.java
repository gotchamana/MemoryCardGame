package memoryCardGame;

import static javafx.animation.RotateTransition.Status;
import javafx.animation.*;
import javafx.beans.property.*;
import javafx.scene.image.*;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * This class represents the card on the board.
 */
public class Card extends ImageView {

	private Image backImage, frontImage;
	private BooleanProperty frontProperty;
	private RotateTransition rt1, rt2;
	private SequentialTransition st;

	/**
	 * Create a card with its front and back image.
	 * @param front The card's front image
	 * @param back The card's back image
	 */
	public Card(Image front, Image back) {
		frontImage = front;
		backImage = back;

		frontProperty = new SimpleBooleanProperty(false);

		// Use two animations respectively rotate 90 degrees
		rt1 = new RotateTransition(Duration.millis(250), this);
		rt1.setAxis(Rotate.Y_AXIS);
		rt1.setByAngle(90);
		rt1.setOnFinished(e -> {
			setImage(getIsFront() ? backImage : frontImage);
		});

		rt2 = new RotateTransition(Duration.millis(250), this);
		rt2.setAxis(Rotate.Y_AXIS);
		rt2.setByAngle(90);
		rt2.setOnFinished(e -> {
			frontProperty.set(!getIsFront());
		});

		st = new SequentialTransition(rt1, rt2);
		st.setInterpolator(Interpolator.EASE_IN);

		// The card is covered by default
		setImage(backImage);
	}

	/**
	 * Defines the card if be front.
	 * @return frontProperty
	 */
	public ReadOnlyBooleanProperty frontProperty() {
		return frontProperty;
	}

	/**
	 * Flips the card.
	 */
	public void flip() {
		st.play();
	}

	/**
	 * Stops the flip animation.
	 */
	public void stopFlipping() {
		st.stop();
		setImage(backImage);
		setRotate(0);
	}

	/**
	 * Get the card is front.
	 * @return The result of the card if be front.
	 */
	public boolean getIsFront() {
		return frontProperty.get();
	}

	/**
	 * Get if the card is flipping.
	 * @return The result of the card being flipping.
	 */
	public boolean getIsFlipping() {
		return st.getStatus() == Status.RUNNING;
	}

	public void setFrontImage(Image front) {
		frontImage = front;
	}

	/**
	 * Get the card's front image.
	 * @return The card's front image.
	 */
	public Image getFrontImage() {
		return frontImage;
	}

	/**
	 * Determine two cards are equal.
	 * @param card Another card
	 * @return The result of two cards being equal. 
	 */
	public boolean equals(Card card) {
		return frontImage.equals(card.getFrontImage());
	}
}
