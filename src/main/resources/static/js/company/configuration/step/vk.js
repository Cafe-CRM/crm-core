$(document).ready(function () {
    $('#formStepConfiguration').on("submit", function (e) {
        e.preventDefault();
        var url = "/company/configuration/step/vk/add";
        var accessToken = $('#inputAccessToken').val();
        var applicationId = $('#inputApplicationId').val();
        var chatId = $('#inputChatId').val();
        var messageName = $('#inputMessageName').val();
        var apiVersion = $('#inputApiVersion').val();
        var data = {
            accessToken: accessToken, applicationId: applicationId, chatId: chatId,
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
                e.preventDefault();
                var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
                $('.errorMessage').html(errorMessage).show();
            }

        });
    });
    $('input[type="text"]').get(0).focus();
});

$(document).ready(function () {
    $("#VKSettingsInput").toggle();
    $("#appRegistrationBlock").toggle();
    $("#VKTokenBlock").toggle();
    $("#VKMessageNameBlock").toggle();
    $("#VKChatBlock").toggle();
});

$("#showVKInputButton").click(function () {
    $("#VKSettingsInput").toggle();
});

$("#appRegistrationButton").click(function () {
    $("#appRegistrationBlock").toggle();

    if (!document.getElementById("VKChatIDLine").hasAttribute("required")) {
        document.getElementById("VKChatIDLine").required = true;
        document.getElementById("VKTokenLine").required = true;
        document.getElementById("appID").required = true;
    } else {
        document.getElementById("VKChatIDLine").required = false;
        document.getElementById("VKTokenLine").required = false;
        document.getElementById("appID").required = false;
    }
});

$("#appIDButton").click(function () {
    generateVKTokenLink($('#appID').val());
    $("#VKTokenBlock").toggle();
});

$("#parseTokenButton").click(function () {
    $("#VKMessageNameBlock").toggle();
});

$("#VKMessageNameButton").click(function () {
    $("#VKChatBlock").toggle();
});

$("#parseChatIDButton").click(function () {
    var url = "/company/configuration/step/vk/add";
    var accessToken = parseVKToken($('#VKTokenLine').val());
    var applicationId = $('#appID').val();
    var chatId = parseChatID($('#VKChatIDLine').val());
    var messageName = $('#VKMessageName').val();
    var apiVersion = $('#inputApiVersion').val();
    var data = {
        accessToken: accessToken, applicationId: applicationId, chatId: chatId,
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
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('.errorMessage').html(errorMessage).show();
        }

    });
});

function generateVKTokenLink(appID) {
    var VKTokenLink = 'https://oauth.vk.com/authorize?client_id=' + appID +
        '&amp;display=page&amp;redirect_uri=https://oauth.vk.com/blank.html&amp;scope=offline,messages&amp;' +
        'response_type=token&amp;v=5.68&amp;state=123456';
    document.getElementById("VKTokenLink").innerHTML = '<a href="' +
        VKTokenLink + '" target="_blank">Переходите по ссылке</a>';
}

function parseVKToken(tokenLine) {
    var tokenLineParameters = tokenLine.split('#');
    var part = tokenLineParameters[1].split("&");
    var item = part[0].split("=");
    if (item[0] === 'access_token') {
        return item[1];
    }
}

function parseChatID(chatIDLine) {
    var chatIdLineParameters = chatIDLine.split("?")
    var item = chatIdLineParameters[1].split("=c");
    if (item[0] === 'sel') {
        return item[1];
    }
}