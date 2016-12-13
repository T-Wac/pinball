package com.twac.pinball;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
	// 桌面的宽度
	private int tableWidth;
	// 桌面的高度
	private int tableHeight;
	// 球拍的垂直位置
	private int racketY;
	// 球拍的高度和宽度
	private final int RACKET_HEIGHT = 30;
	private final int RACKET_WIDTH = 150;
	// 小球的大小
	private final int BALL_SIZE = 30;
	// 小球纵向的运行速度
	private int ySpeed = 23;
	// 返回一个-0.5~0.5的比率，用于控制小球的运行方向
	Random rand = new Random();
	private double xyRate = rand.nextDouble() - 0.5;
	// 小球横向的运行速度
	private int xSpeed = (int) (ySpeed * xyRate * 2);
	// ballX和ballY代表小球的坐标
	private int ballX = rand.nextInt(200) + 20;
	private int ballY = rand.nextInt(10) + 20;
	// racketX代表球拍的水平位置
	private int racketX = rand.nextInt(200);
	// 游戏是否结束的旗标
	private boolean isLose = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 全屏显示
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 创建 GameView组件
		final GameView gameView = new GameView(this);
		setContentView(gameView);
		// 获取窗口管理器
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		// 获取屏幕的宽高
		tableWidth = metrics.widthPixels;
		tableHeight = metrics.heightPixels;
		racketY = tableHeight - 80;
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				if (msg.what == 0x123) {
					gameView.invalidate();
				}
			}
		};

		gameView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View source, int keyCode, KeyEvent event) {
				// 获取由哪个键触发的事件
				switch (event.getKeyCode()) {
				// 挡板左移
				case KeyEvent.KEYCODE_A:
					if (racketX > 0) {
						racketX -= 20;
					}
					break;
				// 挡板右移
				case KeyEvent.KEYCODE_D:
					if (racketX < tableWidth - RACKET_WIDTH) {
						racketX += 20;
					}
					break;
				}
				// 通知GameView组件重绘
				gameView.invalidate();
				return true;
			}
		});

		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// 如果小球碰到左边边框
				if (ballX <= 0 || ballX >= tableWidth - BALL_SIZE) {
					xSpeed = -xSpeed;
				}
				// 如果小球高度超出球拍位置，且横向不在球拍范围以内，游戏借宿
				if (ballY >= racketY - BALL_SIZE
						&& (ballX < racketX || ballX > racketX + RACKET_WIDTH)) {
					timer.cancel();
					// 设置游戏是否结束的旗标为true
					isLose = true;
				}
				// 如果小球位于球拍以内,且到达球拍位置,小球反弹
				else if (ballY <= 0
						|| (ballY >= racketY - BALL_SIZE && ballX > racketX && ballX <= racketX
								+ RACKET_WIDTH)) {
					ySpeed = -ySpeed;
				}
				// 小球坐标增加
				ballY += ySpeed;
				ballX += xSpeed;
				// 发送消息，通知系统重绘组件
				handler.sendEmptyMessage(0x123);
			}
		}, 0, 50);
	}

	class GameView extends View {
		private Paint paint = new Paint();

		public GameView(Context context) {
			super(context);
			setFocusable(true);
		}

		// 重写View的onDraw方法，实现绘画
		@Override
		protected void onDraw(Canvas canvas) {
			paint.setStyle(Paint.Style.FILL);
			// 设置去锯齿
			paint.setAntiAlias(true);
			// 如果游戏已经结束
			if (isLose) {
				paint.setColor(Color.RED);
				paint.setTextSize(40);
				canvas.drawText("游戏结束！！！！", 50, 200, paint);
			}
			// 如果游戏还没结束
			else {
				// 设置颜色，并绘制小球
				paint.setColor(Color.BLUE);
				canvas.drawCircle(ballX, ballY, BALL_SIZE, paint);
				// 设置颜色并绘制球拍
				paint.setColor(Color.rgb(80, 80, 200));
				canvas.drawRect(racketX, racketY, racketX + RACKET_WIDTH,
						racketY + RACKET_HEIGHT, paint);
			}

		}
	}
}
