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

}
