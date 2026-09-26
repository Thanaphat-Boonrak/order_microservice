package com.thanaphat2005.food.ordering.system.messaging;

public enum DebeziumOp {

    CREATE("c"),UPDATE("u"),DELETE("d");
    private final String op;
    DebeziumOp(String op) {
        this.op = op;
    }

    public String getOp() {
        return op;
    }
}
