# Setup
- Download protoc binary
- Simply config the gradle.build file which is in groovy language
- Mockito is not working for some reason
- Either way we can do unit testing
# Client-Server Arch (unilateral call)
## Server
- [Impl](./app/src/main/java/serverImpl/ServerImpl.java)
- [Test](./app/src/test/java/serverImpl/ServerImplTest.java)
## Client
- [Impl](./app/src/main/java/clientImpl/ClientImpl.java)
- [Test](./app/src/test/java/clientImpl/ClientImplTest.java)
## Runners
- [Server](./app/src/main/java/runners/RunnerServer.java)
- [Client](./app/src/main/java/runners/RunnerClient.java)
# Client side stream for Server 
## Server
- [IMPL](./app/src/main/java/serverImpl/ClientStreamServerImpl.java)
- [Test](./app/src/test/java/serverImpl/ClientStreamServerImplTest.java)
## Client
- [IMPL](./app/src/main/java/clientImpl/ClientStreamImpl.java)
- [Test](./app/src/test/java/clientImpl/ClientStreamImplTest.java)
## Runners
- [Server](./app/src/main/java/runners/RunnerServerStream.java)
- [Client](./app/src/main/java/runners/RunnerClientStream.java)