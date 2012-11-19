package nl.tudelft.ewi.dea.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.google.common.base.Preconditions;

@Data
@NoArgsConstructor
@Entity(name = "ssh_keys")
public class SshKey {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;
	@Column(name = "key_name", nullable = false) private String keyName;
	@Column(name = "key_contents", nullable = false) private String keyContents;
	@ManyToOne(optional = false) private User user;

	public SshKey(User user, String name, String contents) {
		this.user = user;
		this.keyName = name;
		this.keyContents = contents;
	}

	public void setUser(User user) {
		Preconditions.checkNotNull(user);
		this.user = user;
	}

	public void setKeyName(String keyName) {
		Preconditions.checkNotNull(keyName);
		this.keyName = keyName;
	}

	public void setContents(String keyContents) {
		Preconditions.checkNotNull(keyContents);
		this.keyContents = keyContents;
	}

}
