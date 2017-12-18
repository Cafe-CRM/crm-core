function setMasterKey() {
    var new_pass_1 = $('#newKey').val();
    var new_pass_2 = $('#repeatNewKey').val();
    if (new_pass_1 === new_pass_2) {
        var formData = {
            new: $('#newKey').val(),
            old: $('#bossPassword').val(),
            secondNew: $('#repeatNewKey').val(),
        }
        $.ajax({
            type: "POST",
            url: "/boss/settings/masterKey/editMasterKey",
            data: formData,
            success: function (result) {
                var modal = $('#successModal');
                modal.find('.messageAd').text(result);
                modal.modal('show');
            },
            error: function (e) {
                var modal = $('#errorModal');
                modal.find('.messageAd').text(e.responseText);
                modal.modal('show');
            }
        });
    } else {
        $('#newKey, #repeatNewKey').val('').attr("placeholder", "");
        $("#wrongModal").modal('show')
        return false;
    }
}