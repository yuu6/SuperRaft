package Role;

import Election.LogReplicationTask;

public class LeaderNodeRole extends AbstractNodeRole{

    private final LogReplicationTask logReplicationTask;// 日志复制定时器

    public LeaderNodeRole(int term, LogReplicationTask logReplicationTask){
        super(RoleName.LEADER, term);
        this.logReplicationTask = logReplicationTask;
    }

    @Override
    public String toString() {
        return "LeaderNodeRole{" +
                "term=" + term +
                ", logReplicationTask=" + logReplicationTask +
                '}';
    }

    @Override
    public void cancelTimeoutOrTask() {
        logReplicationTask.cancel();
    }
}
