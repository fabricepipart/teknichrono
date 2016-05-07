package org.trd.app.teknichrono.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
public class Event implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 929783339304030614L;

	@Id
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@Version
	@Column(name = "version")
	private int version;

	@Column(nullable = false)
	private Date beginning;

	@Column(nullable = false)
	private Date end;

	@OneToMany
	@OrderColumn(name = "index")
	private List<ChronoPoint> chronopoints = new ArrayList<ChronoPoint>();

	@Column(nullable = false)
	private String name;

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

	public Date getBeginning() {
		return beginning;
	}

	public void setBeginning(Date beginning) {
		this.beginning = beginning;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public List<ChronoPoint> getChronopoints() {
		return this.chronopoints;
	}

	public void setChronopoints(final List<ChronoPoint> chronopoints) {
		this.chronopoints = chronopoints;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		if (name != null && !name.trim().isEmpty())
			result += "name: " + name;
		return result;
	}

}
