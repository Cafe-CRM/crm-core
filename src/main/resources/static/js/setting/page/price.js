$(document).ready(function () {
    $('.buttonPropertyRefresh').on('click', function () {
        var url = '/boss/settings/price-setting/edit';
        var id = $(this).data('id');
        var val = $('#inputPriceProperty' + id).val();
        if (Number(val) < 0) {
            return;
        }
        var data = $('#formProperty' + id).serialize();
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