
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
    if (!isValidPasswordsData(newPassword, repeatedPassword)) {
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
    resultEditUserFormMap['shiftSalary'] = baseSalary;
    resultEditUserFormMap['oldPassword'] = oldPassword;
    resultEditUserFormMap['newPassword'] = newPassword;
    resultEditUserFormMap['repeatedPassword'] = repeatedPassword;
    resultEditUserFormMap['password'] = userPassword;
    resultEditUserFormMap['id'] = userId;
    resultEditUserFormMap['companyId'] = companyId;
    return resultEditUserFormMap;
}

function editUserFormData(id) {
    event.preventDefault();
    $('.errorMessage').html('').hide();
    var resultEditUserFormMap = grabUpdatedUserFormData(id);
    if (!isSalaryValid(id)){
        return false;
    }
    sendEditUserFormData(resultEditUserFormMap);
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

function sendEditUserFormData(data) {
    var dataJson = convertObjToArray(data);
    $.ajax({
        type: 'post',
        url: '/supervisor/user/edit',
        data: dataJson,
        success: function () {
            location.reload();
        },
        error: function (error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('.errorMessage').html(errorMessage).show();
        }
    })
}

function isValidPasswordsData(newPassword, repeatedPassword) {
    if (isBlank(newPassword) && isBlank(repeatedPassword)) {
        return true;
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
    $('.formAddRole').on('submit', function (event) {
        event.preventDefault();
        $('.errorMessage').html('').hide();
        var data = $(this).serialize();
        $.ajax({
            type: 'post',
            url: '/supervisor/role/add',
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
    $('.formEditRole').on('submit', function (event) {
        event.preventDefault();
        $('.errorMessage').html('').hide();
        var data = $(this).serialize();
        $.ajax({
            type: 'post',
            url: '/supervisor/role/edit',
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

function isSalaryValid(id) {
    var salary = $('.editAllUsersBaseSalary' + id).val();
    if (salary < 0 || salary >= 2147483647) {
        var errorMessage = '<h4 style="color:red;" align="center">Поле оклад должно быть цифрой от 0 до 2147483647</h4>';
        $('.errorMessage').html(errorMessage).show();
        return false;
    }
    return true;
}