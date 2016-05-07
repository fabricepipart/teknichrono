package org.trd.app.teknichrono.model;
// Generated 5 mai 2016 11:08:49 by Hibernate Tools 4.3.1.Final

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Date;
import org.trd.app.teknichrono.model.Event;
import javax.persistence.ManyToOne;

@Entity
@XmlRootElement
public class LapTime implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2438563507266191424L;

	private int id;
	private double timelap;

	@OneToOne
	private Pilot pilot;

	@Column
	private Date captureDate;

	@ManyToOne
	private Event event;

	public LapTime() {
	}

	public LapTime(int id, double timelap, int beacon) {
		this.id = id;
		this.timelap = timelap;
	}

	@Id
	@Column(name = "ID", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "TIMELAP", nullable = false, precision = 17, scale = 0)
	public double getTimelap() {
		return this.timelap;
	}

	public void setTimelap(double timelap) {
		this.timelap = timelap;
	}

	public Pilot getPilot() {
		return pilot;
	}

	public void setPilot(Pilot pilot) {
		this.pilot = pilot;
	}

	public Date getCaptureDate() {
		return captureDate;
	}

	public void setCaptureDate(Date captureDate) {
		this.captureDate = captureDate;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		result += "timelap: " + timelap;
		return result;
	}

	public Event getEvent() {
		return this.event;
	}

	public void setEvent(final Event event) {
		this.event = event;
	}

}
