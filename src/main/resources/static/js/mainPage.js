var userId;
var userBalance;
var getBalanceFromServer;

$(document).ready(function () {

    (function initialize() {
        $.ajax({
            url: "/rest",
            async: false
        }).then(function (data) {
            $("#response").text(data);
        });
    })();

    (function () {

        var login = prompt("login", "admin");
        var password = prompt("password", "admin");

        if (login == null || password == null) {
            window.reload();
        }

        $.ajax({
            method: "GET",
            url: "/restUserLogin",
            async: false,
            data: {login: login, password: password}
        }).done(function (responseText) {
            userId = JSON.stringify(responseText);
            $('#idOfUser').text('Id uzytkownika: ' + userId)
        });
        if (userId === '-1') {
            window.reload();
        }
    })();

    getBalanceFromServer();


});

function getBalanceFromServer() {

    $.ajax({
        method: "GET",
        url: "/restActualBalance",
        async: false,
        data: {id: userId}
    }).done(function (responseText) {
        userBalance = JSON.stringify(responseText);
        $('#balance').text('Saldo: ' + userBalance);
    });
}

function paymentFun() {
    $.ajax({
        method: "GET",
        url: "/restPayment",
        async: false,
        data: {id: userId, payment: $("#payment").val()}
    }).done(function (responseText) {
        $('#response').text(JSON.stringify(responseText, null, 2));
        getBalanceFromServer();
    });
}

function payoffFun() {
    $.ajax({
        method: "GET",
        url: "/restPayoff",
        async: false,
        data: {id: userId, payoff: $("#payoff").val()}
    }).done(function (responseText) {
        $('#response').text(JSON.stringify(responseText, null, 2));
        getBalanceFromServer();
    });
}

function transferFun() {
    $.ajax({
        method: "GET",
        url: "/restTransfer",
        async: false,
        data: {idFrom: userId, idTo: $("#transferId").val(), value: $("#transferCost").val()}
    }).done(function (responseText) {
        $('#response').text(JSON.stringify(responseText, null, 2));
        getBalanceFromServer();
    });
}

function oneHistoryFun() {
    $.ajax({
        method: "GET",
        url: "/restOneHistory",
        async: false,
        data: {id: $("#oneHistoryId").val()}
    }).done(function (responseText) {
        $('#response').text(JSON.stringify(responseText, null, 2));
    });
}

function allHistoryFun() {
    $.ajax({
        method: "GET",
        url: "/restAllHistory",
        async: false,
    }).done(function (responseText) {
        $('#response').text(JSON.stringify(responseText, null, 2));
    });


}