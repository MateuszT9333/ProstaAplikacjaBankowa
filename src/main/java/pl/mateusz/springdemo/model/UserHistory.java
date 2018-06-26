package pl.mateusz.springdemo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class UserHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long userId;
    private String typeOfTransaction; //payment, payoff, transfer
    private double balanceAfterTransaction;
    private Date date;
    private double amount;
    private String rawData;

    public UserHistory() {
    }

    public UserHistory(String typeOfTransaction
            , double balanceAfterTransaction
            , Date date
            , double amount
            , Long userId) throws ParseException {
        this.typeOfTransaction = typeOfTransaction;
        this.balanceAfterTransaction = balanceAfterTransaction;
        this.date = date;
        this.amount = amount;
        this.userId = userId;
        SimpleDateFormat parser = new SimpleDateFormat("yyyy_MM_dd.HH:mm:ss");
        this.rawData = String.format("%s_%s_%s_%s_%s"
                , parser.format(date)
                , userId
                , typeOfTransaction
                , balanceAfterTransaction
                , String.valueOf(amount));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTypeOfTransaction() {
        return typeOfTransaction;
    }

    public void setTypeOfTransaction(String typeOfTransaction) {
        this.typeOfTransaction = typeOfTransaction;
    }

    public double getBalanceAfterTransaction() {
        return balanceAfterTransaction;
    }

    public void setBalanceAfterTransaction(double balanceAfterTransaction) {
        this.balanceAfterTransaction = balanceAfterTransaction;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }
}
