package runner;

import clientImpl.ClientImpl;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

public class RunnerClient {
    public static void main(String[] args) {
        String name = "Elsa";
        String target = "localhost:2000";
        // ? Create a channel
        ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create())
                .build();
        // ? Create the client
        ClientImpl client = new ClientImpl(channel);
        // ? Simply send the request
        String response = client.greet(name);
        System.out.println(response);
    }
}
