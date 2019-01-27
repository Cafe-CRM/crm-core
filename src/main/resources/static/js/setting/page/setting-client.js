$(document).ready(function () {

    $('.checkboxSettingEnable').on('click', function () {
        var url = '/boss/settings/close-client/changeStatus';
        var id = $(this).data('id');
        var enable = $(this).val();
        localStorage.setItem( 'objectToPass', $(this).val());
        var data = {id : id, enable : enable};
        $.ajax({
            type: 'post',
            url: url,
            data: data,
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