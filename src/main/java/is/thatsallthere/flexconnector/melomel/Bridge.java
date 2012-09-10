package is.thatsallthere.flexconnector.melomel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Bridge {

    private static final int PORT = 10101;
    private Socket socket = null;
    private BufferedReader readerIn;
    PrintWriter writerOut;


    public void connect() throws IOException {
        disconnect();
        ServerSocket server = new ServerSocket(PORT);
        boolean needsToConnect = true;
        boolean connectionSuccessfull = false;


        int counter = 0;
        while (needsToConnect) {
            counter++;

            if (counter >= 5)
                needsToConnect = false;

            System.out.println("Attempt #" + counter);
            Socket actualSocket = server.accept();
            actualSocket.setSoTimeout(10000);
            readerIn = new BufferedReader(new InputStreamReader(actualSocket.getInputStream()));
            writerOut = new PrintWriter(actualSocket.getOutputStream(), true);


            String str;
            str = read(readerIn);
            System.out.println("[READ]" + str + "\n");

            if (str.equals("<policy-file-request/>")) {
                String policy = getCrossDomainPolicy() + "\u0000";
                write(policy, writerOut);
                close(actualSocket, writerOut, readerIn);
            } else if (str.equals("<connect/>")) {
                needsToConnect = false;
                connectionSuccessfull = true;
                System.out.println("Got a connect !!!!");
                socket = actualSocket;
            }
        }

        if (!connectionSuccessfull)
            System.out.println("Leaving after " + counter + " attempts");

        if (socket != null)
            System.out.println("[DEBUG] got a connected socket : {" + socket.toString() + "} and socket is connected ? " + socket.isConnected());
        server.close();
    }

    public void close(Socket socket, PrintWriter writerOut, BufferedReader readerIn) {
        try {
            if (socket != null) socket.close();
            if (writerOut != null) writerOut.close();
            if (readerIn != null) readerIn.close();
        } catch (IOException e) {
        }

        readerIn = null;
        writerOut = null;
        socket = null;
    }


    public void write(String msg, PrintWriter writerOut) {
        writerOut.println(msg + "\u0000");
        writerOut.flush();
        System.out.println("Wrote: " + msg);
    }

    private String read(BufferedReader readerIn) throws IOException{
        StringBuffer buffer = new StringBuffer();
        int codePoint;
        boolean zeroByteRead = false;

        System.out.println("Reading...");
        do {
            codePoint = readerIn.read();
            if (codePoint == 0) zeroByteRead = true;
            else buffer.appendCodePoint(codePoint);
        }
        while (!zeroByteRead && buffer.length() < 100);
        System.out.println("Read: " + buffer.toString());

        return buffer.toString();
    }



    private String getCrossDomainPolicy() {
        return "<?xml version=\"1.0\"?>"
                + "<!DOCTYPE cross-domain-policy SYSTEM \"http://www.macromedia.com/xml/dtds/cross-domain-policy.dtd\">"
                + "<cross-domain-policy>"
                + "<allow-access-from domain=\"*\" to-ports=\"10101\"/>"
                + "</cross-domain-policy>";
    }

    protected void disconnect() {
        if (socket == null)
            return;

        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Socket was closed. Received this : " + e.toString());
        }
        socket = null;
    }


    public void send(String message) throws IOException {
        write(message, writerOut);
        String replyFromFlex = read(readerIn);
        System.out.println("[DEBUG] the reply from flex was : " + replyFromFlex);
    }
}


