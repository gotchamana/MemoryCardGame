package memoryCardGame;

import static memoryCardGame.GameState.*;
import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import java.util.*;
import javafx.application.Platform;

/**
 * The core of game.
 */
public class GameBoard extends Pane {

	private TilePane container;
	private Button start, contPause;
	private Text time;
	private GameTimer timer;
	private Image[] frontImages;
	private Image backImage;
	private List<Card> cards;
	private Card preSelectedCard;

	private ObjectProperty<GameState> gameStateProperty;
	private IntegerProperty totalFrontCardNumberProperty;

	private int selectedCount;

	/**
	 * Create a GameBoard.
	 */
	public GameBoard() {
		start = new Button("Start");
		start.setFocusTraversable(false);
		start.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		start.setOnAction(e -> {
			gameStateProperty.set(gameStateProperty.get() == GAME_START ? GAME_RUN : GAME_START);
		});

		contPause = new Button("Cont/Pause");
		contPause.setFocusTraversable(false);
		contPause.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		contPause.setOnAction(e -> {
			if (gameStateProperty.get() == GAME_PAUSE) {
				gameStateProperty.set(GAME_RUN);
			} else if (gameStateProperty.get() == GAME_RUN){
				gameStateProperty.set(GAME_PAUSE);
			}
		});

		container = new TilePane(Orientation.HORIZONTAL, 30.0, 0.0, start, contPause);
		container.setAlignment(Pos.CENTER);
		container.setPrefColumns(2);
		container.setPrefRows(1);
		getChildren().add(container);
		container.relocate(43, 400);

		time = new Text("00:00");
		time.getStyleClass().add("time");
		getChildren().add(time);
		time.relocate(160, 40);

		timer = new GameTimer();
		timer.secProperty().addListener((obs, oldValue, newValue) -> {
			long minute = newValue.longValue() / 60;
			long second = newValue.longValue() % 60;
			time.setText(String.format("%02d:%02d", minute, second));
		});

		frontImages = loadImages();
		backImage = new Image(getClass().getResourceAsStream("/img/card0.png"));
		cards = createCards();

		// Put cards on the scene
		for (int i = 0; i < cards.size(); i++) {
			Card card = cards.get(i);

			getChildren().add(card);
			// 排成4x4的形式
			card.relocate(70 + (i % 4 * 70), 80 + (i / 4 * 70));
		}

		totalFrontCardNumberProperty = new SimpleIntegerProperty(0);
		totalFrontCardNumberProperty.addListener((obs, oldValue, newValue) -> {
			if (newValue.intValue() >= cards.size()) {
				// 當所有的卡牌都被掀開時即代表遊戲結束
				gameStateProperty.set(GAME_OVER);
			}
		});

		gameStateProperty = new SimpleObjectProperty<>();
		gameStateProperty.addListener((obs, oldValue, newValue) -> {
			switch (newValue) {
				case GAME_START: 
					// 將正在翻轉的卡牌中斷
					cards.stream().filter(c -> c.getIsFlipping()).forEach(c -> c.stopFlipping());

					// 將所有翻到正面的牌翻回背面
					cards.stream().filter(c -> c.getIsFront()).forEach(c -> c.flip());

					// 重新洗牌
					Collections.shuffle(Arrays.asList(frontImages));
					for (int i = 0; i < frontImages.length; i++) {
						cards.get(i).setFrontImage(frontImages[i]);
					}

					// 將記錄所有正面的牌的數目重新歸零
					totalFrontCardNumberProperty.set(0);

					preSelectedCard = null;
					selectedCount = 0;

					start.setText("Start");
					contPause.setDisable(true);

					// 計時器歸零
					timer.stop();
					break;

				case GAME_RUN:
					start.setText("Restart");
					contPause.setDisable(false);

					// 啟動計時器
					timer.start();
					break;

				case GAME_PAUSE:
					// 暫停計時器
					timer.pause();
					break;

				case GAME_OVER:
					contPause.setDisable(true);
					timer.pause();
					break;
			}
		});
		gameStateProperty.set(GAME_START);
	}

	private Image[] loadImages() {
		Image[] images = new Image[16];
		for (int i = 0, j = images.length - 1; i < images.length / 2; i++, j--) {
			images[i] = new Image(getClass().getResourceAsStream("/img/card" + (i + 1) + ".png"));
			images[j] = images[i];
		}

		return images;
	}

	private List<Card> createCards() {
		List<Card> cards = new ArrayList<>();

		for (int i = 0; i < frontImages.length; i++) {
			Card card = new Card(frontImages[i], backImage);
			card.setOnMouseClicked(e -> {
				// 當玩家點擊卡片時即播放翻牌動畫，但是播放動畫的條件為: 1.遊戲進行中 2.掀開的卡牌次數要在2以下 3.翻牌動畫結束後 4.卡牌處於背面時
				if (gameStateProperty.get() == GAME_RUN && selectedCount < 2 && !card.getIsFlipping() && !card.getIsFront()) {
					card.flip();
					selectedCount++;
				}
			});
			card.frontProperty().addListener((obs, oldValue, newValue) -> {
				// 當卡牌被掀開時執行以下敘述
				if (newValue.booleanValue()) {
					// 檢查此卡片是否為第一張掀開的卡片
					if (preSelectedCard == null) {
						preSelectedCard = card;
					} else if (card.equals(preSelectedCard)) {
						preSelectedCard = null;
						selectedCount = 0;

						// 將翻開卡牌的總數加2
						totalFrontCardNumberProperty.set(totalFrontCardNumberProperty.get() + 2);
					} else {
						// 如果兩張卡牌不一樣則翻回去
						preSelectedCard.flip();
						Platform.runLater(() -> {
							card.flip();
						});

						preSelectedCard = null;
						selectedCount = 0;
					}
				}
			});
			cards.add(card);
		}

		return cards;
	}
}
