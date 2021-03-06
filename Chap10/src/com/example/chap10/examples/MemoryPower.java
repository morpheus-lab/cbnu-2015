package com.example.chap10.examples;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;

// 액티비티 : 게임 뷰를 담는 껍데기.
public class MemoryPower extends ActionBarActivity {
	GameView gameView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gameView = new GameView(this);
		setContentView(gameView);
	}
	
	//도형 하나에 대한 정보
	class Shape {
		final static int RECT = 0;
		final static int CIRCLE = 1;
		final static int TRIANGLE = 2;

		int what;
		int color;
		Rect rect;
	}

	// 게임 뷰 : 실질적인 메인이다.
	class GameView extends View {
		// 그리기 모드. 빈화면 또는 도형 출력
		final static int BLANK = 0;
		final static int PLAY = 1;
		// 게임 진행 속도
		final static int DELAY = 1500;
		// 현재 그리기 모드
		int status;
		// 생성된 도형의 목록
		ArrayList<Shape> shapeList = new ArrayList<Shape>();
		Random rnd = new Random();
		Activity parentActivity;

		public GameView(Context context) {
			super(context);
			parentActivity = (Activity)context;
			// 빈 화면으로 시작하고 잠시 후에 게임 시작
			status = BLANK;
			handler.sendEmptyMessageDelayed(0,DELAY);
		}

		public void onDraw(Canvas canvas) {
			// 검은색 배경으로 지운다. 빈 화면이면 지우기만 하고 리턴 
			canvas.drawColor(Color.BLACK);
			if (status == BLANK) {
				return;
			}

			// 도형 목록을 순회하면서 도형 정보대로 출력한다.
			int idx;
			for (idx = 0; idx < shapeList.size(); idx ++) {
				Paint paint = new Paint();
				paint.setAntiAlias(true);
				paint.setColor(shapeList.get(idx).color);

				Rect rect = shapeList.get(idx).rect;
				switch (shapeList.get(idx).what) {
				case Shape.RECT:
					canvas.drawRect(rect, paint);
					break;
				case Shape.CIRCLE:
					canvas.drawCircle(rect.left + rect.width()/2, rect.top + rect.height()/2,
							rect.width()/2, paint);
					break;
				case Shape.TRIANGLE:
					Path path = new Path();
					path.moveTo(rect.left + rect.width()/2, rect.top);
					path.lineTo(rect.left, rect.bottom);
					path.lineTo(rect.right, rect.bottom);
					canvas.drawPath(path, paint);
					break;
				}
			}
		}

		public boolean onTouchEvent(MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				int sel;
				sel = findShapeIdx((int)event.getX(), (int)event.getY());

				// 빈 바닥을 찍었으면 무시한다.
				if (sel == -1) {
					return true;
				}

				// 마지막 추가된 도형을 제대로 찍었으면 다음 단계로 진행. 
				// 빈 화면 잠시 보여준 후 새 도형 추가
				if (sel == shapeList.size()-1) {
					status = BLANK;
					invalidate();
					handler.sendEmptyMessageDelayed(0,DELAY);
					// 엉뚱한 도형을 찍었으면 질문 후 게임 종료 또는 재시작
				} else {
					new AlertDialog.Builder(getContext())
					.setMessage("재미있지! 또 할래?")
					.setTitle("게임 끝")
					.setPositiveButton("함더", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							shapeList.clear();
							status = BLANK;
							invalidate();
							handler.sendEmptyMessageDelayed(0,DELAY);
						}
					})
					.setNegativeButton("안해", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							parentActivity.finish();
						}
					})
					.show();
				}
				return true;
			}
			return false;
		}

		// 새로운 도형을 목록에 추가한다.
		void addNewShape() {
			Shape shape = new Shape();
			int idx;
			boolean findIntersect;
			Rect rect = new Rect();

			// 기존 도형과 겹치지 않는 새 위치를 찾는다.
			for (;;) {
				// 크기는 32, 48, 64 중 하나 선택
				int Size = 32 + 16 * rnd.nextInt(3);

				// 위치는 난수로 선택
				rect.left = rnd.nextInt(getWidth());
				rect.top = rnd.nextInt(getHeight());
				rect.right = rect.left + Size;
				rect.bottom = rect.top + Size;

				// 화면을 벗어나면 안된다.
				if (rect.right > getWidth() || rect.bottom > getHeight()) {
					continue;
				}

				// 기존 도형 순회하며 겹치는지 조사한다.
				findIntersect = false;
				for (idx = 0; idx < shapeList.size(); idx ++) {
					if (rect.intersect(shapeList.get(idx).rect) == true) {
						findIntersect = true;
					}
				}

				// 겹치지 않을 때 확정한다. 겹치면 계속 새 위치 선정한다.
				if (findIntersect == false) {
					break;
				}
			}

			// 새 도형 정보 작성. 모양, 색상 등을 난수 선택한다.
			shape.what = rnd.nextInt(3);

			switch (rnd.nextInt(5)) {
			case 0:
				shape.color = Color.WHITE;
				break;
			case 1:
				shape.color = Color.RED;
				break;
			case 2:
				shape.color = Color.GREEN;
				break;
			case 3:
				shape.color = Color.BLUE;
				break;
			case 4:
				shape.color = Color.YELLOW;
				break;
			}

			shape.rect = rect;
			shapeList.add(shape);
		}

		// x, y 위치의 도형 번호를 찾는다. 도형이 없는 위치면 -1 리턴
		int findShapeIdx(int x, int y) {
			int idx;

			for (idx = 0; idx < shapeList.size(); idx ++) {
				if (shapeList.get(idx).rect.contains(x, y)) {
					return idx;
				}
			}
			return -1;
		}

		// 새 도형을 추가하고 화면을 다시 그린다. 딜레이를 주기 위해 핸들러를 사용했다.
		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				addNewShape();
				status = PLAY;
				invalidate();

				String title;
				title = "MemoryPower - " + shapeList.size() + " 단계";
				parentActivity.setTitle(title);
			}
		};
	}
}
