package com.example.ocr_app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

public class NavBarActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
	private DrawerLayout drawer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navbar);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		drawer = findViewById(R.id.drawer_layout);

		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);

		NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		toggle.syncState();

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
					new HomeFragment()).commit();
			navigationView.setCheckedItem(R.id.nav_home);
		}

	}

	public void launchDTFragment() {
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
				new TranslateTextFragment()).commit();
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
		Log.e("STRING", "" + menuItem.toString());
		switch (menuItem.getItemId()) {
			case R.id.nav_home:
				getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
						new HomeFragment()).commit();
				break;
			case R.id.nav_savedTranslations:
				getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
						new SavedTranslationsFragment()).commit();
				break;
		}
		drawer.closeDrawer(GravityCompat.START);
		// select item after tapping
		return true;
	}

	@Override
	public void onBackPressed() {
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}
}
