package upt.pcbe.messaging.test;

import org.junit.jupiter.api.Test;

import upt.pcbe.messaging.server.Server;

class ClientsTest {

    static final Integer CLIENT_TEST = 20;
    
    @Test
    void test() {
        new Thread() {
            public void run() {
                Server.main(null);
            }
        }.start();
        for (int i = 0; i < CLIENT_TEST; i++) {
            final int id = i;
            // Thread t = new Thread() {
            // public void run() {
            // Client.main(null);
            // String[] args = { String.valueOf(id) }; // client ID
            // ClientMainTest.main(args);
            // }
            // };
            Thread t = new Thread(new ClientThreadTest(i));
            t.setName("Client " + i);
            t.start();
        }

        String[] args = { CLIENT_TEST.toString() }; // client ID
        ClientMainTest.main(args);

    }

}
