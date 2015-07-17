package models;

import java.util.*;

import javax.persistence.*;

import play.data.format.*;
import play.data.validation.*;

import java.sql.*;
import java.sql.Date;

import play.db.*;
import play.db.jpa.*;

public class BugReport {

	@Id
	private int id;

	@Constraints.Required
	private String title;

	@Constraints.Required
	private String email;

	@Constraints.Required
	private String name;

	private String organization;
	private String description;
	private int solved = 0;

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

	@Formats.DateTime(pattern = "MM/dd/yy")
	public java.sql.Date creationDate;

	@Formats.DateTime(pattern = "MM/dd/yy")
	public java.sql.Date updateDate;

	// @ManyToOne
	// public User owner;
	@play.db.jpa.Transactional
	public static List getAll() {
		Connection connection = DB.getConnection();
		// This should clearly not be here, but for the moment i'll leave it
		// we need to learn how H2 works, it's an in-memory db so i think
		// there's two instances running
		try {
			Query query = JPA.em().createNativeQuery(
					"CREATE TABLE BUG_REPORT (id INT auto_increment, "
							+ "title VARCHAR(255), " 
							+ "name VARCHAR(255), "
							+ "email VARCHAR(255), "
							+ "organization VARCHAR(255), "
							+ "description VARCHAR(255), " 
							+ "solved TINYINT)");
			query.executeUpdate();
			System.out.println("created bug report table");
		} catch (Exception e) {
			System.out.println("Didn't create table");
		}
		finally {
			if (connection != null) {
				try {
					connection.close();
		        } catch (SQLException e) { 
		        	System.out.println(e);
		        }
			}
		}

		// org.hibernate.dialect.Dialect does not have boolean type registered
		Query query2 = JPA.em().createNativeQuery("SELECT * FROM BUG_REPORT order by solved");
		List<Object[]> list = query2.getResultList();
		
		return list;
	}

	@play.db.jpa.Transactional
	public boolean save() {
		Connection connection = DB.getConnection();
		// "INSERT INTO BUG_REPORT VALUES ("Name")";
		// Query query =
		// JPA.em().createNativeQuery("INSERT INTO BUG_REPORT VALUES (" +
		// this.title + ")" );
		// query.executeUpdate();
		try {
			// organization_name VARCHAR(255), email VARCHAR(255), description
			// VARCHAR(255), CREATED_AT TIMESTAMP
			Statement statement = connection.createStatement();
			String queryText = "INSERT INTO BUG_REPORT (title, name, email, organization, description, solved) VALUES ('"
					+ this.title.replaceAll("\'", "\'\'")
					+ "', '"
					+ this.name.replaceAll("\'", "\'\'")
					+ "', '"
					+ this.email.replaceAll("\'", "\'\'")
					+ "', '"
					+ this.organization.replaceAll("\'", "\'\'")
					+ "', '"
					+ this.description.replaceAll("\'", "\'\'") + "', 0)";
			statement.executeUpdate(queryText);
		} catch (Exception e) {
			System.out.println(e.toString());
			return false;
		}
		finally {
			if (connection != null) {
				try {
					connection.close();
		        } catch (SQLException e) { 
		        	System.out.println(e);
		        }
			}
		}
		return true;
	}

	@play.db.jpa.Transactional
	public static boolean delete(int id) {
		Connection connection = DB.getConnection();
		// "INSERT INTO BUG_REPORT VALUES ("Name")";
		// Query query =
		// JPA.em().createNativeQuery("INSERT INTO BUG_REPORT VALUES (" +
		// this.title + ")" );
		// query.executeUpdate();
		try {
			// organization_name VARCHAR(255), email VARCHAR(255), description
			// VARCHAR(255), CREATED_AT TIMESTAMP
			Statement statement = connection.createStatement();
			String deleteText = "DELETE FROM BUG_REPORT WHERE ID = " + id;
			statement.executeUpdate(deleteText);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		finally {
			if (connection != null) {
				try {
					connection.close();
		        } catch (SQLException e) { 
		        	System.out.println(e);
		        }
			}
		}
		return true;
	}
	
	@play.db.jpa.Transactional
	public static boolean solve(int id) {
		Connection connection = DB.getConnection();
		try {
			// organization_name VARCHAR(255), email VARCHAR(255), description
			// VARCHAR(255), CREATED_AT TIMESTAMP
			Statement statement = connection.createStatement();
			String solveText = "UPDATE BUG_REPORT SET SOLVED = 1 WHERE ID = " + id;
			statement.executeUpdate(solveText);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		finally {
			if (connection != null) {
				try {
					connection.close();
		        } catch (SQLException e) { 
		        	System.out.println(e);
		        }
			}
		}
		return true;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
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

}