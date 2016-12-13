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
	// ����Ŀ��
	private int tableWidth;
	// ����ĸ߶�
	private int tableHeight;
	// ���ĵĴ�ֱλ��
	private int racketY;
	// ���ĵĸ߶ȺͿ��
	private final int RACKET_HEIGHT = 30;
	private final int RACKET_WIDTH = 150;
	// С��Ĵ�С
	private final int BALL_SIZE = 30;
	// С������������ٶ�
	private int ySpeed = 23;
	// ����һ��-0.5~0.5�ı��ʣ����ڿ���С������з���
	Random rand = new Random();
	private double xyRate = rand.nextDouble() - 0.5;
	// С�����������ٶ�
	private int xSpeed = (int) (ySpeed * xyRate * 2);
	// ballX��ballY����С�������
	private int ballX = rand.nextInt(200) + 20;
	private int ballY = rand.nextInt(10) + 20;
	// racketX�������ĵ�ˮƽλ��
	private int racketX = rand.nextInt(200);
	// ��Ϸ�Ƿ���������
	private boolean isLose = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ȫ����ʾ
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ���� GameView���
		final GameView gameView = new GameView(this);
		setContentView(gameView);
		// ��ȡ���ڹ�����
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		// ��ȡ��Ļ�Ŀ��
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
				// ��ȡ���ĸ����������¼�
				switch (event.getKeyCode()) {
				// ��������
				case KeyEvent.KEYCODE_A:
					if (racketX > 0) {
						racketX -= 20;
					}
					break;
				// ��������
				case KeyEvent.KEYCODE_D:
					if (racketX < tableWidth - RACKET_WIDTH) {
						racketX += 20;
					}
					break;
				}
				// ֪ͨGameView����ػ�
				gameView.invalidate();
				return true;
			}
		});

		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// ���С��������߱߿�
				if (ballX <= 0 || ballX >= tableWidth - BALL_SIZE) {
					xSpeed = -xSpeed;
				}
				// ���С��߶ȳ�������λ�ã��Һ��������ķ�Χ���ڣ���Ϸ����
				if (ballY >= racketY - BALL_SIZE
						&& (ballX < racketX || ballX > racketX + RACKET_WIDTH)) {
					timer.cancel();
					// ������Ϸ�Ƿ���������Ϊtrue
					isLose = true;
				}
				// ���С��λ����������,�ҵ�������λ��,С�򷴵�
				else if (ballY <= 0
						|| (ballY >= racketY - BALL_SIZE && ballX > racketX && ballX <= racketX
								+ RACKET_WIDTH)) {
					ySpeed = -ySpeed;
				}
				// С����������
				ballY += ySpeed;
				ballX += xSpeed;
				// ������Ϣ��֪ͨϵͳ�ػ����
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

		// ��дView��onDraw������ʵ�ֻ滭
		@Override
		protected void onDraw(Canvas canvas) {
			paint.setStyle(Paint.Style.FILL);
			// ����ȥ���
			paint.setAntiAlias(true);
			// �����Ϸ�Ѿ�����
			if (isLose) {
				paint.setColor(Color.RED);
				paint.setTextSize(40);
				canvas.drawText("��Ϸ������������", 50, 200, paint);
			}
			// �����Ϸ��û����
			else {
				// ������ɫ��������С��
				paint.setColor(Color.BLUE);
				canvas.drawCircle(ballX, ballY, BALL_SIZE, paint);
				// ������ɫ����������
				paint.setColor(Color.rgb(80, 80, 200));
				canvas.drawRect(racketX, racketY, racketX + RACKET_WIDTH,
						racketY + RACKET_HEIGHT, paint);
			}

		}
	}
}
