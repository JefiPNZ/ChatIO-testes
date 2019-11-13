package br.udesc.ceavi.dsd.chatiotest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Testes de integração com o servidor.
 * @author Jeferson Penz
 */
public class ServerTest {
    
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    public ServerTest() throws IOException {
        this.socket = new Socket("127.0.0.1", 56000);
        this.writer = new PrintWriter(this.socket.getOutputStream(), true);
        this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    }
    
    public void testMessage(String message, String expect) throws IOException, Exception{
        this.writer.println(message);
        String response = this.reader.readLine();
        if(!response.equals(expect)){
            throw new Exception("Retorno inválido no cadastro de usuário: " + response);
        }
    }
    
    public void closeConnection() throws IOException{
        try {
            this.socket.close();
            this.writer = null;
            this.reader = null;
        } catch (IOException ex) {
            Logger.getLogger(ServerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
