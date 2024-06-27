package clientImpl;

import java.util.logging.Logger;

import io.grpc.Channel;
import io.grpc.myexamples.clientstream.ClientStreamGrpc;
import io.grpc.myexamples.clientstream.Reply;
import io.grpc.myexamples.clientstream.Req;
import io.grpc.myexamples.clientstream.ClientStreamGrpc.ClientStreamStub;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientStreamImpl {
    private static final Logger logger = Logger.getLogger(ClientStreamImpl.class.getName());
    // ? Get a async stub
    private final ClientStreamStub asyncStub;

    // ? Constructor for creating the client of type asyncStub
    public ClientStreamImpl(Channel channel1) {
        asyncStub = ClientStreamGrpc.newStub(channel1);
    }

    public int sayMultipleHellos() throws InterruptedException {
        // ? The number of messages that we are sending
        // ? Must be atomic because we are working with threads and this way the main
        // ? thread and the thread of the response can have access to the same variable
        AtomicInteger result = new AtomicInteger(0);
        // ? The stop to wait for the response
        final CountDownLatch finishLatch = new CountDownLatch(1);
        // ? This is where we will receive our response
        StreamObserver<Reply> responseObserver = new StreamObserver<Reply>() {
            int temp_result = 0;

            // ? What we can do with our response
            @Override
            public void onNext(Reply value) {
                System.out.println("Receiving a result");
                temp_result = value.getNumberOfMessages();
            }

            // ? The error that will throw
            @Override
            public void onError(Throwable t) {
                logger.warning("Error when processing this request " + t.getMessage());
                finishLatch.countDown();
            }

            // ? What to do when completed
            @Override
            public void onCompleted() {
                result.set(temp_result);
                finishLatch.countDown();
            }
        };
        // ? Send the object that the server will use to reply to us and get the object
        // ?? to create the request
        StreamObserver<Req> requestObserver = asyncStub.sayMultipleHellos(responseObserver);
        for (int i = 0; i < 6; i++) {
            Req req = Req.newBuilder()
                    .setMessage("Hello world")
                    .build();
            requestObserver.onNext(req);
        }
        requestObserver.onCompleted();
        // ? Make the thread stop for 1 minute to check if we receive the result;
        if (!finishLatch.await(1, TimeUnit.MINUTES)) {
            logger.warning("It did not catch the result within 1 minute");
        }
        // ? Return the result simply
        return result.get();
    }

}
