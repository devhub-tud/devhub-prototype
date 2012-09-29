package nl.tudelft.ewi.dea.jaxrs.account;

import java.util.List;

import lombok.Data;
import nl.tudelft.ewi.dea.model.User;

import com.google.common.collect.Lists;

@Data
public class Account {

	public static List<Account> convert(List<User> users) {
		List<Account> accounts = Lists.newArrayList();
		for (User user : users) {
			accounts.add(new Account(user.getId(), user.getEmail(), user.getDisplayName()));
		}
		return accounts;
	}

	private final long id;
	private final String email;
	private final String name;

}
