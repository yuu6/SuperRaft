package com.yuu6.raft.core.node;

public class Address {
    private final String host;

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    private final int port;

    public Address(String host, int port){
//        Precondtions.checkNotNull(host);
        this.host = host;
        this.port = port;
    }

    @Override
    public String toString() {
        return "Address{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
