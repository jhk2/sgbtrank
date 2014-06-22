package com.jhk2.sgbtrank;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.jhk2.sgbtrank.SGBTRankApplication.Player;

public class OverallRanksActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_overall_ranks);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.overall_ranks, menu);
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
	
	private void addTableEntry(TableRow row, String s) {
		TextView cell = new TextView(getApplicationContext());
		cell.setText(s);
		cell.setTextSize(20);
		cell.setTextColor(android.graphics.Color.BLACK);
		//cell.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
		row.addView(cell);
	}
	
	private void buildRankTable(TableLayout tlayout) {
		// make a sorted list by descending elo, deep copy
		List<Player> players = new ArrayList<Player>();
		players.addAll(((SGBTRankApplication) getApplication()).getPlayerList());
		Collections.sort(players);
		// players should be ordered by descending elo
		Integer currank = 0;
		double curelo = 0.0;
		DecimalFormat twosig = new DecimalFormat("0.00");
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			TableRow row = new TableRow(getApplicationContext());
			row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
			row.setWeightSum(1.0f);
			row.setPadding(10, 10, 10, 10);
			// we want rank, name, and elo in columns
			// update rank temporary variables
			if (p.elo != curelo) {
				currank = i+1;
				curelo = p.elo;
			}
			addTableEntry(row, currank.toString());
			addTableEntry(row, p.name);
			addTableEntry(row, twosig.format(curelo));
			// make a listener that launches an individual history view on press
			row.setOnClickListener(new OnClickListener() {
				private int m_playerID;
				@Override
				public void onClick(View view) {
					// launch a match history activity
					Intent intent = new Intent(OverallRanksActivity.this, MatchHistoryActivity.class);
					// give the activity the player ID of the one that was clicked
					intent.putExtra("PlayerID", m_playerID);
					startActivity(intent);
				}
				private OnClickListener initPlayer(int id) {
					m_playerID = id;
					return this;
				}
				
			}.initPlayer(p.id));
			/*
			row.setOnTouchListener( new OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent event) {
					TableRow t = (TableRow) view;
					switch(event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						t.setTextColor(Color.GREEN);
						break;
					case MotionEvent.ACTION_CANCEL:
					case MotionEvent.ACTION_UP:
						t.setTextColor(Color.BLACK);
						break;
					}
					return false;
				}
			});
			*/
			tlayout.addView(row);
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_overall_ranks, container, false);
			return rootView;
		}
		
		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			TableLayout tlayout = (TableLayout) getView().findViewById(R.id.rankview);
			((OverallRanksActivity) getActivity()).buildRankTable(tlayout);
		}
	}

}
