package br.udesc.ceavi.dsd.chatiotest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Testes de integração com o servidor.
 * AVISO ANTES DE EXECUTAR OS TESTES FAÇA BACKUP DO BANCO DE DADOS.
 * @author Jeferson Penz
 */
public class ServerTestTest {
    
    private ServerTest server;
    public static final String TEST_UNIQUE_TOKEN = "7f77d5c2b5667c7b2302a6ce8dd5f000";
    
    @Before
    public void setUp() throws IOException, Exception {
        this.server = new ServerTest();
        this.server.testMessage("CLEAR>" + TEST_UNIQUE_TOKEN);
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
     * Teste do comando Create User.
     * @throws java.lang.Exception
     */
    @Test
    public void testCreateUser() throws Exception {
        Assert.assertEquals("Usuário não foi criado", MessageList.MESSAGE_SUCCESS.toString(), this.createNewUser("Teste Create"));
    }

    /**
     * Teste do comando Create User.
     * @throws java.lang.Exception
     */
    @Test
    public void testLogin() throws Exception {
        Assert.assertEquals("Usuário não foi criado", MessageList.MESSAGE_SUCCESS.toString(), this.createNewUser("Teste Login"));
        Assert.assertEquals("Login não efetuado", MessageList.MESSAGE_SUCCESS.toString(), this.doLogin("Teste Login"));
    }

    /**
     * Teste do comando Create User.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetUserData() throws Exception {
        Assert.assertEquals("Usuário não foi criado", MessageList.MESSAGE_SUCCESS.toString(), this.createNewUser("Teste Busca Usuario"));
        Assert.assertEquals("Login não efetuado", MessageList.MESSAGE_SUCCESS.toString(), this.doLogin("Teste Busca Usuario"));
        String res = this.server.testMessage(MessageList.MESSAGE_GET_USER_DATA.toString());
        res = res.replace("DATA>", "");
        JsonObject jsonObject = JsonParser.parseString(res).getAsJsonObject();
        Assert.assertEquals("Teste Busca Usuario", jsonObject.get("nickname").getAsString());
        Assert.assertEquals("1999", jsonObject.get("birthDate").getAsString());
    }
    
    protected String createNewUser(String nickname) throws Exception{
        String data = "{"
                    + "\"birthDate\":\"1999\","
                    + "\"email\":\"teste@teste.com\","
                    + "\"nickname\":\"" + nickname + "\","
                    + "\"password\":\"123456\""
                    + "}";
        return this.server.testMessage(MessageList.MESSAGE_CREATE_USER.toString() + data);
    }
    
    protected String doLogin(String nickname) throws Exception{
        String data = "{"
                    + "\"nickname\":\"" + nickname + "\","
                    + "\"password\":\"123456\""
                    + "}";
        return this.server.testMessage(MessageList.MESSAGE_LOGIN.toString() + data);
    }
    
}
