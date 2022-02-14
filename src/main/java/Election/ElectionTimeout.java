package Election;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ElectionTimeout {
    private final ScheduledFuture<?> scheduledFuture;

    public ElectionTimeout(ScheduledFuture<?> scheduledFuture){
        this.scheduledFuture = scheduledFuture;
    }

    public void cancel(){
        this.scheduledFuture.cancel(false);
    }

    @Override
    public String toString() {
        // 选举超时已取消
        if (this.scheduledFuture.isCancelled()){
            return "ElectionTimeout(state=cancelled)";
        }

        // 选举超时已执行
        if (this.scheduledFuture.isDone()){
            return "ElectionTimeout(state=done)";
        }

        return "ElectionTimeout {delay="+
                scheduledFuture.getDelay(TimeUnit.MILLISECONDS) + "ms}";
    }
}
