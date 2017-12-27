$(document).ready(function () {
    (function ($) {

        $('#filter').keyup(function () {

            var rex = new RegExp($(this).val(), 'i');
            $('.searchable tr').hide();
            $('.searchable tr').filter(function () {
                return rex.test($(this).text());
            }).show();

        })

    }(jQuery));
});

var usersId = [];

$("#editButton").click(function () {
    sendToken();
    $('#add').modal('hide');
    usersId = [];
    Array.from(document.getElementById("checkedUsers").rows).forEach(function(item) {
        var column = item.querySelector('.check');
        var checkBox = column.getElementsByTagName('input')[0];
        if($(checkBox).prop('checked')) {
            usersId.push(checkBox.value);
        }
    });
});

function sendToken() {
    $.ajax({
        type: "POST",
        url: "/manager/shift/send-edit-cash-box-pass",

        error: function (error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('.newAmountError').html(errorMessage).show();
        }
    });
}

function editCashBoxAndBegin() {
    var  formData = {
        usersId: usersId,
        cashBox: $('#cashAmount').val(),
        bankCashBox: $('#cardAmount').val(),
        password : $('#password').val()
    };

    $.ajax({
        type: "POST",
        url: "/manager/shift/editCashBoxAndBegin",
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
