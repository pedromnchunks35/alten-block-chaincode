package serverImpl;

import io.grpc.myexamples.serverstream.ReplyServerStream;
import io.grpc.myexamples.serverstream.ReqServerStream;
import io.grpc.myexamples.serverstream.ServerStreamGrpc.ServerStreamImplBase;
import io.grpc.stub.StreamObserver;

public class ClientServerStreamImpl extends ServerStreamImplBase {
    // ? This one simply receives one message and sends all of the numbers by a
    // stream observer
    @Override
    public void requestThrowOfNumbers(ReqServerStream req, StreamObserver<ReplyServerStream> reply) {
        String message = req.getMessage();
        System.out.println("Just received the message: " + message);
        for (int i = 0; i < 5; i++) {
            reply.onNext(
                    ReplyServerStream.newBuilder()
                            .setNumber(i)
                            .build());
        }
        reply.onCompleted();
    }
}
