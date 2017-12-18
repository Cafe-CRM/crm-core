var initialEditUserFormMap = {};

function arraysEqual(initialData, updatedData) {
    if (initialData === null || updatedData === null) {
        return false;
    }
    return JSON.stringify(initialData) === JSON.stringify(updatedData);
}

function grabUpdatedUserFormData(id) {
    var userId = $('.formEditAllUsers' + id).attr("data-id");
    var email = $('.editAllUsersEmail' + id).val();
    var phone = $('.editAllUsersPhone' + id).val();
    var oldPassword = $('.editAllUsersOldPassword' + id).val();
    var newPassword = $('.editAllUsersNewPassword' + id).val();
    var repeatedPassword = $('.editAllUsersRepeatedPassword' + id).val();
    var firstName = $('.editAllUsersFirstName' + id).val();
    var lastName = $('.editAllUsersLastName' + id).val();
    var baseSalary = $('.editAllUsersBaseSalary' + id).val();
    var userPassword = $('.editAllUsersPassword' + id).val();
    var companyId = $('.formEditAllUsersCompanyId' + id).val();

    if (!isValidPasswordsData(oldPassword, newPassword, repeatedPassword)) {
        return false;
    }
    if (!isSalaryValid(id)) {
        return false;
    }

    var positionCheckboxId = '.editAllUsers-PositionCheckbox' + id;
    var positionsIds = $(positionCheckboxId + ' input:checkbox:checked').map(function () {
        return $(this).val();
    }).get();
    var rolesCheckboxId = '.editAllUsers-RoleCheckbox' + id;
    var rolesIds = $(rolesCheckboxId + ' input:checkbox:checked').map(function () {
        return $(this).val();
    }).get();

    var resultEditUserFormMap = {};
    resultEditUserFormMap['email'] = email;
    resultEditUserFormMap['phone'] = phone;
    resultEditUserFormMap['positionsIds'] = positionsIds;
    resultEditUserFormMap['rolesIds'] = rolesIds;
    resultEditUserFormMap['firstName'] = firstName;
    resultEditUserFormMap['lastName'] = lastName;
    resultEditUserFormMap['ShiftSalary'] = baseSalary;
    resultEditUserFormMap['oldPassword'] = oldPassword;
    resultEditUserFormMap['newPassword'] = newPassword;
    resultEditUserFormMap['repeatedPassword'] = repeatedPassword;
    resultEditUserFormMap['password'] = userPassword;
    resultEditUserFormMap['id'] = userId;
    resultEditUserFormMap['companyId'] = companyId;
    return resultEditUserFormMap;
}

function grabInitialEditUserFormData(id) {
    var email = $('.editAllUsersEmail' + id).val();
    var phone = $('.editAllUsersPhone' + id).val();
    var positionCheckboxId = '.editAllUsers-PositionCheckbox' + id;
    var positionsIds = $(positionCheckboxId + ' input:checkbox:checked').map(function () {
        return $(this).val();
    }).get();
    var baseSalary = $('.editAllUsersBaseSalary' + id).val();
    var rolesCheckboxId = '.editAllUsers-RoleCheckbox' + id;
    var rolesIds = $(rolesCheckboxId + ' input:checkbox:checked').map(function () {
        return $(this).val();
    }).get();
    initialEditUserFormMap['email'] = email;
    initialEditUserFormMap['phone'] = phone;
    initialEditUserFormMap['positionsIds'] = positionsIds;
    initialEditUserFormMap['rolesIds'] = rolesIds;
    initialEditUserFormMap['baseSalary'] = baseSalary;
}

function criticalUserDataUpdated(initialData, updatedData) {
    var emailsEqual = initialData['email'] === updatedData['email'];
    var phonesEqual = initialData['phone'] === updatedData['phone'];
    var baseSalaryEqual = initialData['baseSalary'] === updatedData['baseSalary'];
    var rolesEqual = arraysEqual(initialData['rolesIds'], updatedData['rolesIds']);
    var positionsEqual = arraysEqual(initialData['positionsIds'], updatedData['positionsIds']);
    var userPasswordUpdated = !(isBlank(updatedData['oldPassword']) && isBlank(updatedData['newPassword'])
        && isBlank(updatedData['repeatedPassword']));
    return (!emailsEqual || !phonesEqual || !baseSalaryEqual || !rolesEqual || !positionsEqual || userPasswordUpdated);
}

function editUserFormData(id) {
    event.preventDefault();
    $('.errorMessage').html('').hide();
    var resultEditUserFormMap = grabUpdatedUserFormData(id);
    if (criticalUserDataUpdated(initialEditUserFormMap, resultEditUserFormMap)) {
        $('#allUserEdit' + id).modal('hide');
        $('#passwordPrompt' + id).modal('show');
    } else {
        var message = '<h4 style="color:red;" align="center">Внесите изменения для редактирования данных</h4>';
        $('.errorMessage').html(message).show();
    }
}

function convertObjToArray(data) {
    var dataToSend = [];
    var key;
    var properties = Object.keys(data);
    for (var i = 0; i < properties.length; i++) {
        key = properties[i];
        dataToSend.push({name: key, value: data[key]});
    }
    return dataToSend;
}

function sendEditUserFormData(data, id) {
    var dataJson = convertObjToArray(data);
    $.ajax({
        type: 'post',
        url: '/boss/user/edit',
        data: dataJson,
        success: function () {
            location.reload();
        },
        error: function (error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            var modal = "#allUserEdit" + id;
            $('.errorMessage').html(errorMessage).show();
            $(modal).modal('show');
        }
    })
}

function getBossPassword(id) {
    var resultEditUserFormMap = grabUpdatedUserFormData(id);
    resultEditUserFormMap['bossPasswordRequired'] = true;
    resultEditUserFormMap['bossPassword'] = $('.bossPassword' + id).val();
    sendEditUserFormData(resultEditUserFormMap, id);
}

function isValidPasswordsData(oldPassword, newPassword, repeatedPassword) {
    if (isBlank(oldPassword) && isBlank(newPassword) && isBlank(repeatedPassword)) {
        return true;
    }
    if (isBlank(oldPassword)) {
        var errorMessage = '<h4 style="color:red;" align="center">Не указан старый пароль</h4>';
        $('.errorMessage').html(errorMessage).show();
        return false;
    }
    if (isBlank(newPassword)) {
        var errorMessage = '<h4 style="color:red;" align="center">Не указан новый пароль</h4>';
        $('.errorMessage').html(errorMessage).show();
        return false;
    }
    if (isBlank(repeatedPassword)) {
        var errorMessage = '<h4 style="color:red;" align="center">Не указан повторный пароль</h4>';
        $('.errorMessage').html(errorMessage).show();
        return false;
    }
    if (newPassword != repeatedPassword) {
        var errorMessage = '<h4 style="color:red;" align="center">Новый пароль и повторный не совпадают</h4>';
        $('.errorMessage').html(errorMessage).show();
        return false;
    }
    return true;
}

function isBlank(str) {
    return str.length === 0 || str.trim() === ""
}


$(document).ready(function () {
    $('.formAddUser').on('submit', function (event) {
        event.preventDefault();

        $('.errorMessage').html('').hide();
        if (!$('#isDefaultPassword').is(':checked')) {
            if ($('.addUserFirstPassword').val() != $('.addUserSecondPassword').val()) {
                var errorMessage = '<h4 style="color:red;" align="center">Пароли не совпадают</h4>';
                $('.errorMessage').html(errorMessage).show();
                return false;
            }
        }
        var positionsIds = $('.addUser-PositionCheckbox input:checkbox:checked').map(function () {
            return $(this).val();
        }).get();
        var rolesIds = $('.addUser-RoleCheckbox input:checkbox:checked').map(function () {
            return $(this).val();
        }).get();
        var data = $(this).serializeArray();
        data.push({name: 'positionsIds', value: positionsIds}, {name: 'rolesIds', value: rolesIds});
        $.ajax({
            type: 'post',
            url: '/boss/user/add',
            data: data,
            success: function (data) {
                location.reload();
            },
            error: function (error) {
                var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
                $('.errorMessage').html(errorMessage).show();
            }
        })
    });
});

$(document).ready(function () {
    $('.formAddPosition').on('submit', function (event) {
        event.preventDefault();
        $('.errorMessage').html('').hide();
        if ($('#percentageOfSales').val() == '') {
            var errorMessage = '<h4 style="color:red;" align="center">' + 'Поле Процент не должно быть пустым!' + '</h4>';
            $('.errorMessage').html(errorMessage).show();
            return;
        }
        var data = $(this).serialize();
        $.ajax({
            type: 'post',
            url: '/boss/user/position/add',
            data: data,
            success: function (data) {
                location.reload();
            },
            error: function (error) {
                var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
                $('.errorMessage').html(errorMessage).show();
            }
        })
    });
});

$(document).ready(function () {
    $('.formEditPosition').on('submit', function (event) {
        event.preventDefault();
        $('.errorMessage').html('').hide();
        if ($('#percentageOfSales').val() == '') {
            var errorMessage = '<h4 style="color:red;" align="center">' + 'Поле Процент не должно быть пустым!' + '</h4>';
            $('.errorMessage').html(errorMessage).show();
            return;
        }
        var data = $(this).serialize();
        $.ajax({
            type: 'post',
            url: '/boss/user/position/edit',
            data: data,
            success: function (data) {
                location.reload();
            },
            error: function (error) {
                var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
                $('.errorMessage').html(errorMessage).show();
            }
        })
    });
});

$(document).ready(function () {
    $('#isDefaultPassword').on('click', function () {
        if ($(this).is(':checked')) {
            $(".addUserFirstPassword").val('default').removeAttr('required');
            $(".addUserSecondPassword").val('default').removeAttr('required');
            $("#addUserFirstPasswordAllForm").hide();
            $("#addUserSecondPasswordAllForm").hide();

        } else {
            $(".addUserFirstPassword").val('').attr('required', 'required');
            $(".addUserSecondPassword").val('').attr('required', 'required');
            $("#addUserFirstPasswordAllForm").show();
            $("#addUserSecondPasswordAllForm").show();
        }
    })
});

$(function () {
    $('div[id="inputPercentFromSales"]').hide();

    //show it when the checkbox is clicked
    $('input[name="isPositionUsePercentOfSales"]').on('click', function () {
        if ($(this).prop('checked')) {
            $('div[id="inputPercentFromSales"]').fadeIn();
        } else {
            $('div[id="inputPercentFromSales"]').hide();
        }
    });
});

function isSalaryValid(id) {
    var salary = $('.editAllUsersBaseSalary' + id).val();
    if (salary < 0 || salary >= 2147483647) {
        var errorMessage = '<h4 style="color:red;" align="center">Поле оклад должно быть цифрой от 0 до 2147483647</h4>';
        $('.errorMessage').html(errorMessage).show();
        return false;
    }
    return true;
}