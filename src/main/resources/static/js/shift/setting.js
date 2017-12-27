
jQuery(document).ready( function() {
    var elem = $("div#1");
    var tableWidth = elem.width();
    if(tableWidth < 1000) {
        return;
    }
    var lMargin = (tableWidth / 11) / 2;
    elem.css('margin-left', '+=' + lMargin);
});

function getOtherProductsAndDisplay(iter) {
    var jBlock = jQuery('#otherBlock' + iter);
    var jButton = jQuery('#otherButton' + iter);
    var button = document.getElementById('otherButton' + iter);

    showClose(jBlock, jButton, button);
}

function getDirtyProductsAndDisplay(iter) {
    var jBlock = jQuery('#dirtyBlock' + iter);
    var jButton = jQuery('#dirtyButton' + iter);
    var button = document.getElementById('dirtyButton' + iter);

    showClose(jBlock, jButton, button);
}

function showClose(jBlock, jButton, button) {
    if (jBlock.hasClass('hidden')) {
        jBlock.removeClass('hidden');
        jButton.removeClass('btn-primary').addClass('btn-success');
        button.innerHTML = 'Закрыть';
    } else {
        jBlock.addClass('hidden');
        jButton.removeClass('btn-success').addClass('btn-primary');
        button.innerHTML = 'Посмотреть заказ';
    }
}
$(".deleteButton").click(function () {
    sendDeleteDebtToken();
});

$(".closeAndRecalculate").click(function () {
    sendRecalculateToken();
});

function sendDeleteDebtToken() {
    $.ajax({
        type: "POST",
        url: "/manager/send-calculate-delete-pass",

        error: function (error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('.newAmountError').html(errorMessage).show();
        }
    });
}

function sendRecalculateToken() {
    $.ajax({
        type: "POST",
        url: "/manager/send-modify-amount-pass",

        error: function (error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('.newAmountError').html(errorMessage).show();
        }
    });
}

function deleteCalculate(calculateId) {
    var  formData = {
        password : $('#password' + calculateId).val(),
        calculateId : calculateId
    };

    $.ajax({
        type: "POST",
        url: "/manager/delete-calculate",
        data: formData,

        success: function (data) {
            location.reload();
        },
        error: function (error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('.newAmountError').html(errorMessage).show();
        }
    });
}

function closeClientWithNewAmount(calculateId) {
    var  formData = {
        newAmount : $('#newAmount' + calculateId).val(),
        password : $('#pass' + calculateId).val(),
        calculateId : calculateId
    };

    $.ajax({
        type: "POST",
        url: "/manager/close-and-recalculate",
        data: formData,

        success: function (data) {
            location.reload();
        },
        error: function (error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('.newAmountError').html(errorMessage).show();
        }
    });
}

function recalculate(calculateId) {
    var  formData = {
        newAmount : $('#newAmount' + calculateId).val(),
        password : $('#pass' + calculateId).val(),
        calculateId : calculateId
    };

    $.ajax({
        type: "POST",
        url: "/manager/recalculate",
        data: formData,

        success: function (data) {
            location.reload();
        },
        error: function (error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('.newAmountError').html(errorMessage).show();
        }
    });
}