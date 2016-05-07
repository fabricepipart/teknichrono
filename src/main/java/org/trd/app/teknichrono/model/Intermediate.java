package org.trd.app.teknichrono.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;
import org.trd.app.teknichrono.model.Pilot;
import javax.persistence.ManyToOne;
import java.sql.Date;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
public class Intermediate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8542466638632122392L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@Version
	@Column(name = "version")
	private int version;

	@Column
	private double timeFromLast;

	@ManyToOne
	private Pilot pilot;

	@Column(nullable = false)
	private Date captureDate;

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Intermediate)) {
			return false;
		}
		Intermediate other = (Intermediate) obj;
		if (id != null) {
			if (!id.equals(other.id)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public double getTimeFromLast() {
		return timeFromLast;
	}

	public void setTimeFromLast(double timeFromLast) {
		this.timeFromLast = timeFromLast;
	}

	public Pilot getPilot() {
		return this.pilot;
	}

	public void setPilot(final Pilot pilot) {
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
		result += "timeFromLast: " + timeFromLast;
		return result;
	}
}