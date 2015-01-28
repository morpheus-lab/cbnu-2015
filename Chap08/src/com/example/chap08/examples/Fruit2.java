package com.example.chap08.examples;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import com.example.chap08.*;

public class Fruit2 extends ActionBarActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fruit2);
	}

	public void mOnClick(View v) {
		TextView textFruit=(TextView)findViewById(R.id.fruit);
		switch (v.getId()) {
		case R.id.apple:
			textFruit.setText("Apple");
			break;
		case R.id.orange:
			textFruit.setText("Orange");
			break;
		}
	}
}