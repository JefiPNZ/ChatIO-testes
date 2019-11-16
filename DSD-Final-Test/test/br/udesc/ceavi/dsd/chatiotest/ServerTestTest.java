package br.udesc.ceavi.dsd.chatiotest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
     * Teste do comando CreateUser.
     * @throws java.lang.Exception
     */
    @Test
    public void testCreateUser() throws Exception {
        Assert.assertEquals("Usuário não foi criado", MessageList.MESSAGE_SUCCESS.toString(), this.createNewUser("Teste Create"));
    }

    /**
     * Teste do comando Login.
     * @throws java.lang.Exception
     */
    @Test
    public void testLogin() throws Exception {
        Assert.assertEquals("Usuário não foi criado", MessageList.MESSAGE_SUCCESS.toString(), this.createNewUser("Teste Login"));
        Assert.assertEquals("Login não efetuado", MessageList.MESSAGE_SUCCESS.toString(), this.doLogin("Teste Login"));
    }

    /**
     * Teste do comando GetUserData.
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

    /**
     * Teste do comando Connected.
     * @throws java.lang.Exception
     */
    @Test
    public void testConnected() throws Exception {
        Assert.assertEquals("Usuário não foi criado", MessageList.MESSAGE_SUCCESS.toString(), this.createNewUser("Teste Conectado"));
        Assert.assertEquals("Login não efetuado", MessageList.MESSAGE_SUCCESS.toString(), this.doLogin("Teste Conectado"));
        String res;
        for(int i = 0; i < 10; i++){
            res = this.server.testMessage(MessageList.MESSAGE_CONNECTED_STATUS.toString());
            Assert.assertEquals(MessageList.MESSAGE_SUCCESS.toString(), res);
            Thread.sleep(500);
        }
        // Testa por timeout, comentar quando for realizar outros testes.
        Thread.sleep(10000);
        res = this.server.testMessage(MessageList.MESSAGE_CONNECTED_STATUS.toString());
        Assert.assertNull(res);
    }
    
    /**
     * Teste do comando AddContact.
     * @throws java.lang.Exception
     */
    @Test
    public void testAddContact() throws Exception {
        Assert.assertEquals("Usuário não foi criado", MessageList.MESSAGE_SUCCESS.toString(), this.createNewUser("Teste Add Contato 1"));
        Assert.assertEquals("Login não efetuado", MessageList.MESSAGE_SUCCESS.toString(), this.doLogin("Teste Add Contato 1"));
        Assert.assertEquals("Usuário não foi criado", MessageList.MESSAGE_SUCCESS.toString(), this.createNewUser("Teste Add Contato 2"));
        String res = this.addNewContact("Teste Add Contato 2");
        Assert.assertEquals(MessageList.MESSAGE_SUCCESS.toString(), res);
        res = this.addNewContact("Teste Add Contato 3");
        Assert.assertThat(res, CoreMatchers.startsWith(MessageList.MESSAGE_ERROR.toString()));
    }
    
    /**
     * Teste do comando GetContactList.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetContactList() throws Exception {
        Assert.assertEquals("Usuário não foi criado", MessageList.MESSAGE_SUCCESS.toString(), this.createNewUser("Teste Get Contato 1"));
        Assert.assertEquals("Login não efetuado", MessageList.MESSAGE_SUCCESS.toString(), this.doLogin("Teste Get Contato 1"));
        Assert.assertEquals("Usuário não foi criado", MessageList.MESSAGE_SUCCESS.toString(), this.createNewUser("Teste Get Contato 2"));
        
        String res = this.addNewContact("Teste Get Contato 2");
        Assert.assertEquals(MessageList.MESSAGE_SUCCESS.toString(), res);
        
        res = this.server.testMessage(MessageList.MESSAGE_GET_CONTACT_LIST.toString());
        res = res.replace("DATA>", "");
        JsonArray firstContacts = JsonParser.parseString(res).getAsJsonArray();
        Assert.assertThat(firstContacts.size(), Matchers.greaterThan(0));
        String firstUser    = firstContacts.get(0).getAsJsonObject().get("user").getAsJsonObject().get("nickname").getAsString();
        String firstContact = firstContacts.get(0).getAsJsonObject().get("contact").getAsJsonObject().get("nickname").getAsString();
        
        this.server = new ServerTest();
        Assert.assertEquals("Login não efetuado", MessageList.MESSAGE_SUCCESS.toString(), this.doLogin("Teste Get Contato 2"));
        res = this.server.testMessage(MessageList.MESSAGE_GET_CONTACT_LIST.toString());
        res = res.replace("DATA>", "");
        JsonArray secondContacts = JsonParser.parseString(res).getAsJsonArray();
        Assert.assertThat(secondContacts.size(), Matchers.greaterThan(0));
        String secondUser    = secondContacts.get(0).getAsJsonObject().get("user").getAsJsonObject().get("nickname").getAsString();
        String secondContact = secondContacts.get(0).getAsJsonObject().get("contact").getAsJsonObject().get("nickname").getAsString();
        
        Assert.assertEquals(firstUser, secondUser);
        Assert.assertEquals(firstContact, secondContact);
    }
    
    /**
     * Teste do comando RemoveContact.
     * @throws java.lang.Exception
     */
    @Test
    public void testRemoveContact() throws Exception {
        Assert.assertEquals("Usuário não foi criado", MessageList.MESSAGE_SUCCESS.toString(), this.createNewUser("Teste Remove Contato 1"));
        Assert.assertEquals("Login não efetuado", MessageList.MESSAGE_SUCCESS.toString(), this.doLogin("Teste Remove Contato 1"));
        Assert.assertEquals("Usuário não foi criado", MessageList.MESSAGE_SUCCESS.toString(), this.createNewUser("Teste Remove Contato 2"));
        
        String res = this.addNewContact("Teste Remove Contato 2");
        Assert.assertEquals(MessageList.MESSAGE_SUCCESS.toString(), res);
        
        String data = "{\"nickname\":\"Teste Remove Contato 2\"}";
        res = this.server.testMessage(MessageList.MESSAGE_REMOVE_CONTACT.toString() + data);
        Assert.assertEquals(MessageList.MESSAGE_SUCCESS.toString(), res);
        res = this.server.testMessage(MessageList.MESSAGE_GET_CONTACT_LIST.toString());
        res = res.replace("DATA>", "");
        JsonArray firstContacts = JsonParser.parseString(res).getAsJsonArray();
        Assert.assertEquals(0, firstContacts.size());
    }
    
    protected String addNewContact(String nickname) throws Exception{
        String data = "{"
                    + "\"nickname\":\"" + nickname + "\""
                    + "}";
        return this.server.testMessage(MessageList.MESSAGE_ADD_CONTACT.toString() + data);
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
