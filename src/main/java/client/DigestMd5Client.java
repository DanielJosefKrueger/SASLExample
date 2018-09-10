package client;

import Util.SaslMessage;
import Util.ScenarioVariables;

import javax.security.sasl.Sasl;
import javax.security.sasl.SaslClient;
import java.io.IOException;
import java.net.Socket;

import static Util.PrototypUtil.CALLBACK_HANDLER_CLIENT;
import static Util.SaslConstants.DIGEST_MD5;
import static Util.SaslConstants.PLAIN;
import static Util.SaslMessage.STATUS.CONTINUE;
import static Util.SaslMessage.STATUS.FAILURE;
import static Util.SaslMessage.STATUS.SUCCESS;
import static Util.SaslMessage.receive;

public class DigestMd5Client {

    private Socket socket;


    public static void main(String[] args) throws Exception {
        DigestMd5Client client = new DigestMd5Client(ScenarioVariables.HOST, ScenarioVariables.PORT);
        client.authenticate();
    }

    private DigestMd5Client(String host, int port) {
        try {
            socket = new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean authenticate() throws IOException, InterruptedException, ClassNotFoundException {
        System.out.println("Starting Authentication");
        String[] mechanisms = new String[]{DIGEST_MD5, PLAIN};
        SaslClient sc = Sasl.createSaslClient(mechanisms, "client", "local",
                "127.0.0.1", null, CALLBACK_HANDLER_CLIENT);
        // Get optional initial response
        byte[] response = (sc.hasInitialResponse() ? sc.evaluateChallenge(new byte[0]) : null);
        String mechanism = sc.getMechanismName();

        // Send selected mechanism name and optional initial response to server
        send(mechanism, response);
        // Read response
        SaslMessage msg = receive(socket);
        while (!sc.isComplete() && (msg.getStatus() == CONTINUE || msg.getStatus() == SUCCESS)) {
            // Evaluate server challenge
            response = sc.evaluateChallenge(msg.getContent());

            if (msg.getStatus() == SUCCESS) {
                System.out.println("Authentication successful");
                // done; server doesn't expect any more SASL data
                if (response != null) {
                    throw new IOException("Protocol error: attempting to send response after completion");
                }
                return true;
            } else {
                send(mechanism, response);
                msg = receive(socket);
                System.out.println("Received Message " + msg + "from Server");
                if (msg.getStatus() == FAILURE) {
                    System.out.println("Auithentication failed");
                }
            }
        }
        return false;
    }

    private void send(String mechanism, byte[] response) throws IOException {
        if (response == null) {
            response = new byte[0];
        }
        SaslMessage out = new SaslMessage(mechanism, CONTINUE, response);
        System.out.println("Sending Message " + out + " to Server");
        out.send(socket);
    }


}
