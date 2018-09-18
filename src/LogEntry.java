public class LogEntry {
    private String operation="",state="";
    public LogEntry(){}
    public LogEntry(String operation, String state){
        this.operation=operation;
        this.state=state;
    }

    public String getOperation() {
        return operation;
    }

    public String getState() {
        return state;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setState(String state) {
        this.state = state;
    }
}
