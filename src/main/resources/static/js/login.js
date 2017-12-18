$( document ).ready(function() {
    if (localStorage.getItem('registration') === 'ok'){
        localStorage.setItem('registration','none');
        var registrationSuccessMessage = '<h4 style="color:blue;" align="center">Регистрация завершена. Вы можете войти</h4>';
        $('.registrationSuccessMessage').html(registrationSuccessMessage).show();
    }
});


var recaptchaAnswer = "";
var onloadCallback = function() {
    grecaptcha.render('reCaptcha', {
        'sitekey' : '6LcmCzMUAAAAAE5IbSqx_D2MLo2dGWxGYoTgoQsw',
        'callback' : verifyCallback,
        "expired-callback": onRecaptchaExpired
    });
};
var verifyCallback = function(response) {
    recaptchaAnswer = response;
    $("#reCaptchaAnswer").val(response);
};
var onRecaptchaExpired = function() {
    recaptchaAnswer = "";
    $("#reCaptchaAnswer").val("");
};


function validateEmail(email) { //Validates the email address
    var emailRegex = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
    return emailRegex.test(email);
}

function validatePhone(phone) { //Validates the phone number
    var phoneRegex = /^(8){1}[\d]+$/;
    return phoneRegex.test(phone);
}


$(document).ready(function () {
    $("#signin").submit(function (event) {
        var username = $("#username").val();
        if (!(validateEmail(username.trim()) || validatePhone(username.trim()))) {
            event.preventDefault();
            $("#incorrect").text("Некоректный формат номера телефона или e-mail").addClass("alert-danger");
        }
        if ($("div").is("#reCaptcha")) {
            if (recaptchaAnswer === "") {
                event.preventDefault();
                $("#invalidCaptcha").text("Пройдите капчу").addClass("alert-danger");
            }
        }
    });
});

$(document).ready(function () {
    if ($("div").is("#reCaptcha")) {
        var containerWidth = $('#recaptcha-container').width();
        var recaptchaWidth = 304;
        var margin = (containerWidth - recaptchaWidth) / 2 + "px";
        $('#reCaptcha').css('margin-left', margin);
    }
});

$(document).ready(function () {
    $('[data-toggle="popover"]')
        .popover();
});

$(document).ready(function () {
    $('#username, #password').on('click', function () {
        $("#incorrect, #wrong, #successful-logout").text("").removeClass("alert-danger");

    });
});
