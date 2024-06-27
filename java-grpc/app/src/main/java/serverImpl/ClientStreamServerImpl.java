package serverImpl;

import io.grpc.myexamples.clientstream.Reply;
import io.grpc.myexamples.clientstream.Req;
import io.grpc.myexamples.clientstream.ClientStreamGrpc.ClientStreamImplBase;
import io.grpc.stub.StreamObserver;

import java.util.logging.Logger;
import java.util.logging.Level;

public class ClientStreamServerImpl extends ClientStreamImplBase {
    private static final Logger logger = Logger.getLogger(ClientStreamServerImpl.class.getName());

    @Override
    public StreamObserver<Req> sayMultipleHellos(StreamObserver<Reply> responseObserver) {
        // ? We must return what happens in that stream
        return new StreamObserver<Req>() {
            public int number_of_messages = 0;

            // ? Simply increment a value when there is a new message
            @Override
            public void onNext(Req value) {
                System.out.println("Another value!!");
                number_of_messages++;
            }

            // ? Throw a error when there is a error
            @Override
            public void onError(Throwable t) {
                logger.log(Level.WARNING, "Something went wrong");
            }

            // ? Return number of messages
            @Override
            public void onCompleted() {
                // ? Build the response
                Reply reply = Reply.newBuilder()
                        .setNumberOfMessages(number_of_messages)
                        .build();
                // ? Send the message
                responseObserver.onNext(reply);
                // ? Close connection
                responseObserver.onCompleted();
            }

        };
    }
}
