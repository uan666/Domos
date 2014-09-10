package org.hctr.libraries;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class RaspiSocket implements Runnable {
	private Socket socket;
	private String text = "";
	private int SERVER_PORT=0;
	private String SERVER_IP="";
	private Handler HANDLER;
	
	public RaspiSocket(String ip, int port, String str, Handler handler){
		this.text=str;
		this.HANDLER=handler;
		if (port>0)
			this.SERVER_PORT=port;
		if (ip!="")
				this.SERVER_IP=ip;
	}

    @Override
    public void run() {

        try {
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

            socket = new Socket(serverAddr, this.SERVER_PORT);

            this.sendText(text);
            
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }
    
    public void sendText(String text){
    	PrintWriter out;
		try {
			//enviamos la peticion
			out = new PrintWriter(new BufferedWriter(
			        new OutputStreamWriter(socket.getOutputStream())),
			        true);
			out.println(text);
			
			//recojemos la respuesta
			DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
			String response = dataInputStream.readUTF();
			
			//mandamos al handler de la actividad el resultado, para que haga lo que deba
			Message msgObj = HANDLER.obtainMessage();
            Bundle b = new Bundle();
            b.putString("status", response);
            msgObj.setData(b);
            HANDLER.sendMessage(msgObj);
            
            //cerramos el socket
            socket.close();
            
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
