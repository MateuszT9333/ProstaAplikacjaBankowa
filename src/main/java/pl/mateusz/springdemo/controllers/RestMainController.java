package pl.mateusz.springdemo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.mateusz.springdemo.OperationType;
import pl.mateusz.springdemo.TransactionTypes;
import pl.mateusz.springdemo.model.UserHistory;
import pl.mateusz.springdemo.model.UserLogPass;
import pl.mateusz.springdemo.repositories.UserHistoryRepository;
import pl.mateusz.springdemo.repositories.UserLogPassRepository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class RestMainController {

    @Autowired
    private UserLogPassRepository userLogPassRepository;
    @Autowired
    private UserHistoryRepository userHistoryRepository;

    @GetMapping("/rest")
    public String initializeDatabase() throws ParseException {
        userHistoryRepository.deleteAllInBatch();
        userLogPassRepository.deleteAllInBatch();

        userLogPassRepository.save(new UserLogPass("admin", "admin"));
        userLogPassRepository.save(new UserLogPass("mateusz", "mateusz"));
        userLogPassRepository.save(new UserLogPass("kasia", "kasia"));
        userLogPassRepository.save(new UserLogPass("karol", "karol"));
        userLogPassRepository.save(new UserLogPass("andrzej", "andrzej"));

        userHistoryRepository
                .save(new UserHistory(TransactionTypes.TRANSFER.toString()
                , 100
                , new Date()
                , 0
                , 1L));

        userHistoryRepository
                .save(new UserHistory(TransactionTypes.TRANSFER.toString()
                        , 100
                        , new Date()
                        , 0
                        , 2L));
        userHistoryRepository
                .save(new UserHistory(TransactionTypes.TRANSFER.toString()
                        , 100
                        , new Date()
                        , 0
                        , 3L));
        userHistoryRepository
                .save(new UserHistory(TransactionTypes.TRANSFER.toString()
                        , 100
                        , new Date()
                        , 0
                        , 4L));
        userHistoryRepository
                .save(new UserHistory(TransactionTypes.TRANSFER.toString()
                        , 100
                        , new Date()
                        , 0
                        , 5L));

        return "Data Loaded";
    }
    @GetMapping("/restUserLogin")
    public long login(@RequestParam String login, @RequestParam String password){
        UserLogPass requestedUser = new UserLogPass(login, password);
        List<UserLogPass> userLogPassList = userLogPassRepository.findAll();

        for(UserLogPass userLogPassItem : userLogPassList){
            if(requestedUser.equals(userLogPassItem)){
                return userLogPassItem.getId();
            }
        }
        return -1L;
    }
    @GetMapping("/restPayment")
    public String payment(@RequestParam String id, @RequestParam String payment) throws ParseException, IOException {

        if(!userLogPassRepository.existsById(Long.valueOf(id))){
            return "User doesn't exist";
        }
        String message = paymentPayoffService(id
                , payment
                , TransactionTypes.PAYMENT
                , OperationType.ADD);

        if(!message.equals("Success!")){
            return message;
        }
        String now = getPreetyTimestamp();

        generateFileForPaymentOrPayoff(String.format("Wpłacono %s: %s", payment, now) , id);
        return message;

    }
    @GetMapping("/restPayoff")
    public String payoff(@RequestParam String id, @RequestParam String payoff) throws ParseException, IOException {

        if(!userLogPassRepository.existsById(Long.valueOf(id))){
            return "User doesn't exist";
        }
         String message = paymentPayoffService(id
                , payoff
                , TransactionTypes.PAYOFF
                , OperationType.SUBTRACT);

        if(!message.equals("Success!")){
            return message;
        }
        String now = getPreetyTimestamp();

        generateFileForPaymentOrPayoff(String.format("Wypłacono %s: %s", payoff, now), id);
        return message;

    }


    @GetMapping("/restTransfer")
    public String transfer(@RequestParam String idFrom
            ,@RequestParam String idTo,  @RequestParam String value) throws ParseException, IOException {

        if(!userLogPassRepository.existsById(Long.valueOf(idFrom))){
            return "User that makes a transaction doesn't exist";
        }

        if(!userLogPassRepository.existsById(Long.valueOf(idTo))){
            return "User that receives transaction doesn't exist";
        }

        String message = paymentPayoffService(idFrom
                , value
                , TransactionTypes.TRANSFER
                , OperationType.SUBTRACT);

        if(!message.equals("Success!")){
            return message;
        }


        message = paymentPayoffService(idTo
                , value
                , TransactionTypes.TRANSFER
                , OperationType.ADD);

        if(!message.equals("Success!")){
            return message;
        }

        generateFilefForTransfer(idFrom, idTo, value);
        return message;

    }


    @GetMapping("/restAllHistory")
    public List<UserHistory> allHistory(){
        return getAllUserHistories();
    }

    @GetMapping("/restOneHistory")
    public List<UserHistory> oneUserHistory(@RequestParam String id){
        return getOneUserHistory(Long.valueOf(id));
    }

    @GetMapping("/restActualBalance")
    public double getActualBalance(@RequestParam String id){
        if(!userLogPassRepository.existsById(Long.valueOf(id))){
            return -1;
        }
        return getActualBalanceByIdOfUser(Long.valueOf(id));
    }

    private String paymentPayoffService(@RequestParam String id
            , @RequestParam String paymentOrPayOff
            , TransactionTypes transactionType
            , OperationType operationType) throws ParseException {

        long userId  = Long.decode(id);
        double userPaymentOrPayOff = Double.valueOf(paymentOrPayOff);

        double balanceOfUser = getActualBalanceByIdOfUser(userId);
        double balanceAfterTransaction;

        if(operationType == OperationType.SUBTRACT){
            balanceAfterTransaction = balanceOfUser - userPaymentOrPayOff;
        }else {
            balanceAfterTransaction = balanceOfUser + userPaymentOrPayOff;
        }

        if(balanceAfterTransaction < 0){
            return String.format("Not enough money!(User #%s)", id);
        }
        UserHistory tempUserHistory = new UserHistory(
                transactionType.toString()
                , balanceAfterTransaction
                , new Date()
                , userPaymentOrPayOff
                , userId
        );

        userHistoryRepository.save(tempUserHistory);
        return "Success!";
    }

    private double getActualBalanceByIdOfUser(long id) {
        List<UserHistory> filteredOneUserHistories;

        filteredOneUserHistories = getOneUserHistory(id);
        return filteredOneUserHistories
                        .get(filteredOneUserHistories.size() - 1)
                        .getBalanceAfterTransaction();

    }

    private List<UserHistory> getAllUserHistories(){
       return userHistoryRepository.findAll();
    }

    private List<UserHistory> getOneUserHistory(Long id) {
        List<UserHistory> userHistories;
        List<UserHistory> filteredOneUserHistorie = new ArrayList<>();

        userHistories = getAllUserHistories();
         userHistories
                .stream()
                .filter(userHistory -> userHistory.getUserId() == id)
                .forEach(userHistory -> filteredOneUserHistorie.add(userHistory));
         return filteredOneUserHistorie;
    }

    private void generateFilefForTransfer(String idFrom, String idTo, String value) throws IOException {
        String now = getPreetyTimestamp();
        String message = String.format(
                "Przelew o wartosci %s z konta o id %s do konta o id %s: %s"
                , value, idFrom, idTo, now);

        File file = new File(idFrom + "_" + now + ".txt");
        fileWriter(message, file);
        file = new File(idTo + "_" + now + ".txt");
        fileWriter(message, file);
    }
    private void fileWriter(String message, File file) throws IOException {
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        writer.write(message);
        writer.flush();
        writer.close();
    }

    private void generateFileForPaymentOrPayoff(String message, String id) throws IOException {
        SimpleDateFormat parser = new SimpleDateFormat("yyyyMMdd_HHmmss");
        File file = new File(id + "_" + parser.format(new Date()) + ".txt");
        fileWriter(message, file);
    }

    private String getPreetyTimestamp() {
        SimpleDateFormat parser = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return parser.format(new Date());
    }
}
