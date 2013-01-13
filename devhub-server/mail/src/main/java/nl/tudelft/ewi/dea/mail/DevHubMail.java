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

	/**
	 * @param email The address to send the mail to.
	 * @param projectName The name of the project.
	 * @param publicUrl The public URL of the server.
	 */
	void sendDevHubInvite(String email, String fromDisplayName, String projectName, String publicUrl);

	/**
	 * @param from The address the email should be sent from.
	 * @param to The address to send the email to.
	 * @param title The title of the email.
	 * @param content The contents of the email.
	 */
	void sendFeedbackEmail(String from, String to, String title, String content);

	/**
	 * @param serviceName The name of the service the user has been registered
	 *           for.
	 * @param userName The user name of the user.
	 * @param password The (generated) password of the user for this service.
	 * @param email The email address of the user.
	 */
	void sendServiceRegistrationEmail(String continuousIntegrationServiceName, String userName, String password, String email);

}