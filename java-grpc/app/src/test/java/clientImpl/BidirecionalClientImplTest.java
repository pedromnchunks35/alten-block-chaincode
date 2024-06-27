package clientImpl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import serverImpl.BidirecionalServerImpl;

@RunWith(JUnit4.class)
public class BidirecionalClientImplTest {
    GrpcCleanupRule cleanup = new GrpcCleanupRule();
    Server server;
    ManagedChannel channel;
    BidirecionalClientImpl client;

    @Before
    public void setUp() throws IOException {
        String server_name = InProcessServerBuilder.generateName();
        server = cleanup.register(
                InProcessServerBuilder
                        .forName(server_name)
                        .directExecutor()
                        .addService(new BidirecionalServerImpl())
                        .build()
                        .start());
        channel = cleanup.register(
                InProcessChannelBuilder
                        .forName(server_name)
                        .directExecutor()
                        .build());
        client = new BidirecionalClientImpl(channel);
    }

    @After
    public void clean() {
        channel.shutdown();
        server.shutdown();
    }

    @Test
    public void sendNumberToEachOther_Test() throws InterruptedException {
        int result = client.sendNumbersAndReceiveNumbers();
        assertEquals(5, result);
    }
}
