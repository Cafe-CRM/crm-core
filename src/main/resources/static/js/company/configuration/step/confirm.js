$(document).ready(function () {
    $('#confirm').on("click", function (e) {
        var url = "/company/configuration/step/confirm/test";
        $.ajax({
            type: 'POST',
            url: url,
            success: function (data) {
            },
            error: function (error) {
                e.preventDefault();
                var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
                $('.errorMessage').html(errorMessage).show();
            }

        });
    });
});