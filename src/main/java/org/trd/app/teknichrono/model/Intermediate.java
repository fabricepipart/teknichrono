package org.trd.app.teknichrono.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
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

	@ManyToOne
	private Pilot pilot;

	@Column(nullable = false)
	private Date captureDate;

	@ManyToOne
	private Chronometer chronometer;

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
		result += "id: " + id;
		return result;
	}

	public Chronometer getChronometer() {
		return this.chronometer;
	}

	public void setChronometer(final Chronometer chronometer) {
		this.chronometer = chronometer;
	}
}