package serverImpl;

import java.util.concurrent.atomic.AtomicInteger;

import io.grpc.myexamples.bidirecional.ReplyBidirecional;
import io.grpc.myexamples.bidirecional.ReqBidirecional;
import java.util.logging.*;
import io.grpc.myexamples.bidirecional.BidirecionalStreamGrpc.BidirecionalStreamImplBase;
import io.grpc.stub.StreamObserver;

public class BidirecionalServerImpl extends BidirecionalStreamImplBase {
    private static final Logger logger = Logger.getLogger(BidirecionalServerImpl.class.getName());

    @Override
    public StreamObserver<ReqBidirecional> throwNumbersToEachOther(StreamObserver<ReplyBidirecional> reqObserver) {
        return new StreamObserver<ReqBidirecional>() {
            AtomicInteger result = new AtomicInteger(0);

            @Override
            public void onNext(ReqBidirecional value) {
                result.set(result.get() + 1);
            }

            @Override
            public void onError(Throwable t) {
                logger.warning("Something went wrong " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                // ? On complete send our message stream as well
                for (int i = 0; i < 5; i++) {
                    reqObserver.onNext(
                            ReplyBidirecional.newBuilder()
                                    .setNumber(i)
                                    .build());
                }
                reqObserver.onCompleted();
            }

        };
    }
}
