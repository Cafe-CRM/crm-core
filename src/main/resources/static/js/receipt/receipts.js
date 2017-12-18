$(document).ready(function () {
    $('#sortedTable').dataTable({
        "scrollCollapse": false,
        "autoWidth": true,
        "lengthChange": false,
        "paging": false,
        "info": false,
        "searching": false,
        "order": [[0, "asc"]],
        "columnDefs": [{"targets": [], "visible": false},
            {"targets": [4,5], "orderable": false}],
        "oLanguage": {
            "sEmptyTable": "Нет доступных данных для таблицы!"
        },
    });
});

$(document).ready(function () {
    $('#datePickerFrom').datepicker({
        format: 'dd-mm-yyyy',
        language: 'ru'
    });
    $('#datePickerTo').datepicker({
        format: 'dd-mm-yyyy',
        language: 'ru'
    });
});

$(document).ready(function () {
    $('#formAddReceipts').submit(function (e) {
        e.preventDefault();
        var url = '/manager/tableReceipt/addReceipt';
        var formData = $('#formAddReceipts').serialize();
        if(!isBlank($('#addReceipt').val())) {
            $.ajax({
                type: 'POST',
                url: url,
                data: formData,
                success: function (data) {
                    var successMessage = '<h4 style="color:green;" align="center">' + data + '</h4>';
                    $('.messageAd').html(successMessage).show();
                        window.setTimeout(function () {
                        location.reload()
                    }, 1000);
                },
                error: function (error) {
                    var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
                    $('.messageAd').html(errorMessage).show();
                }

            });
        } else {
            var errorMessage = '<h4 style="color:red;" align="center">Не заполнены комментарии</h4>';
            $('.messageAd').html(errorMessage).show();
        }
    });
});


function isBlank(str) {
    return str.length === 0 || str.trim() === ""
}


function removeReceiptBoss(id) {
    var url = '/manager/tableReceipt/deleteReceiptBoss';

    var request = $.post(url, {receiptId: id}, function () {
        location.reload();
    });
}

function removeReceiptManager(id) {

    var formData = {
        masterKey: $('#masterKey').val(),
        receiptId: id
    }

    $.ajax({
        type: "POST",
        url: "/manager/tableReceipt/deleteReceiptManager",
        data: formData,
        success: function (result) {
            var successMessage = '<h4 style="color:green;" align="center">' + result + '</h4>';
            $('.deleteManagerReceipt').html(successMessage).show();
            window.setTimeout(function () {
                $('.deleteManagerReceipt').html(successMessage).hide();
                location.reload();
            }, 1000);
        },
        error: function (error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('.deleteManagerReceipt').html(errorMessage).show();
            window.setTimeout(function () {
                $('.deleteManagerReceipt').html(errorMessage).hide();
            }, 3000);
        }
    });
}

