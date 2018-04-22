
jQuery(document).ready( function() {
    var elem = $("div#1");
    var tableWidth = elem.width();
    if(tableWidth < 1000) {
        return;
    }
    var lMargin = (tableWidth / 11) / 2;
    elem.css('margin-left', '+=' + lMargin);


    $('#paySalariesTab').click(function () {
        $.ajax({
            type: "POST",
            url: "/boss/user/get-all",

            success: function (data) {
                $('#salaryUsersTable').html('');

                for (var i = 0; i < data.length; i++) {
                    var count = i + 1;
                    var name = data[i].firstName + " " + data[i].lastName;
                    var salaryBalance = data[i].salaryBalance;
                    var bonusBalance = data[i].bonusBalance;

                    $('#salaryUsersTable').append(
                        '<tr>' +
                        '<td><p align="center">' + count + '</p></td>' +
                        '<td><p align="center">' + name + '</p></td>' +
                        '<td><p align="center">' + salaryBalance + '</p></td>' +
                        '<td><p align="center">' + bonusBalance + '</p></td>' +
                        '<td style="text-align: center;">' +
                        '<input name="clientsId" form="formCheckedWorkers" class="checkWorkerSalary" style="width: 20px;height: 20px;margin-top: 5px" id="' + data[i].id + '" value="' +  data[i].id + '" type="checkbox"/>' +
                        '</td>' +
                        '</tr>'



                    );
                }
            },
            error: function (error) {
                var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
                $('#salaryErrorMessage').html(errorMessage).show();
            }
        });
    });


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

function checkWorkers() {
    if ($('#checkAllWorker').is(':checked')) {
        $(".checkWorkerSalary").prop("checked", true);
    } else {
        $(".checkWorkerSalary").prop("checked", false);
    }
}

function ajaxModal() {
    $.ajax({
        type: "POST",
        url: "/boss/user/get-salary-clients",
        data: $('#formCheckedWorkers').serialize(),

        success: function (data) {
            if (data == "") {

            } else {
                $('#salaryConfirmModal').modal('show');

                var totalSalary = 0;
                for (var i = 0; i < data.length; i++) {
                    var id = data[i].id;
                    var name = data[i].firstName + " " + data[i].lastName;
                    var salaryBalance = data[i].salaryBalance;
                    var bonusBalance = data[i].bonusBalance;
                    var salary = salaryBalance + bonusBalance;
                    totalSalary += salary;

                    $('#salaryWorkerTable').find('> tbody').append(
                        '<tr id=' + id + '>' +
                        '<td class="workerName" align="center">' + name + '</td>' +
                        '<td class="workerSalary" align="center">' + salary + '</td>' +
                        '</tr>'
                    );

                    $('#totalSalaryCost').html(totalSalary);

                }
            }
        },
        error: function (error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('#salaryWorkerError').html(errorMessage).show();
        }
    });
}

$('#salaryConfirmModal').on('hidden.bs.modal', function (event) {
    $('#salaryWorkerTable').find('> tbody').html('');
});

function giveSalary() {
    $.ajax({
        type: "POST",
        url: "/boss/user/pay-salaries",
        data: $('#formCheckedWorkers').serialize(),

        success: function (data) {
            var successMessage = '<h4 style="color:green;" align="center"> Зарплаты начисленны! </h4>';
            $('#salaryErrorMessage').html(successMessage).show();

            window.setTimeout(function () {
                $('#salaryUsersTable').html('');

                $('#checkAllWorker').prop("checked", false);

                for (var i = 0; i < data.length; i++) {
                    var count = i + 1;
                    var name = data[i].firstName + " " + data[i].lastName;
                    var salaryBalance = data[i].salaryBalance;
                    var bonusBalance = data[i].bonusBalance;

                    $('#salaryUsersTable').append(
                        '<tr>' +
                        '<td><p align="center">' + count + '</p></td>' +
                        '<td><p align="center">' + name + '</p></td>' +
                        '<td><p align="center">' + salaryBalance + '</p></td>' +
                        '<td><p align="center">' + bonusBalance + '</p></td>' +
                        '<td style="text-align: center;">' +
                        '<input name="clientsId" form="formCheckedWorkers" class="checkWorkerSalary" style="width: 20px;height: 20px;margin-top: 5px" id="' + data[i].id + '" value="' +  data[i].id + '" type="checkbox"/>' +
                        '</td>' +
                        '</tr>'



                    );

                }
                $('#salaryConfirmModal').modal('hide');

            }, 1000);

        },
        error: function (error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('#salaryErrorMessage').html(errorMessage).show();
        }
    });
}

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