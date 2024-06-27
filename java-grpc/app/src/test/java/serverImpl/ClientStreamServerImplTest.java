package serverImpl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import clientImpl.ClientStreamImpl;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;

@RunWith(JUnit4.class)
public class ClientStreamServerImplTest {
    @Rule
    public final GrpcCleanupRule cleanup = new GrpcCleanupRule();
    private Server server;
    private ManagedChannel channel;
    private ClientStreamImpl client;

    @Before
    public void setUp() throws IOException {
        String server_name = InProcessServerBuilder.generateName();
        // ? Create server
        server = cleanup.register(
                InProcessServerBuilder
                        .forName(server_name)
                        .addService(new ClientStreamServerImpl())
                        .build()
                        .start());
        // ? Create a
        channel = cleanup.register(
                InProcessChannelBuilder
                        .forName(server_name)
                        .directExecutor()
                        .build());
        client = new ClientStreamImpl(channel);
    }

    @After
    public void cleanUp() {
        // ? Clean everyhting
        server.shutdown();
        channel.shutdown();
    }

    @Test
    public void sayMultipleHellos_test() throws InterruptedException {
        int result = client.sayMultipleHellos();
        assertEquals(result, 6);
    }
}
