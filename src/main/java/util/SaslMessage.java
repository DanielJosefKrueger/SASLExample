package util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class SaslMessage implements Serializable {

    private final STATUS status;
    private final String mechanism;
    private final byte[] content;
    public SaslMessage(String mechanism, STATUS status, byte[] content) {
        this.status = status;
        this.content = content;
        this.mechanism = mechanism;
    }

    public static SaslMessage receive(Socket socket) throws IOException, ClassNotFoundException, InterruptedException {
        // Thread.sleep(300); // give server time to react
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        Object o = in.readObject();
        if (o instanceof SaslMessage) {
            return (SaslMessage) o;
        }
        throw new RuntimeException("Couldnt get SalssMessage");
    }

    @Override
    public String toString() {
        return "SaslMessage{" +
                "status=" + status +
                ", mechanism='" + mechanism + '\'' +
                ", content=" + new String(content) +
                '}';
    }

    public byte[] getContent() {
        return this.content;
    }


    public STATUS getStatus() {
        return this.status;
    }

    public void send(Socket socket) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(this);
        out.flush();

    }

    public enum STATUS implements Serializable {

        CONTINUE(1),
        SUCCESS(2),
        FAILURE(3);

        private final int index;

        STATUS(int index) {
            this.index = index;
        }

        public static STATUS ofIndex(int index) {
            for (STATUS status : STATUS.values()) {
                if (status.getIndex() == index) {
                    return status;
                }
            }
            return null;
        }

        public int getIndex() {
            return index;
        }

    }


}
