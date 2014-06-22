package com.jhk2.sgbtrank;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import com.jhk2.sgbtrank.SGBTRankApplication.Match;
import com.jhk2.sgbtrank.SGBTRankApplication.Player;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MatchHistoryActivity extends ActionBarActivity {

	// view the match history for a specified player
	public void buildMatchHistory(int id, ViewGroup parent) {
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
		List<Player> players = ((SGBTRankApplication) getApplication()).getPlayerList();
		Player myPlayer = players.get(id);
		int wins = 0;
		int losses = 0;
		
		// insert title first, text to be added later
		TextView title = new TextView(getApplicationContext());
		title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		title.setTextSize(20);
		title.setTextColor(android.graphics.Color.BLACK);
		parent.addView(title);
		
		for (int i = 0; i < myPlayer.getMatchHistory().size(); i++) {
			Match m = myPlayer.getMatchHistory().get(i);
			// print the match statistics from the current player's point of
			// view
			String text = "";
			// win or loss and partner/opponents
			int partnerID = -1;
			int opp1 = -1;
			int opp2 = -1;
			boolean winner = true;
			if (m.p0 == id || m.p1 == id) {
				opp1 = m.p2;
				opp2 = m.p3;
				int roundeddelo = (int) Math.round(m.delo0);
				text += "+" + roundeddelo + " W";
				wins++;
				winner = true;
				partnerID = (m.p0 == id) ? m.p1 : m.p0;
			} else if (m.p2 == id || m.p3 == id) {
				opp1 = m.p0;
				opp2 = m.p1;
				int roundeddelo = (int) Math.round(m.delo1);
				text += roundeddelo + " L";
				losses++;
				winner = false;
				partnerID = (m.p2 == id) ? m.p3 : m.p2;
			} else {
				Log.e("PartnerID", "Player ID doesn't appear in own match history");
			}
			Player partner = players.get(partnerID);
			Player opponent1 = players.get(opp1);
			Player opponent2 = players.get(opp2);
			// add the partner's name
			text += " w/" + partner.getAbbreviatedName();
			// versus opponents
			text += " vs " + opponent1.getAbbreviatedName() + ", " + opponent2.getAbbreviatedName();
			// score
			if (winner) {
				text += " " + m.g0 + "-" + m.g1;
				if (m.t0 > 0) {
					text += " (" + m.t0 + "-" + m.t1 + ")";
				}
			} else {
				text += " " + m.g1 + "-" + m.g0;
				if (m.t0 > 0) {
					text += " (" + m.t1 + "-" + m.t0 + ")";
				}
			}
			// round, court and date
			text += " " + dateFormat.format(m.date) + " R" + m.round + " C" + m.court + "/" + m.maxcourts;
			
			TextView t = new TextView(getApplicationContext());
			t.setText(text);
			t.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
			t.setTextSize(20);
			t.setTextColor(android.graphics.Color.BLACK);
			parent.addView(t);
		}
		final DecimalFormat twosig = new DecimalFormat("0.00");
		// now that we have W/L, set the title text
		title.setText(myPlayer.name + " " + twosig.format(myPlayer.elo) + " " + wins + "-" + losses);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_history);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.match_history_view, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_match_history, container, false);
			return rootView;
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			// load the match history from the specified player
			Bundle extras = getActivity().getIntent().getExtras();
			if (extras == null) {
				Log.e("MatchHistoryError", "Extras bundle was null");
			} else {
				ViewGroup parent = (ViewGroup) getView().findViewById(R.id.matchhistory);
				int playerID = extras.getInt("PlayerID");
				((MatchHistoryActivity) getActivity()).buildMatchHistory(playerID, parent);
			}
		}
	}

}
