package models;

import java.util.Date;

public class BugReport {
	
	private long id;
	private String title;
	private String email;
	private String name;
	private String organization;
	private String description;
	private int solved = 0;
	private Date creationDate;
	private Date updateDate;


	public BugReport() {

	}

	public BugReport(String title, String email, String name,
			String organization, String description, int solved,
			Date creationDate, Date updateDate) {
		super();
		this.title = title;
		this.email = email;
		this.name = name;
		this.organization = organization;
		this.description = description;
		this.solved = solved;
		this.creationDate = creationDate;
		this.updateDate = updateDate;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String toString() {
		return "BugReport #" + id;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return this.description;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getSolved() {
		return solved;
	}

	public void setSolved(int solved) {
		this.solved = solved;
	}


	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

}