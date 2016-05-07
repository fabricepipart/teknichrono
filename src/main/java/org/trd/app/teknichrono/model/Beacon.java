package org.trd.app.teknichrono.model;
// Generated 5 mai 2016 11:08:49 by Hibernate Tools 4.3.1.Final

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
public class Beacon implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2438563507266191424L;

	private int id;
	private int number;

	public Beacon() {
	}

	public Beacon(int id, int number) {
		this.id = id;
		this.number = number;
	}

	@Id
	@Column(name = "ID", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "NUMBER", nullable = false)
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
