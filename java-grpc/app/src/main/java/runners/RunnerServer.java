package runners;

import java.io.IOException;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import serverImpl.*;
import io.grpc.services.AdminInterface;

public class RunnerServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        //? Simply create a server
        final Server server = Grpc.newServerBuilderForPort(2000, InsecureServerCredentials.create())
                .addService(new ServerImpl())
                .addServices(AdminInterface.getStandardServices())
                .build()
                .start();
        System.out.println("Running the server in localhost:2000");
        server.awaitTermination();
    }
}
