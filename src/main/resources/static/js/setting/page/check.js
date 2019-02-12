$(document).ready(function () {

    $('#checkCheckbox').on('click', function () {
        $.ajax({
            type: 'GET',
            url: '/boss/settings/check-setting/changeStatus',
            success: function () {
                location.reload();
            },
            error: function (error) {
                var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
                $('.errorMessage').html(errorMessage).show();
            }
        })
    })
});