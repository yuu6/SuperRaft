package Role;


public abstract class AbstractNodeRole {
    private final RoleName name;
    protected final int term;

    AbstractNodeRole(RoleName name, int term){
        this.name = name;
        this.term = term;
    }
    public RoleName getName(){
        return name;
    }
    public abstract void cancelTimeoutOrTask();

    public int getTerm(){
        return term;
    }

}
