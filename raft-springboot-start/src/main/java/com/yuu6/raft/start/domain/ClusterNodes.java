package com.yuu6.raft.start.domain;

import lombok.Data;

import java.util.List;

/**
 * @Author: yuu6
 * @Date: 2022/04/16/下午7:01
 */
@Data
public class ClusterNodes {
    List<RaftNode> nodeList;
    String selfName;
}
