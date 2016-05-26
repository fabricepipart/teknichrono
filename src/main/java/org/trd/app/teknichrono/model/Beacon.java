package org.trd.app.teknichrono.model;
// Generated 5 mai 2016 11:08:49 by Hibernate Tools 4.3.1.Final

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonCreator;

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
