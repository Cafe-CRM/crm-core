
jQuery(document).ready( function() {

    $('#addProdButton').click(function () {
        let catId = $('#addMissingCat option:selected').val();
        let amount = parseInt($('#addMissingAmount').val().toString());
        let price = parseFloat($('#addMissingPrice').val().toString());

        if (isNaN(amount)) amount = 0;
        if (isNaN(price)) price = 0;

        $.ajax({
            type: "POST",
            url: "/boss/add-missing-shift/add-missing-product",
            data: {catId: catId, amount: amount, price: price},

            success: function (data) {
                refreshProdContainer();
            },
            error: function (error) {
                console.log(error.responseText);
            }
        });
    });

    $('#addOtherDebtButton').click(function () {
        let debtor = $('#addOtherDebtorName').val();
        let amount = parseInt($('#addOtherDebtAmount').val().toString());
        if (isNaN(amount)) amount = 0;

        $.ajax({
            type: "POST",
            url: "/boss/add-missing-shift/add-missing-debt",
            data: {debtor: debtor, amount: amount, isCashBox: false},

            success: function (data) {
                let container = "otherDebtTable";
                let url = "/boss/add-missing-shift/get-all-other-debt";
                let delFunc = "deleteOtherDebt";
                refreshDebtContainer(container, url, delFunc);
            },
            error: function (error) {
                console.log(error.responseText);
            }
        });
    });

    $('#addCashBoxDebtButton').click(function () {
        let debtor = $('#addCashBoxDebtorName').val();
        let amount = parseInt($('#addCashBoxDebtAmount').val().toString());
        if (isNaN(amount)) amount = 0;

        $.ajax({
            type: "POST",
            url: "/boss/add-missing-shift/add-missing-debt",
            data: {debtor: debtor, amount: amount, isCashBox: true},

            success: function (data) {
                let container = "cashBoxDebtTable";
                let url = "/boss/add-missing-shift/get-all-cash-box-debt";
                let delFunc = "deleteCashBoxDebt";
                refreshDebtContainer(container, url, delFunc);
            },
            error: function (error) {
                console.log(error.responseText);
            }
        });
    });

    $('#addCostButton').click(function () {
        let costName = $('#costName').val();
        let costCat = $('#costCat').val();
        let costPrice = parseInt($('#costPrice').val().toString());
        let costAmount = parseInt($('#costAmount').val().toString());

        if (isNaN(costPrice)) costPrice = 0;
        if (isNaN(costAmount)) costAmount = 0;

        $.ajax({
            type: "POST",
            url: "/boss/add-missing-shift/add-missing-cost",
            data: {costName: costName, costCat: costCat, costPrice: costPrice, costAmount: costAmount},

            success: function (data) {
                refreshCostContainer();
            },
            error: function (error) {
                console.log(error.responseText);
            }
        });
    });

    $('#addAdminButton').click(function () {
        let userId = $('#selectAdmin').val();

        $.ajax({
            type: "POST",
            url: "/boss/add-missing-shift/add-user",
            data: {userId: userId},

            success: function (data) {
                let container = "adminTable";
                let url = "/boss/add-missing-shift/get-all-admins-on-shift";
                let delFunc = "deleteAdmin";
                let editFunk = "changeBonusAdmin";
                refreshUserContainer(container, url, delFunc, editFunk);
            },
            error: function (error) {
                console.log(error.responseText);
            }
        });
    });

    $('#addWorkerButton').click(function () {
        let userId = $('#selectWorker').val();

        $.ajax({
            type: "POST",
            url: "/boss/add-missing-shift/add-user",
            data: {userId: userId},

            success: function (data) {
                let container = "workersTable";
                let url = "/boss/add-missing-shift/get-all-workers-on-shift";
                let delFunc = "deleteWorker";
                let editFunk = "changeBonusWorker";
                refreshUserContainer(container, url, delFunc, editFunk);
            },
            error: function (error) {
                console.log(error.responseText);
            }
        });
    });

    $('#closeMissingShiftButton').click(function () {
        let profit = parseFloat($('#dirtyPriceInput').val().toString());
        let cashBox = parseFloat($('#cashBoxInput').val().toString());
        let bankCashBox = parseFloat($('#bankCashBoxInput').val().toString());

        if (isNaN(profit)) profit = 0;
        if (isNaN(cashBox)) cashBox = 0;
        if (isNaN(bankCashBox)) bankCashBox = 0;

        $.ajax({
            type: "POST",
            url: "/boss/add-missing-shift/send-cash-box-data",
            data: {profit: profit, cashBox: cashBox, bankCashBox: bankCashBox},

            success: function (data) {
                console.log(data);
                let content = '<pre>' + data + '</pre>';
                $('#closeShiftModal').modal('show');
                $('#closeShiftDataContent').html(content);

                //$('#salaryConfirmModal').modal('show');
            },
            error: function (error) {
                console.log(error.responseText);
            }
        });
    });

    $('#closeShift').click(function () {
        $.ajax({
            type: "POST",
            url: "/boss/add-missing-shift/close-missing-shift",

            success: function (data) {
                let url = data.split(":");
                window.location.href = url[1];
            },
            error: function (error) {
                console.log(error.responseText);
            }
        });
    });

});

function deleteShift(shiftId) {
    $.ajax({
        type: "POST",
        url: "/boss/add-missing-shift/delete-missing-shifts",
        data: {shiftId: shiftId},

        success: function (data) {
            let url = data.split(":");
            window.location.href = url[1];
        },
        error: function (error) {
            console.log(error.responseText);
        }
    });
}

function deleteProd(prodId) {
    $.ajax({
        type: "POST",
        url: "/boss/add-missing-shift/delete-missing-product",
        data: {prodId: prodId},

        success: function (data) {
            refreshProdContainer()
        },
        error: function (error) {
            console.log(error.responseText);
        }
    });
}

function refreshProdContainer() {
    $.ajax({
        type: "POST",
        url: "/boss/add-missing-shift/get-all-product",

        success: function (data) {
            $('#missingProductTable').html('');

            for (let i = 0; i < data.length; i++) {
                let product = data[i];
                let id = product.id;
                let categoryId = product.category.id;
                let name = product.category.name;

                $('#missingProductTable').append(
                    '<tr>\n' +
                    '<td>\n' +
                    '<p align="center">' + (i + 1) + '</p>\n' +
                    '</td>\n' +
                    '<td>\n' +
                    '<p align="center">' + name + '</p>\n' +
                    '</td>\n' +
                    '<td>\n' +
                    '<p align="center">' + product.amount + '</p>\n' +
                    '</td>\n' +
                    '<td>\n' +
                    '<p align="center">' + product.price + '</p>\n' +
                    '</td>\n' +
                    '<td>\n' +
                    '<button class="btn btn-danger" ' +
                    'onclick="deleteProd(' + id + ');">Удалить</button>\n' +
                    '</td>\n' +
                    '</tr>'
                );
            }
        },
        error: function (error) {
            console.log(error.responseText);
        }
    });
}

function refreshDebtContainer(container, url, delFunc) {
    $.ajax({
        type: "POST",
        url: url,

        success: function (data) {
            $('#' + container).html('');

            for (let i = 0; i < data.length; i++) {
                let debt = data[i];
                let id = debt.id;
                let debtor = debt.debtor;
                let amount = debt.debtAmount;

                $('#' + container).append(
                    '<tr>\n' +
                    '<td>\n' +
                    '<p align="center">' + (i + 1)  + '</p>\n' +
                    '</td>\n' +
                    '<td>\n' +
                    '<p align="center">' + debtor + '</p>\n' +
                    '</td>\n' +
                    '<td>\n' +
                    '<p align="center">' + amount + '</p>\n' +
                    '</td>\n' +
                    '<td>\n' +
                    '<button class="btn btn-danger" onclick="' + delFunc + '(' + id + ');">Удалить</button>' +
                    '</td>\n' +
                    '</tr>'
                );
            }
        },
        error: function (error) {
            console.log(error.responseText);
        }
    });
}

function refreshCostContainer() {
    $.ajax({
        type: "POST",
        url: "/boss/add-missing-shift/get-all-missing-cost",

        success: function (data) {
            $('#missingCostTable').html('');

            for (let i = 0; i < data.length; i++) {
                let cost = data[i];
                let id = cost.id;
                let name = cost.name;
                let price = cost.price;
                let amount = cost.quantity;

                $('#missingCostTable').append(
                    '<tr>' +
                    '<td>' +
                    '<p align="center">' + (i + 1) + '</p>\n' +
                    '</td>' +
                    '<td>' +
                    '<p align="center">' + name + '</p>' +
                    '</td>' +
                    '<td>' +
                    '<p align="center">' + price + '</p>' +
                    '</td>' +
                    '<td>' +
                    '<p align="center">' + amount + '</p>' +
                    '</td>' +
                    '<td>' +
                    '<button class="btn btn-danger" onclick="deleteCost(' + id + ');">Удалить</button>' +
                    '</td>' +
                    '</tr>'
                );
            }
        },
        error: function (error) {
            console.log(error.responseText);
        }
    });
}

function refreshUserContainer(container, url, delFunc, editFunk) {
    $.ajax({
        type: "POST",
        url: url,

        success: function (data) {
            $('#' + container).html('');

            for (let i = 0; i < data.length; i++) {
                //console.log("1");
                let user = data[i];
                let id = user.id;
                let name = user.firstName + " " + user.lastName;
                let shiftSalary = user.shiftSalary;
                let bonus = user.paidBonus;
                //alert("bonus: " + bonus + ", shiftSalary: " + shiftSalary);

                $('#' + container).append(
                    '<tr>' +
                    '<td>' +
                    '<p align="center">' + (i + 1) + '</p>' +
                    '</td>' +
                    '<td>' +
                    '<p align="center">' + name + '</p>' +
                    '</td>' +
                    '<td>' +
                    '<p align="center">' + shiftSalary + '</p>' +
                    '</td>' +
                    '<td>' +
                    '<input id="bonusInput' + id + '" ' +
                    'style="width: 100px" ' +
                    'class="form-control" ' +
                    'type="number" ' +
                    'oninput="this.value = Math.abs(this.value)" min="0" step="1" ' +
                    'onblur="' + editFunk + '(' + id + ');" ' +
                    'placeholder="Бонус"/ ' +
                    'value="' + bonus + '"' +
                    '</td>' +
                    '<td>' +
                    '<button class="btn btn-danger" ' +
                    'onclick="' + delFunc + '(' + id + ');">Удалить</button>' +
                    '</td>' +
                    '</tr>'
                );
            }
            refreshSalaryContainer();
        },
        error: function (error) {
            console.log(error.responseText);
        }
    });
}

function refreshSalaryContainer() {
    $.ajax({
        type: "POST",
        url: "/boss/add-missing-shift/get-all-users-on-shift",
        success: function (data) {
            $('#salaryTable').html('');

            for (let i = 0; i < data.length; i++) {
                let user = data[i];
                let id = user.id;
                let name = user.firstName + " " + user.lastName;
                let salary = user.shiftSalary + user.paidBonus;
                let isSalary = user.salary;

                let html =
                    '<tr>' +
                    '<td>' +
                    '<p align="center">' + (i + 1) + '</p>' +
                    '</td>' +
                    '<td>' +
                    '<p align="center">' + name + '</p>' +
                    '</td>' +
                    '<td>' +
                    '<p align="center">' + salary + '</p>' +
                    '</td>' +
                    '<td>' +
                    '<input id="salaryBox' + id + '" ' +
                    'style="width: 20px;height: 20px;margin-top: 5px" ' +
                    'onchange="changeSalaryState(' + id + ');"'

                if (isSalary) {
                    html += 'checked '
                }

               html +=
                    ' type="checkbox"/>' +
                    '</td>';

                $('#salaryTable').append(
                    html
                );
            }
        },
        error: function (error) {
            console.log(error.responseText);
        }
    });
}

function deleteOtherDebt(debtId) {
    $.ajax({
        type: "POST",
        url: "/boss/add-missing-shift/delete-debt",
        data: {debtId: debtId},

        success: function (data) {
            let container = "otherDebtTable";
            let url = "/boss/add-missing-shift/get-all-other-debt";
            let delFunc = "deleteOtherDebt";
            refreshDebtContainer(container, url, delFunc);
        },
        error: function (error) {
            console.log(error.responseText);
        }
    });
}

function deleteCashBoxDebt(debtId) {
    $.ajax({
        type: "POST",
        url: "/boss/add-missing-shift/delete-debt",
        data: {debtId: debtId},

        success: function (data) {
            let container = "cashBoxDebtTable";
            let url = "/boss/add-missing-shift/get-all-cash-box-debt";
            let delFunc = "deleteCashBoxDebt";
            refreshDebtContainer(container, url, delFunc);
        },
        error: function (error) {
            console.log(error.responseText);
        }
    });
}

function deleteCost(costId) {
    $.ajax({
        type: "POST",
        url: "/boss/add-missing-shift/delete-cost",
        data: {costId: costId},

        success: function (data) {
            refreshCostContainer();
        },
        error: function (error) {
            console.log(error.responseText);
        }
    });
}

function deleteAdmin(userId) {
    $.ajax({
        type: "POST",
        url: "/boss/add-missing-shift/delete-user",
        data: {userId: userId},

        success: function (data) {
            let container = "adminTable";
            let url = "/boss/add-missing-shift/get-all-admins-on-shift";
            let delFunc = "deleteAdmin";
            let editFunk = "changeBonusAdmin";
            refreshUserContainer(container, url, delFunc, editFunk);
        },
        error: function (error) {
            console.log(error.responseText);
        }
    });
}

function deleteWorker(userId) {
    $.ajax({
        type: "POST",
        url: "/boss/add-missing-shift/delete-user",
        data: {userId: userId},

        success: function (data) {
            let container = "workersTable";
            let url = "/boss/add-missing-shift/get-all-workers-on-shift";
            let delFunc = "deleteWorker";
            let editFunk = "changeBonusWorker";
            refreshUserContainer(container, url, delFunc, editFunk);
        },
        error: function (error) {
            console.log(error.responseText);
        }
    });
}

function changeBonusAdmin(userId) {
    let bonus = parseInt($('#bonusInput' + userId).val().toString());
    if (isNaN(bonus)) bonus = 0;

    $.ajax({
        type: "POST",
        url: "/boss/add-missing-shift/edit-bonus",
        data: {userId: userId, bonus: bonus},

        success: function (data) {
            let container = "adminTable";
            let url = "/boss/add-missing-shift/get-all-admins-on-shift";
            let delFunc = "deleteAdmin";
            let editFunk = "changeBonusAdmin";
            refreshUserContainer(container, url, delFunc, editFunk);
        },
        error: function (error) {
            console.log(error.responseText);
        }
    });
}

function changeBonusWorker(userId) {
    let bonus = parseInt($('#bonusInput' + userId).val().toString());
    if (isNaN(bonus)) bonus = 0;

    $.ajax({
        type: "POST",
        url: "/boss/add-missing-shift/edit-bonus",
        data: {userId: userId, bonus: bonus},

        success: function (data) {
            let container = "workersTable";
            let url = "/boss/add-missing-shift/get-all-workers-on-shift";
            let delFunc = "deleteWorker";
            let editFunk = "changeBonusWorker";
            refreshUserContainer(container, url, delFunc, editFunk);
        },
        error: function (error) {
            console.log(error.responseText);
        }
    });
}

function changeSalaryState(userId) {
    let state = false;
    if ($('#salaryBox' + userId).is(':checked')){
        state = true
    }

    $.ajax({
        type: "POST",
        url: "/boss/add-missing-shift/change-salary-state",
        data: {userId: userId, state: state},

        success: function (data) {
            console.log(data.responseText);
        },
        error: function (error) {
            console.log(error.responseText);
        }
    });
}

function sendDeletedShiftToken(date) {
    $.ajax({
        type: "POST",
        url: "/boss/add-missing-shift/send-delete-shifts-token",
        data: {date : date},

        error: function (error) {
            let errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('.newAmountError').html(errorMessage).show();
        }
    });
}

function deleteShifts(date) {
    var  formData = {
        password : $('#password').val(),
        date : date
    };

    $.ajax({
        type: "POST",
        url: "/boss/add-missing-shift/delete-shifts",
        data: formData,

        success: function (data) {
            let url = data.split(":");
            window.location.href = url[1];
        },
        error: function (error) {
            let errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('.newAmountError').html(errorMessage).show();
        }
    });
}