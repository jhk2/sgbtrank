package com.jhk2.sgbtrank;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.jhk2.sgbtrank.SGBTRankApplication.Match;

public class RecordResultsActivity extends ActionBarActivity {
	private int m_year;
	private int m_month;
	private int m_day;
	
	public void setDate(int year, int month, int day) {
		m_year = year; m_month = month; m_day = day;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_results);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		m_rounds = new LinkedList<List<Match>>();
		currentRound = 1;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.record_results, menu);
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
	
	public void onDateClick(View view) {
		DatePickerDialog datepicker = new DatePickerDialog(this, new OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int month, int day) {
				// change the current date
				m_year = year; m_month = month; m_day = day;
				TextView datebox = (TextView) findViewById(R.id.datebox);
				datebox.setText(month + "/" + day + "/" + year);
			}
		}, m_year, m_month, m_day);
		datepicker.show();
	}
	
	public void onRoundDecrement(View view) {
		currentRound = Math.max(currentRound - 1, 1);
		TextView roundnumber = (TextView) findViewById(R.id.roundnumber);
		roundnumber.setText(Integer.valueOf(currentRound).toString());
	}
	
	public void onRoundIncrement(View view) {
		currentRound = Math.min(currentRound + 1, 10);
		TextView roundnumber = (TextView) findViewById(R.id.roundnumber);
		roundnumber.setText(Integer.valueOf(currentRound).toString());
	}
	
	// we want to keep track of the current active courts
	// which can be done by representing them as Match objects
	private List<List<Match>> m_rounds;
	private int currentRound;
	
	public void onAddCourtClick(View view) {
		// create a blank match and add it to the list
		@SuppressWarnings("deprecation")
		Date d = new Date(m_year, m_month, m_day);
		
		
		
		
		// TODO: ignoring all of the below, we want to populate the round list and court list somehow
		// when we first start with a completely empty list, adding a court should popular various things and add one new court
		// if we have a round going with some courts and increment the round number, we should probably carry over existing courts
		// also maybe give the option to clear all courts in a round
		// maybe prevent incrementing the round if the current one has no courts active
		// and also maybe prevent incrementing the round unless all current courts have scores finalized
		
		
		
		
		/*
		// first, populate the rounds up until the current round
		int roundIndex = currentRound - 1;
		while (m_rounds.size() < currentRound) {
			m_rounds.add(new LinkedList<Match>());
		}
		
		List<Match> curRoundList = m_rounds.get(roundIndex);
		
		
		Match mcourt = ((SGBTRankApplication) getApplication()).new Match(d);
		m_activeCourts.add(mcourt);
		mcourt.court = m_activeCourts.size();
		
		for (Match m : m_activeCourts) {
			m.maxcourts = m_activeCourts.size();
		}
		
		
		// also add it to the visible matches
		ViewGroup courts = (ViewGroup) findViewById(R.id.courtsummary);
		*/
		// let's use some layouts to organize the info
		
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_record_results,
					container, false);
			return rootView;
		}
		
		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			TextView datebox = (TextView) getView().findViewById(R.id.datebox);
			RecordResultsActivity activity = (RecordResultsActivity) getActivity();
			// set it to today's date at first
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			
			datebox.setText(month+1 + "/" + day + "/" + year);
			activity.setDate(year, month, day);
			
			TextView roundnumber = (TextView) getView().findViewById(R.id.roundnumber);
			roundnumber.setText("1");
		}
	}

}
