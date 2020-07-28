package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private List<ClientHandler> clients;
    private AuthService authService;
    private ExecutorService service = Executors.newCachedThreadPool();

    public AuthService getAuthService() {
        return authService;
    }

    public Server() {
        clients = new Vector<>();
//        authService = new SimpleAuthService();
        //==============//
        if (!SQLHandler.connect()) {
            throw new RuntimeException("Не удалось подключиться к БД");
        }
        authService = new DBAuthServise();
        //==============//

        ServerSocket server = null;
        Socket socket;

        final int PORT = 8189;

        try {
            server = new ServerSocket(PORT);
            System.out.println("Сервер запущен!");

            while (true) {
                socket = server.accept();
                System.out.println("Клиент подключился");
                System.out.println("socket.getRemoteSocketAddress(): " + socket.getRemoteSocketAddress());
                System.out.println("socket.getLocalSocketAddress() " + socket.getLocalSocketAddress());


                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void broadcastMsg(ClientHandler sender, String msg) {
        String message = String.format("%s : %s", sender.getNick(), msg);

        //==============//
        SQLHandler.addMessage(sender.getNick(),"null",msg,"once upon a time");
        //==============//

        for (ClientHandler client : clients) {
            client.sendMsg(message);
        }
    }



    void privateMsg(ClientHandler sender, String receiver, String msg) {

        service.execute(new Runnable() {
            @Override
            public void run() {
                String message = String.format("[%s] private [%s] : %s", sender.getNick(), receiver, msg);

                for (ClientHandler c : clients) {
                    if(c.getNick().equals(receiver)){
                        c.sendMsg(message);

                        //==============//
                        SQLHandler.addMessage(sender.getNick(),receiver,msg,"once upon a time");
                        //==============//

                        if (!sender.getNick().equals(receiver)) {
                            sender.sendMsg(message);
                        }

                        return;
                    }
                }
                sender.sendMsg(String.format("Client %s not found", receiver));


            }
        });
    }


    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        broadcastClientList();
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastClientList();
    }

    public boolean isLoginAuthorized(String login){
        for (ClientHandler c : clients) {
            if(c.getLogin().equals(login)){
                return true;
            }
        }
        return false;
    }

    void broadcastClientList() {
        StringBuilder sb = new StringBuilder("/clientlist ");

        for (ClientHandler c : clients) {
            sb.append(c.getNick()).append(" ");
        }

        String msg = sb.toString();

        for (ClientHandler c : clients) {
            c.sendMsg(msg);
        }
    }

}
