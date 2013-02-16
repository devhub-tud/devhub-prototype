package nl.tudelft.ewi.dea.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

@Entity
@Table(name = "ssh_keys")
public class SshKey {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;
	@Column(name = "key_name", nullable = false) private String keyName;
	@Column(name = "key_contents", nullable = false) private String keyContents;
	@ManyToOne(optional = false) private User user;

	@SuppressWarnings("unused")
	private SshKey() {
		// Do nothing.
	}

	public SshKey(User user, String name, String contents) {
		this.user = user;
		this.keyName = name.trim();
		this.keyContents = contents.trim();
		validate();
	}

	public void setUser(User user) {
		Preconditions.checkNotNull(user);
		this.user = user;
		validate();
	}

	public void setKeyName(String keyName) {
		Preconditions.checkNotNull(keyName);
		this.keyName = keyName;
		validate();
	}

	public void setContents(String keyContents) {
		Preconditions.checkNotNull(keyContents);
		this.keyContents = keyContents;
		validate();
	}

	public long getId() {
		return id;
	}

	public String getKeyName() {
		return keyName;
	}

	public String getKeyContents() {
		return keyContents;
	}

	public User getUser() {
		return user;
	}

	private final void validate() {
		checkNotNull(user);
		checkArgument(!Strings.isNullOrEmpty(keyName));
		checkArgument(!Strings.isNullOrEmpty(keyContents));

		checkArgument(keyName.length() <= 25);
		checkArgument(keyName.matches("^[a-zA-Z0-9]+([-][a-zA-Z0-9]+)*$"));
		checkArgument(keyContents.matches("^ssh\\-[a-z]{3}\\s\\S+(\\s\\S+)?$"));
	}

}
