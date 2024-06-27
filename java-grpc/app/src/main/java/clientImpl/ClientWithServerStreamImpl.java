package clientImpl;

import java.util.Iterator;

import io.grpc.Channel;
import io.grpc.myexamples.serverstream.ReplyServerStream;
import io.grpc.myexamples.serverstream.ReqServerStream;
import io.grpc.myexamples.serverstream.ServerStreamGrpc;
import io.grpc.myexamples.serverstream.ServerStreamGrpc.ServerStreamBlockingStub;

public class ClientWithServerStreamImpl {
    private final ServerStreamBlockingStub blockingStub;

    public ClientWithServerStreamImpl(Channel channel) {
        blockingStub = ServerStreamGrpc.newBlockingStub(channel);
    }

    public int getLengthNumbersServer() {
        //? Simply invoke the method
        Iterator<ReplyServerStream> reply = blockingStub.requestThrowOfNumbers(
                ReqServerStream.newBuilder()
                        .setMessage("Give me a number")
                        .build());
        int number_of_numbers = 0;
        while (reply.hasNext()) {
            number_of_numbers++;
            reply.next();
        }
        return number_of_numbers;
    }
}
