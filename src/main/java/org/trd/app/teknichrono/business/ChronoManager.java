package org.trd.app.teknichrono.business;

import org.trd.app.teknichrono.model.Beacon;
import org.trd.app.teknichrono.model.Chronometer;
import org.trd.app.teknichrono.model.Event;
import org.trd.app.teknichrono.model.Pilot;
import org.trd.app.teknichrono.model.Ping;

public class ChronoManager {

	/**
	 * @param ping
	 */
	public void addPing(Ping ping) {
		Beacon beacon = getBeacon(ping);
		Pilot pilot = getPilot(ping);
		Chronometer chronometer = getChronometer(ping);
		Event event = getEvent(ping);

		// Missing intermediate of previous existing Laptime

		// New intermediate of last Laptime

		// New Laptime / Finish laptime
		// The chronopoint is the last one of the event

		// New Laptime but not first intermediate
	}

	private Event getEvent(Ping ping) {
		// TODO Auto-generated method stub
		return null;
	}

	private Chronometer getChronometer(Ping ping) {
		// TODO Auto-generated method stub
		return null;
	}

	private Pilot getPilot(Ping ping) {
		// TODO Auto-generated method stub
		return null;
	}

	private Beacon getBeacon(Ping ping) {
		// TODO Auto-generated method stub
		return null;
	}

}
