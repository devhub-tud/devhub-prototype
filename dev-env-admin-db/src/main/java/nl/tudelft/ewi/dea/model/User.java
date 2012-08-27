package nl.tudelft.ewi.dea.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class User {

	@Id @GeneratedValue private long id;

	@Column(unique = true, nullable = false) private String username;

	@Column(unique = true, nullable = false) private String mailAddress;

	public User(long id, String username, String mailAddress) {
		this.id = id;
		this.username = username;
		this.mailAddress = mailAddress;
	}

	public String getUsername() {
		return username;
	}

	public String getMailAddress() {
		return mailAddress;
	}

	public long getId() {
		return id;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", mailAddress=" + mailAddress + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((mailAddress == null) ? 0 : mailAddress.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id != other.id)
			return false;
		if (mailAddress == null) {
			if (other.mailAddress != null)
				return false;
		} else if (!mailAddress.equals(other.mailAddress))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

}
