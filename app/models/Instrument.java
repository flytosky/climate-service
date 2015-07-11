package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Instrument {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String name;
	private String description;
	private Date launchDate;
	
	public Instrument() {
	}
	
	public Instrument (String name, String description, Date launchDate) {
		super();
		this.name = name;
		this.description = description;
		this.launchDate = launchDate;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Date getLaunchDate() {
		return launchDate;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setLaunchDate(Date launchDate) {
		this.launchDate = launchDate;
	}

	@Override
	public String toString() {
		return "Instrument [id=" + id + ", name=" + name + ", description="
				+ description + ", launchDate=" + launchDate + "]";
	}

	

}
