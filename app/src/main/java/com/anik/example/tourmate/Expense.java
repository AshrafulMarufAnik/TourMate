package com.anik.example.tourmate;

public class Expense {
    private String expenseType,expenseNote,expenseDate,expenseTime,expenseID;
    private double expenseAmount,totalExpenseAmount;

    public Expense() {
    }

    public Expense(String expenseType, String expenseNote, String expenseDate, String expenseTime, String expenseID, double expenseAmount) {
        this.expenseType = expenseType;
        this.expenseNote = expenseNote;
        this.expenseDate = expenseDate;
        this.expenseTime = expenseTime;
        this.expenseID = expenseID;
        this.expenseAmount = expenseAmount;
    }

    public Expense(String expenseType, String expenseDate, String expenseTime, String expenseID, double expenseAmount, double totalExpenseAmount) {
        this.expenseType = expenseType;
        this.expenseDate = expenseDate;
        this.expenseTime = expenseTime;
        this.expenseID = expenseID;
        this.expenseAmount = expenseAmount;
        this.totalExpenseAmount = totalExpenseAmount;
    }

    public String getExpenseID() { return expenseID; }

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

    public double getTotalExpenseAmount() { return totalExpenseAmount; }
}
