package models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class ClimateService {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private long rootServiceId;
	@ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
	@JoinColumn(name = "creatorId", referencedColumnName = "id")
	private User user;
	private String name;
	private String purpose;
	private String url;
	private String scenario;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;

	private String versionNo;

	public ClimateService() {
	}

	public ClimateService(long rootServiceId, User user, String name,
			String purpose, String url, String scenario, Date createTime,
			String versionNo) {
		super();
		this.rootServiceId = rootServiceId;
		this.user = user;
		this.name = name;
		this.purpose = purpose;
		this.url = url;
		this.scenario = scenario;
		// For creation time, can just use current time like this
		if (createTime == null)
			createTime = new Date();
		this.createTime = createTime;
		this.versionNo = versionNo;
	}

	public long getId() {
		return id;
	}

	public long getRootServiceId() {
		return rootServiceId;
	}

	public void setRootServiceId(long rootServiceId) {
		this.rootServiceId = rootServiceId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getScenario() {
		return scenario;
	}

	public void setScenario(String scenario) {
		this.scenario = scenario;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}

	@Override
	public String toString() {
		return "ClimateService [id=" + id + ", rootServiceId=" + rootServiceId
				+ ", user=" + user + ", name=" + name + ", purpose=" + purpose
				+ ", url=" + url + ", scenario=" + scenario + ", createTime="
				+ createTime + ", versionNo=" + versionNo + "]";
	}

}
