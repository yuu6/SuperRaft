package com.yuu6;


import com.yuu6.node.Address;
import com.yuu6.node.NodeEndpoint;
import com.yuu6.node.NodeId;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: yuu6
 * @Date: 2022/03/27/下午3:28
 */
class NodeBuilderTest {

    private Map<String, NodeEndpoint> nodeMap(){
        NodeEndpoint a = new NodeEndpoint(new NodeId("A"), new Address("localhost", 7001));
        NodeEndpoint b = new NodeEndpoint(new NodeId("B"), new Address("localhost", 7002));
        NodeEndpoint c = new NodeEndpoint(new NodeId("C"), new Address("localhost", 7003));
        return new HashMap<String, NodeEndpoint>(){{
            put("a", a);
            put("b", b);
            put("c", c);
        }};
    }
}