package Role;

import Election.ElectionTimeout;

public class CandidateNodeRole extends AbstractNodeRole{
    private final int votesCount; // 票数
    private final ElectionTimeout electionTimeout; // 选举超时

    // 预设了投票数1
    public CandidateNodeRole(int term, ElectionTimeout electionTimeout){
        this(term, 1, electionTimeout);
    }

    // 构造函数，可以指定票数
    public CandidateNodeRole(int term, int votesCount, ElectionTimeout electionTimeout){
        super(RoleName.CANDIDATE, term);
        this.votesCount = votesCount;
        this.electionTimeout = electionTimeout;
    }

    public int getVotesCount(){
        return votesCount;
    }
    @Override
    public void cancelTimeoutOrTask() {
        electionTimeout.cancel();
    }

    @Override
    public String toString() {
        return "CandidateNodeRole{" +
                "term=" + term +
                ", votesCount=" + votesCount +
                ", electionTimeout=" + electionTimeout +
                '}';
    }

    public CandidateNodeRole increasesVotesCount(ElectionTimeout electionTimeout){
        this.electionTimeout.cancel();
        return new CandidateNodeRole(term, votesCount + 1, electionTimeout);
    }
}
