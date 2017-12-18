$(document).ready(function () {
    $('#buttonCardEnable').on('click', function () {
        var url = '/boss/settings/card/changeStatus';
        var data = $('#inputCardEnable').serialize();
        $.ajax({
            type: 'post',
            url: url,
            data: data,
            success: function () {
                location.reload();
            }
        })
    })
});
