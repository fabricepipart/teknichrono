package org.trd.app.teknichrono.business;

import javax.inject.Inject;

import org.trd.app.teknichrono.model.Beacon;
import org.trd.app.teknichrono.rest.BeaconEndpoint;

public class BeaconManager {

	@Inject
	protected BeaconEndpoint beaconManager;

	public Beacon findBeaconNumber(int number) {
		return beaconManager.findBeacon(number);
	}
}
