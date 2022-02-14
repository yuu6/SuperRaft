package Election;

import Mess.AppendEntriesResult;
import Mess.AppendEntriesRpc;
import Mess.RequestVoteResult;
import Mess.RequestVoteRpc;
import Node.NodeEndpoint;

import java.util.Collection;

public interface Connector {

    void initialize();

    void sendRequestVote(RequestVoteRpc rpc, Collection<NodeEndpoint> destinationEndpoints);

    void replyRequestVote(RequestVoteResult result, NodeEndpoint destinationEndpoints);

    void sendAppendEntries(AppendEntriesRpc rpc, NodeEndpoint destinationEndpoint);

    void replyAppendEntries(AppendEntriesResult result, NodeEndpoint destinationEndpoint);

    void close();
}
