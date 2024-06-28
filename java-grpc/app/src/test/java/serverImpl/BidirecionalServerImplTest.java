package serverImpl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

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
import io.grpc.myexamples.bidirecional.BidirecionalStreamGrpc;
import io.grpc.myexamples.bidirecional.BidirecionalStreamGrpc.BidirecionalStreamStub;
import io.grpc.myexamples.bidirecional.ReplyBidirecional;
import io.grpc.myexamples.bidirecional.ReqBidirecional;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;

@RunWith(JUnit4.class)
public class BidirecionalServerImplTest {

    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();
    private Server server;
    private ManagedChannel channel;
    private static final Logger logger = Logger.getLogger(BidirecionalServerImplTest.class.getName());
    private CountDownLatch latch;

    @Before
    public void setUp() throws IOException {
        String serverName = InProcessServerBuilder.generateName();
        server = grpcCleanup.register(
                InProcessServerBuilder
                        .forName(serverName)
                        .directExecutor()
                        .addService(new BidirecionalServerImpl())
                        .build()
                        .start());
        channel = grpcCleanup.register(
                InProcessChannelBuilder.forName(serverName)
                        .directExecutor()
                        .build());
        latch = new CountDownLatch(1);
    }

    @After
    public void clean() {
        if (server != null) {
            server.shutdown();
        }
        if (channel != null) {
            channel.shutdown();
        }
    }

    @Test
    public void throwNumberToEachOther_test() throws InterruptedException {
        BidirecionalStreamStub asyncStub = BidirecionalStreamGrpc.newStub(channel);
        AtomicInteger numberOfNumbers = new AtomicInteger(0);
        StreamObserver<ReplyBidirecional> reply = new StreamObserver<ReplyBidirecional>() {

            @Override
            public void onNext(ReplyBidirecional value) {
                numberOfNumbers.incrementAndGet();
            }

            @Override
            public void onError(Throwable t) {
                logger.warning("Error while receiving the data: " + t.getMessage());
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                logger.info("Done receiving all of the data");
                latch.countDown();
            }

        };
        StreamObserver<ReqBidirecional> req = asyncStub.throwNumbersToEachOther(reply);
        for (int i = 0; i < 5; i++) {
            req.onNext(ReqBidirecional.newBuilder()
                    .setNumber("Hello")
                    .build());
        }
        req.onCompleted();
        if (!latch.await(1, TimeUnit.MINUTES)) {
            logger.warning("We cannot be here waiting more than 1 minute");
        }
        assertEquals(5, numberOfNumbers.get());
    }
}
