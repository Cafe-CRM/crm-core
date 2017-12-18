$(document).ready(function () {
    $('#formStepConfiguration').on("submit", function (e) {
        e.preventDefault();
        var url = "/company/configuration/step/masterKey/add";
        var masterKey = $('#inputMasterKey').val();
        var data = {masterKey: masterKey};
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

