package br.udesc.ceavi.dsd.chatiotest;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Testes de integração com o servidor.
 * @author Jeferson Penz
 */
public class ServerTestTest {
    
    private ServerTest server;
    
    @Before
    public void setUp() throws IOException {
        this.server = new ServerTest();
    }
    
    @After
    public void tearDown() {
        try {
            this.server.closeConnection();
        } catch (IOException ex) {
            Logger.getLogger(ServerTestTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.server = null;
    }

    /**
     * Test of beginTests method, of class ServerTest.
     * @throws java.lang.Exception
     */
    @Test
    public void testBeginTests() throws Exception {
        String data = "{"
                    + "\"birthdate\":\"17/06/1999\""
                    + "\"email\":\"teste@teste.com\""
                    + "\"nickname\":\"Teste\""
                    + "\"password\":\"123456\""
                    + "}";
        this.server.testMessage(MessageList.MESSAGE_CREATE_USER.toString() + data,
                                MessageList.MESSAGE_SUCCESS.toString());
    }
    
}
