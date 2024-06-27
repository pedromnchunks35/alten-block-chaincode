package serverImpl;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;

/**
 * @description This is simply a test case to check how can we unit testing a
 *              grpc normal service
 */
@RunWith(JUnit4.class)
public class ServerImplTest {
    // ? Cleanup rules that grpc give us for the unit testing
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    @Test
    public void sayHello_test() throws Exception {
        String name = "Elsa";
        // ? This is to generate a name for the server required for some unknown reason
        String serverName = InProcessServerBuilder.generateName();
        // ? Create the fake server which will add a service,start and register for
        // ? automatic graceful shutdown
        grpcCleanup.register(
                InProcessServerBuilder
                        .forName(serverName)
                        .directExecutor()
                        .addService(new ServerImpl())
                        .build()
                        .start());
        // ? Create a fake client
        GreeterGrpc.GreeterBlockingStub blockingStub = GreeterGrpc.newBlockingStub(
                // ? Fake channel inside of this new blocing stub
                grpcCleanup.register(
                        InProcessChannelBuilder
                                .forName(serverName)
                                .directExecutor()
                                .build()));
        // ? Create request
        HelloRequest req = HelloRequest.newBuilder()
                .setName(name)
                .build();
        // ? Send the request
        HelloReply reply = blockingStub.sayHello(req);
        assertEquals(reply.getMessage(), "The message you just sent me was: " + name);
    }
}
