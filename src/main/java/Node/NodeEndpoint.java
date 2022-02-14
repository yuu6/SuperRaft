package Node;

public class NodeEndpoint {
    public NodeId getId() {
        return id;
    }

    private final NodeId id;

    public Address getAddress() {
        return address;
    }

    private final Address address;

    private NodeEndpoint(String id, String host, int port){
        this(new NodeId(id), new Address(host, port));
    }

    public NodeEndpoint(NodeId id, Address address){
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(address);
        this.id = id;
        this.address = address;
    }

}
