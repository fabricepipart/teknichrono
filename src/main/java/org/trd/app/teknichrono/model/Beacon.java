package org.trd.app.teknichrono.model;
// Generated 5 mai 2016 11:08:49 by Hibernate Tools 4.3.1.Final

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@XmlRootElement
public class Beacon implements java.io.Serializable {

	/* =========================== Entity stuff =========================== */

	/**
	 * 
	 */
	private static final long serialVersionUID = -2438563507266191424L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, nullable = false)
	private int id;

	@Version
	@Column(name = "version")
	private int version;

	/* =============================== Fields =============================== */

	@Column(nullable = false, unique = true)
	private int number;

	// Mapped by denotes that Pilot is the owner of the relationship
	// http://meri-stuff.blogspot.fr/2012/03/jpa-tutorial.html#RelationshipsBidirectionalOneToManyManyToOneConsistency
	@OneToOne(fetch = FetchType.EAGER, optional = true, mappedBy = "currentBeacon", cascade = CascadeType.MERGE)
	@JsonBackReference(value = "pilot-beacon")
	private Pilot pilot;

	// Can be null if after event, items are reassociated
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "beacon")
	@JsonIgnore
	private List<Ping> pings = new ArrayList<Ping>();

	/* ============================ Factory ============================ */

	public Beacon() {
	}

	public Beacon(int id, int number) {
		this.id = id;
		this.number = number;
	}

	@JsonCreator
	public static Beacon create(int id) {
		Beacon b = new Beacon();
		b.setId(id);
		return b;
	}

	/* ===================== Getters and setters ======================== */

	public Pilot getPilot() {
		return pilot;
	}

	public void setPilot(Pilot pilot) {
		// prevent endless loop
		if (sameAsFormer(pilot)) {
			return;
		}
		Pilot oldPilot = this.pilot;
		// Set new pilot
		this.pilot = pilot;
		// This beacon is not associated to the previous Pilot
		if (oldPilot != null) {
			oldPilot.setCurrentBeacon(null);
		}
		// Set reverse relationship
		if (pilot != null) {
			pilot.setCurrentBeacon(this);
		}
	}

	private boolean sameAsFormer(Pilot newPilot) {
		return pilot == null ? newPilot == null : pilot.equals(newPilot);
	}

	public List<Ping> getPings() {
		return pings;
	}

	public void setPings(List<Ping> pings) {
		this.pings = pings;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

	public int getNumber() {
		return this.number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		result += "number: " + number;
		return result;
	}

}
