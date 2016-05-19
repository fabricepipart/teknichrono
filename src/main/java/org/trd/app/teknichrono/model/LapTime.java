package org.trd.app.teknichrono.model;
// Generated 5 mai 2016 11:08:49 by Hibernate Tools 4.3.1.Final

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
public class LapTime implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2438563507266191424L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, nullable = false)
	private int id;

	@OneToOne(fetch = FetchType.LAZY)
	private Pilot pilot;

	@Column
	private Date startDate;

	@ManyToOne(fetch = FetchType.LAZY)
	private Event event;

	@Column
	private Date endDate;

	@OneToMany
	@OrderColumn(name = "captureDate")
	private List<Intermediate> intermediates = new ArrayList<Intermediate>();

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Pilot getPilot() {
		return pilot;
	}

	public void setPilot(Pilot pilot) {
		this.pilot = pilot;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date captureDate) {
		this.startDate = captureDate;
	}

	public Event getEvent() {
		return this.event;
	}

	public void setEvent(final Event event) {
		this.event = event;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		result += "id: " + id;
		return result;
	}

	public List<Intermediate> getIntermediates() {
		return this.intermediates;
	}

	public void setIntermediates(final List<Intermediate> intermediates) {
		this.intermediates = intermediates;
	}

}
