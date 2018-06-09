
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
                alert(error.responseText);
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
                let container = "otherDebtContainer";
                let url = "/boss/add-missing-shift/get-all-other-debt";
                let delFunc = "deleteOtherDebt";
                refreshDebtContainer(container, url, delFunc);
            },
            error: function (error) {
                alert(error.responseText);
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
                let container = "cashBoxDebtContainer";
                let url = "/boss/add-missing-shift/get-all-cash-box-debt";
                let delFunc = "deleteCashBoxDebt";
                refreshDebtContainer(container, url, delFunc);
            },
            error: function (error) {
                alert(error.responseText);
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
                alert(error.responseText);
            }
        });
    });
});

function deleteProd(prodId) {
    $.ajax({
        type: "POST",
        url: "/boss/add-missing-shift/delete-missing-product",
        data: {prodId: prodId},

        success: function (data) {
            refreshProdContainer()
        },
        error: function (error) {
            alert(error.responseText);
        }
    });
}

function refreshProdContainer() {
    $.ajax({
        type: "POST",
        url: "/boss/add-missing-shift/get-all-product",

        success: function (data) {
            $('#productContainer').html('');

            for (let i = 0; i < data.length; i++) {
                let product = data[i];
                let id = product.id;
                let categoryId = product.category.id;
                let text = product.category.name;

                $('#productContainer').append(
                    '<div class="missingProd">' +
                    '<div class="col-md-4">' +
                    '<select class="form-control">' +
                    '<option value="' + categoryId + '" selected="selected">' + text + '</option>' +
                    '</select>' +
                    '</div>' +
                    '<div class="col-md-2">' +
                    '<input type="number" class="form-control" value="' + product.amount + '"/>' +
                    '</div>' +
                    '<div class="col-md-2">' +
                    '<input type="number" class="form-control" value="' + product.price + '"/>' +
                    '</div>' +
                    '<div class="col-md-1">' +
                    '<button class="btn btn-danger" onclick="deleteProd(' + id + ');">Удалить</button>' +
                    '</div>' +
                    '</div>'
                );
            }
            populateSelectors();
        },
        error: function (error) {
            alert(error.responseText);
        }
    });
}

function populateSelectors() {
    $.ajax({
        type: "POST",
        url: "/boss/add-missing-shift/get-all-category",
        success: function (data) {
            $('#productContainer > div > div > select').each(function(){
                let selectedCatId = parseInt($(this).find('option').val().toString());

                for (let i = 0; i < data.length; i++) {
                    let category = data[i];
                    let catId = parseInt(category.id.toString());

                    if (catId !== selectedCatId) {
                        $(this).append(
                            '<option value="' + category.id + '">' + category.name + '</option>'
                        );
                    }
                }
            });
        },
        error: function (error) {
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
               // alert(debtor + " " + amount);

                $('#' + container).append(
                    '<div class="missingOtherDebt">' +
                    '<div class="col-md-2">' + (i + 1)  + '</div>' +
                    '<div class="col-md-4">' +
                    '<input type="text" class="form-control" value="' + debtor + '"/>' +
                    '</div>' +
                    '<div class="col-md-4">' +
                    '<input type="number" class="form-control" value="' + amount + '"/>\n' +
                    '</div>' +
                    '<div class="col-md-2">' +
                    '<button class="btn btn-danger" onclick="' + delFunc + '(' + id + ');">Удалить</button>' +
                    '</div>' +
                    '</div>'
                );
            }
        },
        error: function (error) {
            alert(error.responseText);
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
            populateSelectors();
        },
        error: function (error) {
            alert(error.responseText);
        }
    });
}

function deleteOtherDebt(debtId) {
    $.ajax({
        type: "POST",
        url: "/boss/add-missing-shift/delete-debt",
        data: {debtId: debtId},

        success: function (data) {
            let container = "otherDebtContainer";
            let url = "/boss/add-missing-shift/get-all-other-debt";
            let delFunc = "deleteOtherDebt";
            refreshDebtContainer(container, url, delFunc);
        },
        error: function (error) {
            alert(error.responseText);
        }
    });
}

function deleteCashBoxDebt(debtId) {
    $.ajax({
        type: "POST",
        url: "/boss/add-missing-shift/delete-debt",
        data: {debtId: debtId},

        success: function (data) {
            let container = "cashBoxDebtContainer";
            let url = "/boss/add-missing-shift/get-all-cash-box-debt";
            let delFunc = "deleteCashBoxDebt";
            refreshDebtContainer(container, url, delFunc);
        },
        error: function (error) {
            alert(error.responseText);
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
            alert(error.responseText);
        }
    });
}
