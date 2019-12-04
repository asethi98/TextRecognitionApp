package com.example.ocr_app.AccountActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.ocr_app.NavBarActivity;
import com.example.ocr_app.R;

public class LoginActivity extends AppCompatActivity {
	private EditText username;
	private EditText password;
	private Button loginButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		username = (EditText) findViewById(R.id.etUsername);
		password = (EditText) findViewById(R.id.etPassword);
		loginButton = (Button) findViewById(R.id.loginButton);
	}

	public void login(View view) {
		Intent intent = new Intent(this, NavBarActivity.class);
		startActivity(intent);
	}
}
