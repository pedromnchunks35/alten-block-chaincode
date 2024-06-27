package clientImpl;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import serverImpl.ServerImpl;

@RunWith(JUnit4.class)
public class ClientImplTest {
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();
    // ? Client instance
    private ClientImpl client;

    @Before
    public void setUp() throws Exception {
        // ? Create our fake server
        String server_name = InProcessServerBuilder.generateName();
        grpcCleanup.register(
                InProcessServerBuilder
                        .forName(server_name)
                        .addService(new ServerImpl())
                        .directExecutor()
                        .build()
                        .start());
        // ? Create fake channel
        ManagedChannel channel = grpcCleanup.register(
                InProcessChannelBuilder
                        .forName(server_name)
                        .directExecutor()
                        .build());
        // ? Create client
        client = new ClientImpl(channel);
    }

    @Test
    public void greet_test() throws Exception {
        String name = "Elsa";
        String response = client.greet(name);
        assertEquals(response, "The message you just sent me was: " + name);
    }
}
