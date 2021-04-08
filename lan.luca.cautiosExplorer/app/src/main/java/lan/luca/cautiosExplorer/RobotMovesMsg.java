package lan.luca.cautiosExplorer;

public enum RobotMovesMsg {
    forwardMsg("{\"robotmove\":\"moveForward\", \"time\": 350}", "moveForward"),
    backwardMsg("{\"robotmove\":\"moveBackward\", \"time\": 350}", "moveBackward"),
    turnLeftMsg("{\"robotmove\":\"turnLeft\", \"time\": 300}", "turnLeft"),
    turnRightMsg("{\"robotmove\":\"turnRight\", \"time\": 300}", "turnRight"),
    haltMsg("{\"robotmove\":\"alarm\", \"time\": 100}", "alarm");

    private String cmd;
    private String move;

    RobotMovesMsg(String cmd, String move) {
        this.cmd = cmd;
        this.move = move;
    }

    public String getMsg() {
        return cmd;
    }

    public String getMove(){return move;}
}
