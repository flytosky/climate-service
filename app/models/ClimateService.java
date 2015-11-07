package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import utils.Constants;
import utils.RESTfulCalls;

import com.fasterxml.jackson.databind.JsonNode;

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
	private String imageURL;

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

	public void setId(long id){
		this.id = id;
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
	
	public void setImageURL(){
		if(name.equals("scatterPlot2Vars")) {
			imageURL = "/assets/images/ScatterPlot.png";
		}else if(name.equals("twoDimSlice3D")) {
			imageURL = "/assets/images/TwoDimSlice3D.jpeg";
		}else {
			//photo = "http://i1-mac.softpedia-static.com/screenshots/Climate-Data-Analysis-Tools_1.png";
			imageURL = "http://upload.wikimedia.org/wikipedia/commons/3/33/White_square_with_question_mark.png";
		}
	}
	
	public String getImageURL() {
		return imageURL;
	}
	
	@Override
	public String toString() {
		return "ClimateService [id=" + id + ", rootServiceId=" + rootServiceId
				+ ", user=" + user + ", name=" + name + ", purpose=" + purpose
				+ ", url=" + url + ", scenario=" + scenario + ", createTime="
				+ createTime + ", versionNo=" + versionNo + "]";
	}
}
