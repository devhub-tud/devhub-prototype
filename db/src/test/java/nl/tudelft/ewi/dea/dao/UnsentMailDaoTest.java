package nl.tudelft.ewi.dea.dao;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;
import nl.tudelft.ewi.dea.model.UnsentMail;

import org.junit.Test;

public class UnsentMailDaoTest extends DatabaseTest {

	@Test
	public void testAddingAndRemoving() {
		String mail = "This should be a mail as JSON";
		String mail2 = "This is another email";
		UnsentMailDao dao = getInstance(UnsentMailDao.class);
		UnsentMail unsent1 = dao.persist(mail);
		assertThat(dao.getUnsentMails(), hasSize(1));
		UnsentMail unsent2 = dao.persist(mail2);
		assertThat(dao.getUnsentMails(), hasSize(2));
		dao.remove(unsent1.getId());
		dao.remove(unsent2.getId());
		assertThat(dao.getUnsentMails(), hasSize(0));
	}

}
