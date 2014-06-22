package com.jhk2.sgbtrank;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jhk2.sgbtrank.SGBTRankApplication.Match;
import com.jhk2.sgbtrank.SGBTRankApplication.Player;

public class DailyResultsActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_daily_results);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	
	
	private void buildMatchEntries(ViewGroup parent) {
		// make a text box for each match
		List<Match> matches = ((SGBTRankApplication) getApplication()).getMatchList();
		List<Player> players = ((SGBTRankApplication) getApplication()).getPlayerList();
		
		for (int i = 0; i < matches.size(); i++) {
			Match m = matches.get(i);
			String text = m.getSummary(players);
			if (m.t0 > 0) {
				text += " (" + m.t0 + "-" + m.t1 + ")";
			}
			//HorizontalScrollView h = new HorizontalScrollView(getApplicationContext());
			//h.setHorizontalScrollBarEnabled(false);
			TextView t = new TextView(getApplicationContext());
			t.setText(text);
			t.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
			t.setTextSize(20);
			t.setTextColor(android.graphics.Color.BLACK);
			parent.addView(t);
			//h.addView(t);
			//parent.addView(h);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.daily_results, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_daily_results, container, false);
			return rootView;
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			LinearLayout llayout = (LinearLayout) getView().findViewById(R.id.dailyview);
			((DailyResultsActivity) getActivity()).buildMatchEntries(llayout);
		}
		
		
	}

}
