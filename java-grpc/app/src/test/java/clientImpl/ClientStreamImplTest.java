package clientImpl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import serverImpl.ClientStreamServerImpl;

@RunWith(JUnit4.class)
public class ClientStreamImplTest {
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();
    Server server;
    ClientStreamImpl client;
    ManagedChannel channel;

    @Before
    public void setUp() throws IOException {
        String server_name = InProcessServerBuilder.generateName();
        server = grpcCleanup.register(
                InProcessServerBuilder
                        .forName(server_name)
                        .directExecutor()
                        .addService(new ClientStreamServerImpl())
                        .build()
                        .start());
        channel = grpcCleanup.register(
                InProcessChannelBuilder
                        .forName(server_name)
                        .directExecutor()
                        .build());
        client = new ClientStreamImpl(channel);
    }

    @After
    public void clean() {
        server.shutdown();
        channel.shutdown();
    }

    @Test
    public void clientsayMultipleHellos_test() throws InterruptedException {
        int result = client.sayMultipleHellos();
        assertEquals(result, 6);
    }
}
