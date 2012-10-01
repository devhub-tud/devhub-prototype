package nl.tudelft.ewi.dea.jaxrs.account;

import java.util.List;

import lombok.Data;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.model.UserRole;

import com.google.common.collect.Lists;

@Data
public class AccountDto {

	public static List<AccountDto> convert(List<User> users) {
		List<AccountDto> accounts = Lists.newArrayList();
		for (User user : users) {
			accounts.add(new AccountDto(user.getId(), user.getEmail(), user.getDisplayName(), user.getRole() == UserRole.ADMIN));
		}
		return accounts;
	}

	private final long id;
	private final String email;
	private final String name;
	private final boolean isAdmin;

}
