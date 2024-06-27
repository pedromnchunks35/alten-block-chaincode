package clientImpl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import serverImpl.ClientServerStreamImpl;

@RunWith(JUnit4.class)
public class ClientWithServerStreamImplTest {
    GrpcCleanupRule cleanup = new GrpcCleanupRule();
    Server server;
    ClientWithServerStreamImpl client;
    ManagedChannel channel;

    @Before
    public void setup() throws IOException {
        String server_name = InProcessServerBuilder.generateName();
        server = cleanup.register(
                InProcessServerBuilder
                        .forName(server_name)
                        .directExecutor()
                        .addService(new ClientServerStreamImpl())
                        .build()
                        .start());
        channel = cleanup.register(
                InProcessChannelBuilder
                        .forName(server_name)
                        .directExecutor()
                        .build());
        client = new ClientWithServerStreamImpl(channel);
    }

    @After
    public void clean() {
        server.shutdown();
        channel.shutdown();
    }

    @Test
    public void requestThrowNumbers_test() {
        int response = client.getLengthNumbersServer();
        assertEquals(response, 5);
    }
}
