$(document).ready(function () {
    $('#formStepConfiguration').on("submit", function (e) {
        e.preventDefault();
        var url = "/company/configuration/step/first-hour-price/add";
        var firstHour = $('#inputPriceFirstHour').val();
        var data = {firstHour: firstHour};
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
    $('input[type="number"]').get(0).focus();
});

