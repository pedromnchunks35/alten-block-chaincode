package clientImpl;

import java.util.logging.Logger;

import io.grpc.Channel;
import io.grpc.StatusRuntimeException;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;

public class ClientImpl {
    // ? Logger by some unknown reason yet
    private static final Logger logger = Logger.getLogger(GreeterGrpc.class.getName());
    // ? This is one type of client
    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    // ? Creating a client using a channel
    public ClientImpl(Channel channel) {
        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    /**
     * @param name, just a simple name for the server to reply back
     * @return The message from the server
     * @description This is simply for the client to send a greet and getting
     *              something back and nothing else
     */
    public String greet(String name) throws StatusRuntimeException {
        logger.info("Trying to greet with " + name);
        // ? Build the request
        HelloRequest req = HelloRequest.newBuilder()
                .setName(name)
                .build();
        // ? Sending the request
        HelloReply reply = blockingStub.sayHello(req);
        return reply.getMessage();
    }
}
