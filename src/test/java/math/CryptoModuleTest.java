package math;


import configuration.Configuration;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CryptoModuleTest {
    private CryptoModule cryptoModule;

    @Before
    public void initTest() {
        Configuration configuration = new Configuration("config.properties");
        configuration.getConfiguration();
        cryptoModule = new CryptoModule(configuration);
    }

    @Test
    public void binpowTest() {
        System.out.println("TEST running");
        assertEquals(8, cryptoModule.binpow(2, 3));
    }
}