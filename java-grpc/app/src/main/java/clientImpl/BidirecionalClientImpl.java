package clientImpl;

import io.grpc.Channel;
import io.grpc.myexamples.bidirecional.BidirecionalStreamGrpc;
import io.grpc.myexamples.bidirecional.ReplyBidirecional;
import io.grpc.myexamples.bidirecional.ReqBidirecional;
import io.grpc.stub.StreamObserver;
import io.grpc.myexamples.bidirecional.BidirecionalStreamGrpc.BidirecionalStreamStub;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CountDownLatch;
import java.util.logging.*;
import java.util.concurrent.TimeUnit;

public class BidirecionalClientImpl {
    private static final Logger logger = Logger.getLogger(BidirecionalClientImpl.class.getName());
    private BidirecionalStreamStub asyncStub;

    public BidirecionalClientImpl(Channel channel) {
        asyncStub = BidirecionalStreamGrpc.newStub(channel);
    }

    public int sendNumbersAndReceiveNumbers() throws InterruptedException {
        // ? The result which is counting the number of numbers received
        AtomicInteger number_of_numbers_received = new AtomicInteger(0);
        // ? The stop to wait for the response
        final CountDownLatch finishLatch = new CountDownLatch(1);
        // ? For receiving the message
        StreamObserver<ReplyBidirecional> reply = new StreamObserver<ReplyBidirecional>() {

            @Override
            public void onNext(ReplyBidirecional value) {
                number_of_numbers_received.incrementAndGet();
            }

            @Override
            public void onError(Throwable t) {
                logger.warning("An error occured " + t.getMessage());
                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                finishLatch.countDown();
            }
        };
        // ? Make the normal stream request
        StreamObserver<ReqBidirecional> request = asyncStub.throwNumbersToEachOther(reply);
        for (int i = 0; i < 5; i++) {
            request.onNext(
                    ReqBidirecional.newBuilder()
                            .setNumber("lul")
                            .build());
        }
        request.onCompleted();
        // ? Wait for the response back
        if (!finishLatch.await(1, TimeUnit.MINUTES)) {
            logger.warning("It did not catch the result within 1 minute");
        }
        // ? Return number of numbers counted
        return number_of_numbers_received.get();
    }
}
