package server;

import util.SaslMessage;

import javax.security.sasl.Sasl;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import java.io.IOException;
import java.net.Socket;

import static util.PrototypUtil.CALLBACK_HANDLER_SERVER;
import static util.SaslConstants.DIGEST_MD5;
import static util.SaslMessage.STATUS.CONTINUE;
import static util.SaslMessage.STATUS.FAILURE;
import static util.SaslMessage.STATUS.SUCCESS;

public class DigestMd5ConnectionHandler extends Thread{

    private final Socket socket;
    private SaslServer saslServer;


    DigestMd5ConnectionHandler(final Socket socket) {
        this.socket = socket;
        try {
            saslServer = Sasl.createSaslServer(DIGEST_MD5, "local",
                    "127.0.0.1", null, CALLBACK_HANDLER_SERVER);
            if (saslServer == null) {
                throw new RuntimeException("SaslServer is null");
            }
        } catch (SaslException e) {
            e.printStackTrace();
            saslServer = null;
        }
    }


    @Override
    public void run() {

        try {
            proceedWithSasl();
        } catch (InterruptedException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

    }

    private void proceedWithSasl() throws InterruptedException, IOException, ClassNotFoundException {
        // Read request that contains mechanism name and optional initial response
        SaslMessage msg = SaslMessage.receive(socket);
        // Perform authentication steps until done
        while (!saslServer.isComplete()) {
            try {
                // Process response
                byte[] challenge = saslServer.evaluateResponse(msg.getContent());

                if (saslServer.isComplete()) {
                    send(saslServer.getMechanismName(), challenge, SUCCESS);
                    System.out.println("SASL COMPLETED");
                } else {
                    send(saslServer.getMechanismName(), challenge, CONTINUE);
                    msg = SaslMessage.receive(socket);

                }
            } catch (SaslException e) {
                e.printStackTrace();
                send(saslServer.getMechanismName(), new byte[0], FAILURE);
                saslServer.dispose();
                break;
            }
        }


    }


    private void send(String mechanism, byte[] response, SaslMessage.STATUS status) throws IOException {
        SaslMessage out = new SaslMessage(mechanism, status, response);
        out.send(socket);
    }
}



