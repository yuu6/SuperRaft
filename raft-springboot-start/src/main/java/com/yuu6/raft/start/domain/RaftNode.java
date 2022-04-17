package com.yuu6.raft.start.domain;

import lombok.Data;

/**
 * @Author: yuu6
 * @Date: 2022/04/16/下午7:14
 */
@Data
public class RaftNode {
    private String name;
    private String host;
    private Integer port;
}
