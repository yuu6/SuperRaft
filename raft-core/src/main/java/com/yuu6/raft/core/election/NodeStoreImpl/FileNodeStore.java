//package com.yuu6.Election.NodeStoreImpl;
//
//import com.yuu6.Election.NodeStore;
//import com.yuu6.Node.NodeId;
//import com.google.common.io.Files;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.RandomAccessFile;
//
//
//public class FileNodeStore implements NodeStore {
//    private static final String FILE_NAME="node.bin";
//    private static final long OFFSET_TERM = 0;
//    private static final long OFFSET_VOTED_FOR = 4;
//    private final SeekableFile seekableFile;
//    private int term = 0;
//    private NodeId votedFor = null;
//
//    public FileNodeStore(File file){
//        try {
//            if (!file.exists()){
//                Files.touch(file);
//            }
//            seekableFile = new RandomAccessFileAdapter(file);
//            initializeOrLoad();
//        }catch (IOException e){
//            throw new NodeStoreExecption(e);
//        }
//    }
//
//    @Override
//    public int getTerm() {
//        return 0;
//    }
//
//    @Override
//    public void setTerm(int term) {
//
//    }
//
//    @Override
//    public NodeId getVotedFor() {
//        return null;
//    }
//
//    @Override
//    public void setVotedFor(NodeId nodeId) {
//
//    }
//
//    @Override
//    public void close() {
//
//    }
//}
