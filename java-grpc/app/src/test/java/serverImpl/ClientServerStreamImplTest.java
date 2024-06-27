package serverImpl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.myexamples.serverstream.ReplyServerStream;
import io.grpc.myexamples.serverstream.ReqServerStream;
import io.grpc.myexamples.serverstream.ServerStreamGrpc;
import io.grpc.myexamples.serverstream.ServerStreamGrpc.ServerStreamBlockingStub;
import io.grpc.testing.GrpcCleanupRule;

@RunWith(JUnit4.class)
public class ClientServerStreamImplTest {
    GrpcCleanupRule cleanup = new GrpcCleanupRule();
    Server server;
    ManagedChannel channel;

    @Before
    public void setUp() throws IOException {
        String server_name = InProcessServerBuilder.generateName();
        server = cleanup.register(
                InProcessServerBuilder
                        .forName(server_name)
                        .directExecutor()
                        .addService(new ClientServerStreamImpl())
                        .build()
                        .start());
        channel = cleanup.register(InProcessChannelBuilder
                .forName(server_name)
                .directExecutor()
                .build());
    }

    @After
    public void clean() {
        server.shutdown();
        channel.shutdown();
    }

    @Test
    public void throwNumbersFunctionServer_test() {
        ServerStreamBlockingStub blockingStub = ServerStreamGrpc.newBlockingStub(channel);
        Iterator<ReplyServerStream> reply = blockingStub.requestThrowOfNumbers(
                ReqServerStream.newBuilder()
                        .setMessage("My numbers give me them")
                        .build());
        int number_of_numbers = 0;
        int sum_of_numbers = 0;
        while (reply.hasNext()) {
            number_of_numbers++;
            sum_of_numbers += reply.next().getNumber();
        }
        assertEquals(number_of_numbers, 5);
        assertEquals(10, sum_of_numbers);
    }

}
