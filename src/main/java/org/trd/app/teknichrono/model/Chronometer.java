package org.trd.app.teknichrono.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@XmlRootElement
public class Chronometer implements Serializable {

	/* =========================== Entity stuff =========================== */

	private static final long serialVersionUID = 108231410607139227L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, nullable = false)
	private int id;

	@Version
	@Column(name = "version")
	private int version;

	/* =============================== Fields =============================== */

	@Column
	private String name;

	// I dont make it unique because I am unsure of the save order
	@Column(nullable = true)
	private Integer index;

	// Can be null if after event, items are reassociated
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "chrono")
	@JsonIgnore
	private List<Ping> pings = new ArrayList<Ping>();

	/* ===================== Getters and setters ======================== */
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public List<Ping> getPings() {
		return pings;
	}

	public void setPings(List<Ping> pings) {
		this.pings = pings;
	}

	/* ===================== Other ======================== */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Chronometer)) {
			return false;
		}
		Chronometer other = (Chronometer) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		result += "(" + index + ") ";
		if (name != null && !name.trim().isEmpty())
			result += name;
		return result;
	}
}