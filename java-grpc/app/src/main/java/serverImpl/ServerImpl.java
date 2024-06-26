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
        HelloReply reply = HelloReply.newBuilder()
                .setMessage("Hello world")
                .build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
