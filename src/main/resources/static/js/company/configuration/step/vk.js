$(document).ready(function () {
    try {
        $('#formStepConfiguration').on("submit", function (e) {
            e.preventDefault();
            if ($("#vkInstruction").is(":visible")) {
                sendInstructionData();
                return;
            }

            var url = "/company/configuration/step/vk/add";
            var accessToken = $('#inputAccessToken').val().trim();
            var applicationId = $('#inputApplicationId').val().trim();
            var serviceChatId = $('#inputChatId').val().trim();
            var adminChatId = $('#inputAdminChatId').val().trim();
            var messageName = $('#inputMessageName').val().trim();
            var apiVersion = $('#inputApiVersion').val().trim();
            var data = {
                accessToken: accessToken,
                applicationId: applicationId,
                serviceChatId: serviceChatId,
                adminChatId: adminChatId,
                messageName: messageName,
                apiVersion: apiVersion
            };
            $.ajax({
                type: 'POST',
                url: url,
                data: data,
                success: function (data) {
                    location.reload();
                },
                error: function (error) {
                    e.preventDefault();
                    var errorMessage = '<h4>' + error.responseText + '</h4>';
                    $('.errorMessage').removeClass('hidden');
                    $('.errorMessage').html(errorMessage).show();
                }

            });
        });
        $('input[type="text"]').get(0).focus();
    } catch (e) {
        alert(e);
    }
});

$("#showVKInputButton").click(function () {
    if ($("#vkInstruction").is(":visible")) {
        clearRequiredInstructionInputs();
        $("#vkInstruction").toggle();
    }
    $("#VKSettingsInput").toggle();

    if (document.getElementById("inputAccessToken").hasAttribute("required")) {
        clearRequiredSettingsInputs();
    } else {
        addRequiredSettingsInputs();
    }
});

$("#appRegistrationButton").click(function () {
    if ($("#VKSettingsInput").is(":visible")) {
        clearRequiredSettingsInputs();
        $("#VKSettingsInput").toggle();
    }
    $("#vkInstruction").toggle();

    if (document.getElementById("appID").hasAttribute("required")) {
        clearRequiredInstructionInputs();
    } else {
        addRequiredInstructionInputs();
    }
});

$("#appIDButton").click(function () {
    var appId = $('#appID').val().trim();
    if (!isBlank(appId)) {
        generateVKTokenLink(appId);

        if (!$("#VKTokenBlock").is(":visible")) {
            $("#VKTokenBlock").toggle();
            $('#VKTokenLine').attr('required', 'required');
        }

        $('#errorId').html("").show();
    } else {
        var errorMessage = '<h4 style="color:red; margin-left: 15px;">id приложения не может быть пустым!</h4>';
        $('#errorId').html(errorMessage).show();
    }
});

$("#parseTokenButton").click(function () {
    if (!isBlank($('#VKTokenLine').val())) {

        if (!$("#VKMessageNameBlock").is(":visible")) {
            $("#VKMessageNameBlock").toggle();
            $('#VKMessageName').attr('required', 'required');
        }

        $('#errorToken').html("").show();
    } else {
        var errorMessage = '<h4 style="color:red; margin-left: 15px;">Значение адресной строки не должно быть пустым!</h4>';
        $('#errorToken').html(errorMessage).show();
    }
});

$("#VKMessageNameButton").click(function () {
    if (!isBlank($('#VKMessageName').val())) {

        if (!$("#VKChatBlock").is(":visible")) {
            $("#VKChatBlock").toggle();
            $('#VKChatIDLine').attr('required', 'required');
        }

        $('#errorMessageName').html("").show();
    } else {
        var errorMessage = '<h4 style="color:red; margin-left: 15px;">Название сообщения не должно быть пустым!</h4>';
        $('#errorMessageName').html(errorMessage).show();
    }
});

$("#parseChatIDButton").click(function () {
    if (!isBlank($('#VKChatIDLine').val())) {

        if (!$("#VKAdminChatBlock").is(":visible")) {
            $("#VKAdminChatBlock").toggle();
            $('#VKAdminChatIDLine').attr('required', 'required');
        }

        $('#errorChatId').html("").show();
    } else {
        var errorMessage = '<h4 style="color:red; margin-left: 15px;">Значение адресной строки не должно быть пустым!</h4>';
        $('#errorChatId').html(errorMessage).show();
    }
});

$("#parseAdminChatIDButton").click(function () {
    sendInstructionData();
});

function sendInstructionData() {
    if (!validateInstructionInputs()) {
        var errorMessage = '<h4>Одно или несколько полей в инструкции настройки приложения вк не заполнены!</h4>';
        $('.errorMessage').removeClass('hidden');
        $('.errorMessage').html(errorMessage).show();
        return;
    }

    if (isBlank($('#VKAdminChatIDLine').val())) {
        var errorMessage = '<h4 style="color:red; margin-left: 15px;">Значение адресной строки не должно быть пустым!</h4>';
        $('#errorAdminChatId').html(errorMessage).show();
        return;
    } else {
        $('#errorAdminChatId').html("").show();
    }
    var url = "/company/configuration/step/vk/add";
    var accessToken = parseVKToken($('#VKTokenLine').val().trim());
    var applicationId = $('#appID').val().trim();
    var serviceChatId = parseChatID($('#VKChatIDLine').val().trim());
    var adminChatId = parseChatID($('#VKAdminChatIDLine').val().trim());
    var messageName = $('#VKMessageName').val().trim();
    var apiVersion = $('#inputApiVersion').val().trim();
    var data = {
        accessToken: accessToken, applicationId: applicationId, serviceChatId: serviceChatId, adminChatId: adminChatId,
        messageName: messageName, apiVersion: apiVersion
    };
    $.ajax({
        type: 'POST',
        url: url,
        data: data,
        success: function (data) {
            location.reload();
        },
        error: function (error) {
            error.preventDefault();
            var errorMessage = '<h4>' + error.responseText + '</h4>';
            $('.errorMessage').removeClass('hidden');
            $('.errorMessage').html(errorMessage).show();
        }

    });
}

function generateVKTokenLink(appID) {
    var VKTokenLink = 'https://oauth.vk.com/authorize?client_id=' + appID +
        '&amp;display=page&amp;redirect_uri=https://oauth.vk.com/blank.html&amp;scope=offline&amp;' +
        'response_type=token&amp;v=5.68&amp;state=123456';
    document.getElementById("VKTokenLink").innerHTML = '<a href="' +
        VKTokenLink + '" target="_blank">Переходите по ссылке</a>';
}

function parseVKToken(tokenLine) {
    try {
        var tokenLineParameters = tokenLine.split('#');
        var part = tokenLineParameters[1].split("&");
        var item = part[0].split("=");
        if (item[0] === 'access_token') {
            return item[1];
        }
    } catch (e) {
        var errorMessage = '<h4 style="color:red; margin-left: 15px;">Не корректное значение адресной строки!</h4>';
        $('#errorAdminChatId').html(errorMessage).show();
    }
}

function parseChatID(chatIDLine) {
    var chatIdLineParameters = chatIDLine.split("?");
    var item = chatIdLineParameters[1].split("=c");
    if (item[0] === 'sel') {
        return item[1];
    }
}

function isBlank(str) {
    return str.length === 0 || str.trim() === ""
}

function validateInstructionInputs() {
    if(isBlank($('#appID').val())) return false;
    if(isBlank($('#VKTokenLine').val())) return false;
    if(isBlank($('#VKMessageName').val())) return false;
    if(isBlank($('#VKChatIDLine').val())) return false;
    return !isBlank($('#VKAdminChatIDLine').val());

}

function clearRequiredInstructionInputs() {
    $('#appID').removeAttr('required');
    $('#VKTokenLine').removeAttr('required');
    $('#VKMessageName').removeAttr('required');
    $('#VKChatIDLine').removeAttr('required');
    $('#VKAdminChatIDLine').removeAttr('required');
}

function addRequiredInstructionInputs() {
    $('#appID').attr('required', 'required');
}

function clearRequiredSettingsInputs() {
    $('#inputAccessToken').removeAttr('required');
    $('#inputApplicationId').removeAttr('required');
    $('#inputChatId').removeAttr('required');
    $('#inputAdminChatId').removeAttr('required');
    $('#inputMessageName').removeAttr('required');
    $('#inputApiVersion').removeAttr('required');
}

function addRequiredSettingsInputs() {
    $('#inputAccessToken').attr('required', 'required');
    $('#inputApplicationId').attr('required', 'required');
    $('#inputChatId').attr('required', 'required');
    $('#inputAdminChatId').attr('required', 'required');
    $('#inputMessageName').attr('required', 'required');
    $('#inputApiVersion').attr('required', 'required');
}