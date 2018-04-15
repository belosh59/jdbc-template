package com.belosh.jdbctemplate.entity;

import java.time.LocalDateTime;

public class TestVO {
    private boolean fieldBoolean;
    private int fieldInt;
    private double fieldDouble;
    private float fieldFloat;
    private String fieldText;
    private LocalDateTime fieldDate;

    public boolean isFieldBoolean() {
        return fieldBoolean;
    }

    public void setFieldBoolean(boolean fieldBoolean) {
        this.fieldBoolean = fieldBoolean;
    }

    public int getFieldInt() {
        return fieldInt;
    }

    public void setFieldInt(int fieldInt) {
        this.fieldInt = fieldInt;
    }

    public double getFieldDouble() {
        return fieldDouble;
    }

    public void setFieldDouble(double fieldDouble) {
        this.fieldDouble = fieldDouble;
    }

    public float getFieldFloat() {
        return fieldFloat;
    }

    public void setFieldFloat(float fieldFloat) {
        this.fieldFloat = fieldFloat;
    }

    public String getFieldText() {
        return fieldText;
    }

    public void setFieldText(String fieldText) {
        this.fieldText = fieldText;
    }

    public LocalDateTime getFieldDate() {
        return fieldDate;
    }

    public void setFieldDate(LocalDateTime fieldDate) {
        this.fieldDate = fieldDate;
    }

    @Override
    public String toString() {
        return "TestVO{" +
                "fieldBoolean=" + fieldBoolean +
                ", fieldInt=" + fieldInt +
                ", fieldDouble=" + fieldDouble +
                ", fieldFloat=" + fieldFloat +
                ", fieldText='" + fieldText + '\'' +
                ", fieldDate=" + fieldDate +
                '}';
    }
}
