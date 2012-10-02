package nl.tudelft.ewi.dea.mail;

import com.google.inject.ImplementedBy;

/**
 * Hub for sending all available mail templates.
 * 
 */
@ImplementedBy(DevHubMailImpl.class)
public interface DevHubMail {

	void sendVerifyRegistrationMail(String toAdress, String url);

	void sendResetPasswordMail(String toAdress, String url);

	/**
	 * @param email The address the mail has to be sent to.
	 * @param displayName The user name of the guy inviting.
	 * @param projectName The project name.
	 * @param url The url that is to be visited by the invited person.
	 */
	void sendProjectInvite(String email, String displayName, String projectName, String url);

}
