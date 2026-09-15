public enum DebeziumOp {

    CREATE("c"),UPDATE("u"),DELETE("d");
    private final String op;
    DebeziumOp(String op) {
        this.op = op;
    }
}
