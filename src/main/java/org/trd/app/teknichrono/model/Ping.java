package org.trd.app.teknichrono.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
public class Ping implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7022575222961829989L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, nullable = false)
	private int id;

	@Version
	@Column(name = "version")
	private int version;

	@Column(nullable = false)
	private double time;

	@Column(nullable = false)
	private String uuid;

	@Column(nullable = false)
	private int power;

	@Column(nullable = false)
	private int chronoPointIndex;

	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
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
		if (!(obj instanceof Ping)) {
			return false;
		}
		Ping other = (Ping) obj;

		if (id != other.id) {
			return false;
		}
		return true;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public int getChronoPointIndex() {
		return chronoPointIndex;
	}

	public void setChronoPointIndex(int chronoPointIndex) {
		this.chronoPointIndex = chronoPointIndex;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		result += "time: " + time;
		if (uuid != null && !uuid.trim().isEmpty())
			result += ", uuid: " + uuid;
		result += ", power: " + power;
		result += ", chronoPointIndex: " + chronoPointIndex;
		return result;
	}
}