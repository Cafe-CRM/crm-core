$(document).ready(function () {
    $('#form-registration').submit(function (e) {
        e.preventDefault();
        var url = '/registration';
        var formData = $('#form-registration').serialize();
        if ($('#form-password').val() == $('#form-password-repeat').val()) {
            $.ajax({
                type: 'POST',
                url: url,
                data: formData,
                success: function (data) {
                    window.location.href = "/login";
                    localStorage.setItem('registration', 'ok');
                },
                error: function (error) {
                    var modal = $('#errorModal');
                    modal.find('.messageAd').text(error.responseText);
                    modal.modal('show');

                }

            });
        } else {
            var modal = $('#errorModal');
            modal.find('.messageAd').text('Введенные пароли не совпадают!');
            modal.modal('show');
        }
    });
});