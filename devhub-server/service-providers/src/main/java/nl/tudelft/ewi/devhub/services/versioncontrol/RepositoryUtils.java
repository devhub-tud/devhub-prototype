package nl.tudelft.ewi.devhub.services.versioncontrol;

public interface RepositoryUtils {

	/**
	 * @param repositoryUrl The url where the other repo should be cloned into.
	 * @param cloneRepo The repo you want to clone into the new repo.
	 */
	void setCustomTemplateInRepo(String repositoryUrl, String cloneRepo);

	/**
	 * @param repositoryUrl The url where the default skeleton should be loaded.
	 */
	void setDefaultTemplateInRepo(String repositoryUrl);

}
