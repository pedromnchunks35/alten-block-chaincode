package serverImpl;

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
import io.grpc.myexamples.bidirecional.BidirecionalStreamGrpc;
import io.grpc.myexamples.bidirecional.ReplyBidirecional;
import io.grpc.myexamples.bidirecional.BidirecionalStreamGrpc.BidirecionalStreamStub;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import java.util.concurrent.atomic.*;
import java.util.logging.*;

@RunWith(JUnit4.class)
public class BidirecionalServerImplTest {
    GrpcCleanupRule cleanup = new GrpcCleanupRule();
    Server server;
    ManagedChannel channel;
    private static final Logger logger = Logger.getLogger(BidirecionalServerImplTest.class.getName());

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
                InProcessChannelBuilder.forName(server_name)
                        .directExecutor()
                        .build());
    }

    @After
    public void clean() {
        server.shutdown();
        channel.shutdown();
    }

    @Test
    public void sendNumbersToEachOther_test() {
        BidirecionalStreamStub asyncStub = BidirecionalStreamGrpc.newStub(channel);
        AtomicInteger number_of_numbers = new AtomicInteger(0);
        
        new StreamObserver<ReplyBidirecional>() {

            @Override
            public void onNext(ReplyBidirecional value) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'onNext'");
            }

            @Override
            public void onError(Throwable t) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'onError'");
            }

            @Override
            public void onCompleted() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'onCompleted'");
            }

        };
    }
}
