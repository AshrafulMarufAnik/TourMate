package com.anik.example.tourmate;

public class Expense {
    private String expenseType,expenseNote,expenseDate,expenseTime;
    private double expenseAmount;

    public Expense() {
    }

    public Expense(String expenseType, String expenseDate, String expenseTime, double expenseAmount) {
        this.expenseType = expenseType;
        this.expenseDate = expenseDate;
        this.expenseTime = expenseTime;
        this.expenseAmount = expenseAmount;
    }

    public Expense(String expenseType, String expenseNote, String expenseDate, String expenseTime, double expenseAmount) {
        this.expenseType = expenseType;
        this.expenseNote = expenseNote;
        this.expenseDate = expenseDate;
        this.expenseTime = expenseTime;
        this.expenseAmount = expenseAmount;
    }

    public String getExpenseType() {
        return expenseType;
    }

    public String getExpenseNote() {
        return expenseNote;
    }

    public String getExpenseDate() {
        return expenseDate;
    }

    public String getExpenseTime() {
        return expenseTime;
    }

    public double getExpenseAmount() {
        return expenseAmount;
    }
}
