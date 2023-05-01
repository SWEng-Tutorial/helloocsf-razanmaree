package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class SimpleServer extends AbstractServer {

	static int counter = 0;
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

	public SimpleServer(int port) {
		super(port);
		
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		Message message = (Message) msg;
		String request = message.getMessage();
		counter=0;
		try {
			//we got an empty message, so we will send back an error message with the error details.
			if (request.isBlank()){
				message.setMessage("Error! we got an empty message");
				client.sendToClient(message);
			}
			//we got a request to change submitters IDs with the updated IDs at the end of the string, so we save
			// the IDs at data field in Message entity and send back to all subscribed clients a request to update
			//their IDs text fields. An example of use of observer design pattern.
			//message format: "change submitters IDs: 123456789, 987654321"
			else if(request.startsWith("change submitters IDs:")){
				message.setData(request.substring(23));
				message.setMessage("update submitters IDs");
				sendToAllClients(message);
			}
			//we got a request to add a new client as a subscriber.
			else if (request.equals("add client")){
				SubscribedClient connection = new SubscribedClient(client);
				SubscribersList.add(connection);
				message.setMessage("client added successfully");
				client.sendToClient(message);
			}
			//we got a message from client requesting to echo Hello, so we will send back to client Hello world!
			else if(request.startsWith("echo Hello")){
				message.setMessage("Hello World!");
				client.sendToClient(message);
			}
			else if(request.startsWith("send Submitters IDs")){
				if(counter==0)
				{
					//nothing
					counter++;
				}
				else
				{
					message.setMessage("314893520, 314893538");
					client.sendToClient(message);
				}
			}
//			else if(request.startsWith("send Submitters IDs")){  //no need
//				//add code here to send submitters IDs to client
//				message.setMessage("314893520, 314893538");
//				client.sendToClient(message);
//			}
			else if (request.startsWith("send Submitters")){
				//add code here to send submitters names to client
				message.setMessage("Razan, Nawras");
				client.sendToClient(message);
			}
			else if (request.equals("what’s the time?")) {
				//add code here to send the time to client
				LocalDateTime t = message.getTimeStamp();
				String st=String.valueOf(t);
				message.setMessage(st);
				client.sendToClient(message);
			}
			else if (request.startsWith("multiply")){
				//add code here to multiply 2 numbers received in the message and send result back to client
				//(use substring method as shown above)
				//message format: "multiply n*m"
				char[] arr = request.toCharArray();// or char[] arr = message.getMessage().toCharArray();
				String str = request;
				int index=0,index2=0;
				for(int i=0;i<=(arr.length-1);i++)
				{
					if(arr[i]=='*')
					{
						index=i;
					}
					if(arr[i]==' ')
					{
						index2=i;
					}
				}
				String m_=str.substring(index+1);
				int m=Integer.parseInt(m_);
				String n_=str.substring(index2+1,index);
				int n=Integer.parseInt(n_);

				message.setMessage(Integer.toString(n*m));
				client.sendToClient(message);

			}else{
				sendToAllClients(message);
				//add code here to send received message to all clients.
				//The string we received in the message is the message we will send back to all clients subscribed.
				//Example:
					// message received: "Good morning"
					// message sent: "Good morning"
				//see code for changing submitters IDs for help

			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void sendToAllClients(Message message) {
		try {
			for (SubscribedClient SubscribedClient : SubscribersList) {
				SubscribedClient.getClient().sendToClient(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
