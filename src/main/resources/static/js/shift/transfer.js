$(document).ready(function () {
    $('#transferToCard').submit(function (e) {
        e.preventDefault();
        var url = '/manager/shift/edit/transferCashToBankCashBox';
        var formData = {transferBankCashBox: $("#transferBankCashBox").val()};
        if ($("#transferBankCashBox").val() > 0) {
            $.ajax({
                type: 'POST',
                url: url,
                data: formData,
                success: function (data) {

                    var successMessage = '<h3 style="color:lightblue;" align="center">' + "Вы успешно перевели сумму на карту: " + $("#transferBankCashBox").val() + '</h3>';
                    $('.messageAd').html(successMessage).show();
                    $('#cashBox').text(data[0]);
                    $('#bankCashBox').text(data[1]);
                },
                error: function (error) {
                    var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
                    $('.messageAd').html(errorMessage).show();
                }

            });
        }
        else {
            var errorMessage = '<h4 style="color:red;" align="center">' + "Сумма перевода не может быть меньше нуля!" + '</h4>';
            $('.messageAd').html(errorMessage).show();
        }
    });
});

$(document).ready(function () {
    $('#transferToCasse').submit(function (e) {
        e.preventDefault();
        var url = '/manager/shift/edit/transferCashToCashBox';
        var formData = {transferCashBox: $("#transferCashBox").val()};
        if ($("#transferCashBox").val() > 0) {
            $.ajax({
                type: 'POST',
                url: url,
                data: formData,
                success: function (data) {

                    var successMessage = '<h3 style="color:lightblue;" align="center">' + "Вы успешно перевели сумму в кассу: " + $("#transferCashBox").val() + '</h3>';
                    $('.messageAd').html(successMessage).show();
                    $('#cashBox').text(data[0]);
                    $('#bankCashBox').text(data[1]);
                },
                error: function (error) {
                    var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
                    $('.messageAd').html(errorMessage).show();
                }

            });
        }
        else {
            var errorMessage = '<h4 style="color:red;" align="center">' + "Сумма перевода не может быть меньше нуля!" + '</h4>';
            $('.messageAd2').html(errorMessage).show();
        }
    });
});