$(document).ready(function () {
    $('input[type="text"]').get(0).focus();

    var isAnyCategoryExist = $('#categoryTable').find('> tbody > tr').length > 0;

    if (isAnyCategoryExist) {
        $('#categoryBlock').append(
            '<button onclick="addCategory()" id="salaryButton" data-toggle="dropdown" type="button" ' +
            'class="btn btn-danger buttonEditCategory dropdown-toggle">Выбрать зарплатную категорию</button>'
        );

        $($('#categoryTable').find('> tbody > tr')).each(function() {
            var categoryId = $(this).attr('id');
            var categoryName = $(this).find('> td.categoryName').html();

            $('#salaryCostList')
                .append(
                    '<li id="salaryCategoryLi' + categoryId +'">' +
                    '<a href="javascript:sendSalaryCategoryFromList(' + categoryId + ')">' + categoryName + '</a>' +
                    '</li>'
                );
        });
    }

    $('#buttonAddCategory').on("click", function (e) {
        var data = $('#inputCategoryName').serialize();
        var rowVal = $('#inputCategoryName').val();
        $('#inputCategoryName').val('');
        $('input[type="text"]').get(0).focus();
        var url = "/company/configuration/step/costCategory/add";
        $.ajax({
            type: 'POST',
            url: url,
            data: data,
            success: function (category) {
                var dataId = category.id;
                var rowId = "categoryRow" + dataId;
                var salaryRowId = "salaryRowId" + dataId;
                var isSalaryButtonExist = $('#categoryBlock').find($("#salaryButton")).length > 0;
                $('#categoryTable').find('> tbody')
                    .append('<tr id=' + rowId + '>'+
                        '<td class="categoryName" align="center">' + rowVal + '</td>' +
                        '<td align="center" >' +
                        '<button type="button" class="btn btn-danger buttonDeleteCategory" id=' + category.id + '>Удалить</button></td>' +
                        '</tr>');
                if (!isSalaryButtonExist) {
                    $('#categoryBlock').append(
                        '<button onclick="addCategory()" id="salaryButton" data-toggle="dropdown" type="button" ' +
                        'class="btn btn-danger buttonEditCategory dropdown-toggle">Выбрать зарплатную категорию</button>'
                    )
                }
                $('#salaryCategoryTable').find('> tbody')
                    .append('<tr id=' + salaryRowId + '>'+
                        '<td align="center">' + rowVal + '</td>' +
                        '<td class="checkSalary" align="center" >' +
                        '<input id=' + category.id + ' onchange="checkAsSalary(' + dataId + ')" type="checkbox" id=' + category.id + '></input></td>' +
                        '</tr>');
                $('#salaryCostList')
                    .append(
                        '<li id="salaryCategoryLi' + category.id +'">' +
                        '<a href="javascript:sendSalaryCategoryFromList(' + category.id + ')">' + category.name + '</a>' +
                        '</li>'
                    );

            },
            error: function (error) {
                e.preventDefault();
                var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
                $('.errorMessage').html(errorMessage).show();
            }

        });
    });
});

function addCategory() {
    var left = $("#salaryButton").position().left;
    var width = $("#salaryButton").width() + 24;
    var listWidth = width + "px";
    var leftOffset = left + "px";
    $("#salaryCostList").css("left", leftOffset);
    $("#salaryCostList").css("width", listWidth);
}

function checkAsSalary(categoryId) {
    var tBody = $('#salaryCategoryBody');
    var checkBox = tBody.find('tr td.checkSalary input#' + categoryId);
    var isChecked = checkBox.prop('checked');

    if (isChecked === undefined) {
        checkBox.prop('checked', false);
        var errorMessage = '<h4 style="color:red;" align="center">Выбрана не существующая категория</h4>';
        $('#salaryErrorMessage').html(errorMessage).show();
    } else {
        sendSalaryCategory(categoryId, isChecked, tBody, checkBox);
    }
}

function sendSalaryCategoryFromList(categoryId) {
    var url = "/company/configuration/step/costCategory/check-as-salary";
    $.ajax({
        type: 'POST',
        url: url,
        data: {categoryId: categoryId, isSalary: true, isChangeable: true},
        success: function (category) {

            $('#reconfiguredMessage').remove();

            var successMessage = '<h4 style="color:green;" align="center">Категория "' + category.name + '" отмечена как зарплатная.</h4>';
            $('.errorMessage').html(successMessage).show();
        },
        error: function (error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('.errorMessage').html(errorMessage).show();
        }

    });
}

function sendSalaryCategory(categoryId, isChecked, tBody, checkBox) {
    var url = "/company/configuration/step/costCategory/check-as-salary";
    $.ajax({
        type: 'POST',
        url: url,
        data: {categoryId: categoryId, isSalary: isChecked,  isChangeable: false},
        success: function (category) {
            alert("on change");

            $('#reconfiguredMessage').remove();

            tBody.find('tr td.checkSalary input').each(function() {
                if (isChecked) {
                    if ($(this).attr("data-id") != categoryId) {
                        $(this).attr("disabled", true);
                    }
                } else {
                    $(this).attr("disabled", false);
                }
            });
        },
        error: function (error) {
            checkBox.prop('checked', false);
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('#salaryErrorMessage').html(errorMessage).show();
        }

    });
}

function removeDisabled() {
    $('#salaryCategoryBody').find('tr td.checkSalary input').each(function() {

        $(this).attr("disabled", false);

    });
}

function ajaxModal(id, cardEnable) {
    $.ajax({
        type: "POST",
        url: "/manager/output-clients",
        data: $('#formTest').serialize(),

        success: function (data) {
            if (data == "") {
                $('#tb' + id).html(" ");
            } else {

            }
        },
        error: function () {
            console.log('ajaxModal сломался? ');
        }
    });
}

$(document).ready(function () {
    $('#categoryTable').on("click", '.buttonDeleteCategory', function (e) {
        var categoryId = $(this).attr('id');
        var delRow =  $(this);
        var isAnyCategoryExist = $('#categoryTable').find('> tbody > tr').length > 1;
        var url = "/company/configuration/step/costCategory/delete";
        $.ajax({
            type: 'POST',
            url: url,
            data: {categoryId: categoryId},
            success: function (delCategoryId) {
                var tBody = $('#salaryCategoryBody');
                var checkBox = tBody.find('tr td.checkSalary input#' + categoryId);
                var isChecked = checkBox.prop('checked');

                if (isChecked) {
                    removeDisabled();
                }

                $('#salaryCategoryLi' + delCategoryId).remove();

                var salaryRowId = "salaryRowId" + categoryId;
                $('#' + salaryRowId).remove();
                delRow.closest('tr').remove();
                if (!isAnyCategoryExist) {
                    $('#salaryButton').remove();
                }
            },
            error: function (error) {
                e.preventDefault();
                var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
                $('.errorMessage').html(errorMessage).show();
            }

        });
    });
});

$(document).ready(function() {
    $('#textbox1').val(this.checked);

    $('#checkbox1').change(function() {
        if(this.checked) {
            var returnVal = confirm("Are you sure?");
            $(this).prop("checked", returnVal);
        }
        $('#textbox1').val(this.checked);
    });
});

$(document).ready(function () {
    $('#formStepConfiguration').on("submit", function (e) {
        e.preventDefault();
        var url = "/company/configuration/step/costCategory/check-exist-category";

        $.ajax({
            type: 'POST',
            url: url,
            success: function () {
                location.reload();
            },
            error: function (error) {
                var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
                $('.errorMessage').html(errorMessage).show();
            }
        });
    });
});
