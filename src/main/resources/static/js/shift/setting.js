
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
                    var id = data[i].id;
                    var count = i + 1;
                    var name = data[i].firstName + " " + data[i].lastName;
                    var salaryBalance = data[i].salaryBalance;
                    var bonusBalance = data[i].bonusBalance;

                    $('#salaryUsersTable').append(
                        '<tr>' +
                        '<td><p align="center">' + count + '</p></td>' +
                        '<td id="fullName' + id + '"><p align="center">' + name + '</p></td>' +
                        '<td id="salaryBalance' + id + '"><p align="center">' + salaryBalance + '</p></td>' +
                        '<td id="bonusBalance' + id + '"><p align="center">' + bonusBalance + '</p></td>' +
                        '<td>' +
                        '<button onclick="payNewSalary(' + id + ');" class="btn btn-info">Выдать новую сумму</button>' +
                        '<button style="float: right" onclick="changeUserBalance(' + id + ');" class="btn btn-danger">Изменить баланс</button>' +
                        '</td>' +
                        '<td style="text-align: center;">' +
                        '<input name="clientsId" form="formCheckedWorkers" class="checkWorkerSalary" style="width: 20px;height: 20px;margin-top: 5px" id="' + data[i].id + '" value="' +  data[i].id + '" type="checkbox"/>' +
                        '</td>totalSalaryCost' +
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

function payNewSalary(userId) {
    var salary = parseFloat($('#salaryBalance' + userId).text().toString());
    var bonus = parseFloat($('#bonusBalance' + userId).text().toString());

    if (isNaN(salary)) salary = 0;
    if (isNaN(bonus)) bonus = 0;

    $('#title').text('Изменение зп при выдаче.\nВведите новые данные и отправьте код подверждения.');
    $('#workerName').text($('#fullName' + userId).text());
    $('#newSalaryBalance').val(salary);
    $('#newBonusBalance').val(bonus);
    $('#newTotalSalary').html(salary + bonus);
    $('#salaryDataButton').attr('onclick', 'sendNewSalaryDataToken(' + userId + ');' );
    $("#changeBalanceModal").modal('show');
}

function recalculateSalary() {
    var newSalary = parseFloat($('#newSalaryBalance').val().toString());
    var newBonus = parseFloat($('#newBonusBalance').val().toString());

    if (isNaN(newSalary)) newSalary = 0;
    if (isNaN(newBonus)) newBonus = 0;

    $('#newTotalSalary').html(newSalary + newBonus);
}

function sendNewSalaryDataToken(userId) {
    var newSalary = parseFloat($('#newSalaryBalance').val().toString());
    var newBonus = parseFloat($('#newBonusBalance').val().toString());

    if (isNaN(newSalary)) newSalary = 0;
    if (isNaN(newBonus)) newBonus = 0;

    $.ajax({
        type: "POST",
        url: "/boss/user/send-changed-salary-password",
        data: {userId: userId, salary: newSalary, bonus: newBonus},

        success: function (data) {
            $('#newSalaryBalance').prop('disabled',true);
            $('#newBonusBalance').prop('disabled',true);
            $('#title').html('В админ-конференцию был послан короткий код. \n Используйте его для подтверждения действия.');
            $('#salaryDataButton').addClass('hidden');
            $('#confirmCodeBlock').removeClass('hidden');
            $('#sendChangedSalaryCodeAgain').removeClass('hidden');
            $('#sendChangedSalaryCodeAgain').attr('onclick', 'sendNewSalaryDataToken(' + userId + ');' );
            $('#payChangedSalary').removeClass('hidden');
            $('#payChangedSalary').attr('onclick', 'payChangedSalary(' + userId + ');' );
        },
        error: function (error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('#changeSalaryError').html(errorMessage).show();
        }
    });
}

function payChangedSalary(userId) {
    var newSalary = parseFloat($('#newSalaryBalance').val().toString());
    var newBonus = parseFloat($('#newBonusBalance').val().toString());
    var password = $('#confirmCode').val();

    if (isNaN(newSalary)) newSalary = 0;
    if (isNaN(newBonus)) newBonus = 0;

    $.ajax({
        type: "POST",
        url: "/boss/user/pay-changed-salary",
        data: {userId: userId, salary: newSalary, bonus: newBonus, password: password},

        success: function (data) {
            location.reload();
        },
        error: function (error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('#changeSalaryError').html(errorMessage).show();
        }
    });
}

function changeUserBalance(userId) {
    var salary = parseFloat($('#salaryBalance' + userId).text().toString());
    var bonus = parseFloat($('#bonusBalance' + userId).text().toString());

    if (isNaN(salary)) salary = 0;
    if (isNaN(bonus)) bonus = 0;

    $('#title').text('Изменение баланса сотрудника.\nВведите новые данные и отправьте код подверждения.');
    $('#workerName').text($('#fullName' + userId).text());
    $('#newSalaryBalance').val(salary);
    $('#newBonusBalance').val(bonus);
    $('#salaryInfo').addClass('hidden');
    $('#salaryDataButton').attr('onclick', 'sendNewBalanceToken(' + userId + ');' );
    $("#changeBalanceModal").modal('show');

    $("#changeBalanceModal").modal('show');
}

function sendNewBalanceToken(userId) {
    var newSalary = parseFloat($('#newSalaryBalance').val().toString());
    var newBonus = parseFloat($('#newBonusBalance').val().toString());

    if (isNaN(newSalary)) newSalary = 0;
    if (isNaN(newBonus)) newBonus = 0;

    $.ajax({
        type: "POST",
        url: "/boss/user/send-change-balance-password",
        data: {userId: userId, salary: newSalary, bonus: newBonus},

        success: function (data) {
            $('#newSalaryBalance').prop('disabled',true);
            $('#newBonusBalance').prop('disabled',true);
            $('#title').html('В админ-конференцию был послан короткий код. \n Используйте его для подтверждения действия.');
            $('#salaryDataButton').addClass('hidden');
            $('#confirmCodeBlock').removeClass('hidden');
            $('#sendChangedSalaryCodeAgain').removeClass('hidden');
            $('#sendChangedSalaryCodeAgain').attr('onclick', 'sendNewBalanceToken(' + userId + ');' );
            $('#changeBalance').removeClass('hidden');
            $('#changeBalance').attr('onclick', 'changeBalance(' + userId + ');' );
        },
        error: function (error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('#changeSalaryError').html(errorMessage).show();
        }
    });
}

function changeBalance(userId) {
    var newSalary = parseFloat($('#newSalaryBalance').val().toString());
    var newBonus = parseFloat($('#newBonusBalance').val().toString());
    var password = $('#confirmCode').val();

    if (isNaN(newSalary)) newSalary = 0;
    if (isNaN(newBonus)) newBonus = 0;

    $.ajax({
        type: "POST",
        url: "/boss/user/changed-balance",
        data: {userId: userId, salary: newSalary, bonus: newBonus, password: password},

        success: function (data) {
            location.reload();
        },
        error: function (error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('#changeSalaryError').html(errorMessage).show();
        }
    });
}

function sendSalaryToken() {
    $.ajax({
        type: "POST",
        url: "/boss/user/send-salary-token",
        data: $('#formCheckedWorkers').serialize(),

        success: function (data) {
            $('#sendSalaryCode').addClass('hidden');
            $('#sendSalaryCodeAgain').removeClass('hidden');
            $('#confirmPayBlock').removeClass('hidden');
            $('#paySalaryButton').removeClass('hidden');
        },
        error: function (error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('#salaryWorkerError').html(errorMessage).show();
        }
    });
}

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
    var formInfo = $('#formCheckedWorkers').serialize();
    var arr = formInfo.split("&");
    for (var i = 0; i < arr.length; i++) {
        arr[i] = arr[i].substring(10);
    }
    $.ajax({
        type: "POST",
        url: "/boss/user/pay-salaries",

        data: {clientsId: arr.toString(), password: $('#confirmPayCode').val()},

        success: function (data) {
            var successMessage = '<h4 style="color:green;" align="center"> Зарплаты начисленны! </h4>';
            $('#salaryErrorMessage').html(successMessage).show();

            window.setTimeout(function () {
                $('#salaryUsersTable').html('');

                $('#checkAllWorker').prop("checked", false);

                for (var i = 0; i < data.length; i++) {
                    var id = data[i].id;
                    var count = i + 1;
                    var name = data[i].firstName + " " + data[i].lastName;
                    var salaryBalance = data[i].salaryBalance;
                    var bonusBalance = data[i].bonusBalance;

                    $('#salaryUsersTable').append(

                        '<tr>' +
                        '<td><p align="center">' + count + '</p></td>' +
                        '<td id="fullName' + id + '"><p align="center">' + name + '</p></td>' +
                        '<td id="salaryBalance' + id + '"><p align="center">' + salaryBalance + '</p></td>' +
                        '<td id="bonusBalance' + id + '"><p align="center">' + bonusBalance + '</p></td>' +
                        '<td>' +
                        '<button onclick="payNewSalary(' + id + ');" class="btn btn-info">Выдать новую сумму</button>' +
                        '<button style="float: right" onclick="changeClientBalance(' + id + ');" class="btn btn-danger">Изменить баланс</button>' +
                        '</td>' +
                        '<td style="text-align: center;">' +
                        '<input name="clientsId" form="formCheckedWorkers" class="checkWorkerSalary" style="width: 20px;height: 20px;margin-top: 5px" id="' + data[i].id + '" value="' +  data[i].id + '" type="checkbox"/>' +
                        '</td>totalSalaryCost' +
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

function sendDeleteDebtToken(calcId) {
    $.ajax({
        type: "POST",
        url: "/manager/send-calculate-delete-pass",
        data: {calcId : calcId},

        error: function (error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('.newAmountError').html(errorMessage).show();
        }
    });
}

function sendRecalculateToken(calcId) {
    $.ajax({
        type: "POST",
        url: "/manager/send-modify-amount-pass-from-settings",
        data: {calcId : calcId, newAmount : $('#newAmount' + calcId).val()},

        success: function (data) {
            $('#newAmount' + calcId).prop('disabled',true);
            $('#sendCode' + calcId).html('Отправить код повторно');
            $('#promptText' + calcId).html('В админ-конференцию был послан короткий код. \n Используйте его для подтверждения действия.');
            $('#confirmPassword' + calcId).removeClass('hidden');
            $('.confirmButton').removeClass('hidden');
        },
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

function repeatPrintCheck(calculateId) {

    $.ajax({
        type: "POST",
        url: "/manager/repeat-print-check",
        data: { calculateId : calculateId
        },

        success: function (data) {
            location.reload();
        },
        error: function (error) {
            alert(error.responseText);
        }
    });

}