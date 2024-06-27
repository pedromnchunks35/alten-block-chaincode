package serverImpl;

import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.examples.helloworld.GreeterGrpc.GreeterImplBase;
import io.grpc.stub.StreamObserver;

public class ServerImpl extends GreeterImplBase {
    public ServerImpl() {
    }

    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
        // ? GET THE REQUEST
        String clientMessage = req.getName();
        // ? Construct the reply
        HelloReply reply = HelloReply.newBuilder()
                .setMessage("The message you just sent me was: " + clientMessage)
                .build();
        // ? Send it
        responseObserver.onNext(reply);
        // ? Mark it as completed
        responseObserver.onCompleted();
    }
}
