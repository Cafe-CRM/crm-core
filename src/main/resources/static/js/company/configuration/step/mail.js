$(document).ready(function () {
    $('#formStepConfiguration').on("submit", function (e) {
        e.preventDefault();
        var url = "/company/configuration/step/mail/add";
        var settingsName = $('#inputMailSettingsName').val();
        var email = $('#inputMailEmail').val();
        var password = $('#inputMailPassword').val();
        var data = {settingsName: settingsName, email: email, password: password};
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

