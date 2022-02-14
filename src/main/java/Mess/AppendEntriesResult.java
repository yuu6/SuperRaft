package Mess;

public class AppendEntriesResult {
    public final int term;

    public int getTerm() {
        return term;
    }

    @Override
    public String toString() {
        return "AppendEntriesResult{" +
                "term=" + term +
                ", success=" + success +
                '}';
    }

    public boolean isSuccess() {
        return success;
    }

    private final boolean success;


    public AppendEntriesResult(int term, boolean success) {
        this.term = term;
        this.success = success;
    }


}
