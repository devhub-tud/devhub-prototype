package nl.tudelft.ewi.dea.jaxrs.api.account;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.inject.Provider;

import nl.tudelft.ewi.dea.dao.PasswordResetTokenDao;
import nl.tudelft.ewi.dea.dao.UserDao;
import nl.tudelft.ewi.dea.jaxrs.html.utils.Renderer;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.security.SecurityProvider;
import nl.tudelft.ewi.dea.security.UserFactory;

import org.apache.shiro.authz.AuthorizationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccountResourceTest {

	private final long userId = 1;
	@Mock private Provider<Renderer> renderers;
	@Mock private PasswordResetTokenDao passwordResetTokenDao;
	@Mock private UserDao userDao;
	@Mock private UserFactory userFactory;
	@Mock private SecurityProvider subjectProvider;
	@Mock private User user;
	@Mock private User userFromPersist;
	@InjectMocks private AccountResource resource;

	@Before
	public void before() {
		when(subjectProvider.getUser()).thenReturn(user);
		when(user.getId()).thenReturn(userId);

		when(userDao.findById(user.getId())).thenReturn(userFromPersist);
	}

	@Test
	public void whenUserIsAllowedUpdateHisPassword() {
		String password = "newPassword";
		NewPasswordRequest request = mock(NewPasswordRequest.class);

		when(request.getPassword()).thenReturn(password);
		when(userFromPersist.getId()).thenReturn(userId);

		resource.resetPassword(userId, request);

		verify(userDao).findById(userId);
		verify(userFactory).resetUserPassword(userFromPersist, password);
	}

	@Test(expected = AuthorizationException.class)
	public void prohibitNormalUserFromChangingOtherPassword() {
		long otherUserId = 2l;
		resource.resetPassword(otherUserId, null);
	}

	@Test
	public void allowAdminToResetOtherPassword() {
		String password = "newPassword";
		NewPasswordRequest request = mock(NewPasswordRequest.class);
		when(user.isAdmin()).thenReturn(true);
		long otherUserId = 2l;
		when(userDao.findById(otherUserId)).thenReturn(userFromPersist);
		when(request.getPassword()).thenReturn(password);
		resource.resetPassword(otherUserId, request);

		verify(userFactory).resetUserPassword(userFromPersist, password);
	}

}
