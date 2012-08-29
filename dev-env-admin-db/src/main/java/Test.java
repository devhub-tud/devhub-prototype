import nl.tudelft.ewi.dea.Database;
import nl.tudelft.ewi.dea.PersistenceStartStopHandler;
import nl.tudelft.ewi.dea.di.DatabaseModule;

import com.google.inject.Guice;
import com.google.inject.Injector;


public class Test {

	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new DatabaseModule("test-h2"));

		injector.getInstance(Database.class).createDatabaseStructure("test-h2");
		
		injector.getInstance(PersistenceStartStopHandler.class).start();
	}

}
