package runner;

import java.io.IOException;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import serverImpl.*;
import io.grpc.services.AdminInterface;

public class Runner {
    public static void main(String[] args) throws IOException, InterruptedException {
        final Server server = Grpc.newServerBuilderForPort(2000, InsecureServerCredentials.create())
                .addService(new ServerImpl())
                .addServices(AdminInterface.getStandardServices())
                .build()
                .start();
        System.out.println("Hello world");
        server.awaitTermination();
    }
}
