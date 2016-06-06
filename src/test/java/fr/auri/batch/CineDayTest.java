package fr.auri.batch;

import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.Before;
import org.junit.BeforeClass;

public class CineDayTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

//        @Test
    public final void testEncryptMdp() throws Exception {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword("toto");
        String mdpEncrypt=textEncryptor.encrypt(""); //Mets ton mot de passe Orange en clair :/
        System.out.println(mdpEncrypt);
    }
}
