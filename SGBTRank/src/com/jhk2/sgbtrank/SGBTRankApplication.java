package com.jhk2.sgbtrank;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

public class SGBTRankApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		m_players = new ArrayList<Player>();
		m_matches = new ArrayList<Match>();
		
//		try {
//			File f = new File(Environment.getExternalStorageDirectory()
//					+ "/.sgbt/data.gbt");
//			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
//			out.write("players:"); out.newLine();
//			out.write("Justin Kim"); out.newLine();
//			out.write("Isaac Yi"); out.newLine();
//			out.write("Jong-Suk Yoo"); out.newLine();
//			out.write("Lawrence Kim"); out.newLine();
//			out.write("matches:"); out.newLine();
//			out.write("2014/6/2;1;1/3;0,1d2,3;6-4");
//			out.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		refreshData();
	}
	
	public void refreshData() {
		m_players.clear();
		m_matches.clear();
		loadData();
		calculateElo();
		// can't sort here because daily results depend on players being in ID order
		//Collections.sort(m_players);
	}
	
	private static final int STARTING_ELO = 2000;
	
	public class Player implements Comparable<Player> {
		public String name;
		public int id;
		public double elo;
		private ArrayList<Match> history;
		public Player(String name, int id) {
			this.name = name; this.id = id;
			history = new ArrayList<Match>();
			elo = STARTING_ELO;
		}
		public ArrayList<Match> getMatchHistory() { return history; }
		public String getAbbreviatedName() {
			// first split first/last names
			String[] names = name.split(" ");
			String abbrev = "";
			if (names[0].contains("-")) {
				// we have a hyphenated name, so we need both first letters of the hyphenated stuff
				String[] hyphenated = names[0].split("-");
				abbrev += "" + hyphenated[0].charAt(0) + hyphenated[1].charAt(0);
			} else {
				// just get the first initial
				abbrev += names[0].charAt(0);
			}
			abbrev += " " + names[1];
			// use parenthesis after the first name to handle duplicates
			if (names[0].contains("(")) {
				abbrev += names[0].substring(names[0].length()-3, names[0].length());
			}
			return abbrev;
		}
		
		@Override
		public int compareTo(Player arg0) {
			double diff = elo - arg0.elo;
			if (diff > 0) {
				return -1;
			} else if (diff < 0) {
				return 1;
			} else {
				return 0;
			}
		}
	}
	private ArrayList<Player> m_players;
	public List<Player> getPlayerList() { return m_players; }
	
	// struct for matches
	public class Match {
		// p0-p3 is the four players 0 and 1 are on a team, 0 and 2 are on a team
		// g0 and g1 are the game score, t0 and t1 are tiebreak scores, if applicable
		// p0 and p1 are always the winning team
		public int p0, p1, p2, p3, g0, g1, t0, t1;
		public Date date;
		public int round;
		public int court, maxcourts;
		public double delo0, delo1;
		public Match(Date date, int round, int court, int maxcourts, int p0, int p1, int p2, int p3, int g0, int g1, int t0, int t1) {
			this.p0 = p0; this.p1 = p1; this.p2 = p2; this.p3 = p3; this.g0 = g0; this.g1 = g1; this.t0 = t0; this.t1 = t1;
			this.date = date;
			this.round = round;
			this.court = court; this.maxcourts = maxcourts;
		}
		public Match(Date date) {
			p0 = p1 = p2 = p3 = g0 = g1 = t0 = t1 = round = court = maxcourts = -1;
			this.date = date;
		}
		public String getSummary(List<Player> players) {
			final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
			return dateFormat.format(date) + " R" + round + " C" + court + "/" + maxcourts + " "
					+ players.get(p0).getAbbreviatedName() + ", " + players.get(p1).getAbbreviatedName() + " d. "
					+ players.get(p2).getAbbreviatedName() + ", " + players.get(p3).getAbbreviatedName() + " "
					+ g0 + "-" + g1;
		}
	}
	
	private ArrayList<Match> m_matches;
	public List<Match> getMatchList() { return m_matches; }
	
	private void calculateElo() {
		// for each match, we want to update the elo ratings for each affected player
		// the first item in m_matches is the most recent match, so go backwards
		for (int i = m_matches.size() - 1; i >= 0; i--) {
			Match m = m_matches.get(i);
			processMatch(m);
		}
	}
	
	private static final int ELO_K = 32;
	
	public void processMatch(Match m) {
		Player p0 = m_players.get(m.p0);
		Player p1 = m_players.get(m.p1);
		Player p2 = m_players.get(m.p2);
		Player p3 = m_players.get(m.p3);
		
		// calculate the combined ratings for both teams
		double t0rating = p0.elo * 0.5 + p1.elo * 0.5;
		double t1rating = p2.elo * 0.5 + p3.elo * 0.5;
		
		// calculate expected outcomes
		double Q0 = Math.pow(10, t0rating / 400);
		double Q1 = Math.pow(10, t1rating / 400);
		double t0expected = Q0 / (Q0 + Q1);
		double t1expected = Q1 / (Q0 + Q1);
		
		// calculate the new ratings
		m.delo0 = ELO_K * (1.0 - t0expected);
		m.delo1 = ELO_K * -t1expected;
		p0.elo += m.delo0; p1.elo += m.delo0;
		p2.elo += m.delo1; p3.elo += m.delo1;
	}

	private void loadData() {
		// check if a directory exists
		File dir = new File(Environment.getExternalStorageDirectory()
				+ "/.sgbt");
		if (!dir.exists()) {
			// then we create a new folder for sgbt files
			dir.mkdir();
		}
		// now check if the data file exists
		File data = new File(Environment.getExternalStorageDirectory()
				+ "/.sgbt/data.gbt");
		if (!data.exists()) {
			// if not, create an empty one
			try {
				data.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// otherwise, load its contents
			try {
				BufferedReader br = new BufferedReader(new FileReader(data));
				String line;
				int mode = 0; // 0 is header mode, 1 is player mode, 2 is
								// matches mode
				int playerCount = 0;
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);

				while ((line = br.readLine()) != null) {
					// check if it is a header "players:" or "matches:"
					if (line.equals("players:")) {
						// we are in player parsing mode
						mode = 1;
						continue;
					} else if (line.equals("matches:")) {
						// we are parsing match history
						mode = 2;
						continue;
					}
					switch (mode) {
					case 1:
						// add to the list of players
						//Log.i("addplayer", m_players.size() + " : " + line);
						m_players.add(new Player(line, playerCount));
						playerCount++;
						break;
					case 2:
						// try to parse a match
						// example line: 2014/6/2;2;2/4;1,2d0,3;7-6(11-9)
						// this would mean that on this date, round 2, court 2 of 4, players 1 and 2 beat 0 and 3 with score 7-6(9)
						String[] fields = line.split(";");
						// first field is the date
						Date date = dateFormat.parse(fields[0]);
						// second field is the round
						int round = Integer.parseInt(fields[1]);
						// third field is the court data
						int court = Integer.parseInt(fields[2].substring(0,1));
						int maxcourts = Integer.parseInt(fields[2].substring(2,3));
						// fourth field is the match result players
						// teams separated by d, teammates separated by commas
						String[] teams = fields[3].split("d");
						String[] team0 = teams[0].split(",");
						String[] team1 = teams[1].split(",");
						int p0 = Integer.parseInt(team0[0]);
						int p1 = Integer.parseInt(team0[1]);
						int p2 = Integer.parseInt(team1[0]);
						int p3 = Integer.parseInt(team1[1]);
						// int p0 = Integer.parseInt(fields[3].substring(0,1));
						// int p1 = Integer.parseInt(fields[3].substring(2,3));
						// int p2 = Integer.parseInt(fields[3].substring(4,5));
						// int p3 = Integer.parseInt(fields[3].substring(6,7));
						// fifth field is the score
						int g0 = Integer.parseInt(fields[4].substring(0,1));
						int g1 = Integer.parseInt(fields[4].substring(2,3));
						int t0 = 0, t1 = 0;
						if (fields[4].length() > 3) {
							// it's a tiebreak
							assert(fields[4].contains("(") && fields[4].contains(")")); // must have parenthesis for the tiebreak score
							String[] tiebreak = fields[4].substring(3).split("-");
							t0 = Integer.parseInt(tiebreak[0].substring(1));
							t1 = Integer.parseInt(tiebreak[1].substring(0,tiebreak[1].length()-1));
						}
						// now put it all in a match object
						Match m = new Match(date, round, court, maxcourts, p0, p1, p2, p3, g0, g1, t0, t1);
						//Log.i("newmatch", p0 + ", " + p1 + ", " + p2 + ", " + p3);
						//Log.i("addmatch", m.getSummary(m_players));
						// add it to the overall match history
						m_matches.add(m);
						// add it to the match history for each player
						m_players.get(p0).getMatchHistory().add(m);
						m_players.get(p1).getMatchHistory().add(m);
						m_players.get(p2).getMatchHistory().add(m);
						m_players.get(p3).getMatchHistory().add(m);
						break;
					default:
						Log.e("sgbt", "tried to read an unidentifiable line, mode: " + mode + ", line: '" + line + "'");
						break;
					}
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void saveData() {
		// we assume that directory and file exist
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(openFileOutput(Environment.getExternalStorageDirectory()
				+ "/.sgbt/data.gbt", 2)));
			// first write the player data
			out.write("players:"); out.newLine();
			// player list is in elo rank order, and we need to print them in order of id
			Player[] orderplayers = new Player[m_players.size()];
			for (Player p : m_players) {
				orderplayers[p.id] = p;
			}
			for (int i = 0; i < orderplayers.length; i++) {
				out.write(orderplayers[i].name); out.newLine();
			}
			// now write the match data
			for (int i = 0; i < m_matches.size(); i++) {
				Match m = m_matches.get(i);
				out.write(m.date + ";" + m.round + ";" + m.p0 + "," + m.p1 + "d" + m.p2 + "," + m.p3 + ";" + m.g0 + "-" + m.g1);
				if (m.t0 > 0) {
					out.write("(" + m.t0 + "-" + m.t1 + ")");
				}
				out.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
