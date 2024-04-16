package com.d2d.dto;

public class TableRow {
    private String column1;
    private int column2;

    public String getColumn1() {
        return column1;
    }

    public void setColumn1(String column1) {
        this.column1 = column1;
    }

    public int getColumn2() {
        return column2;
    }

    public void setColumn2(int column2) {
        this.column2 = column2;
    }

    @Override
    public String toString() {
        return "Column1: " + column1 + ", Column2: " + column2;
    }
}
