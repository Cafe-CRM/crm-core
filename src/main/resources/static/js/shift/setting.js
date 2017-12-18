
function getOtherProductsAndDisplay(iter) {
    var jBlock = jQuery('#otherBlock' + iter);
    var jButton = jQuery('#otherButton' + iter);
    var button = document.getElementById('otherButton' + iter);

    showClose(jBlock, jButton, button);
}

function getDirtyProductsAndDisplay(iter) {
    var jBlock = jQuery('#dirtyBlock' + iter);
    var jButton = jQuery('#dirtyButton' + iter);
    var button = document.getElementById('dirtyButton' + iter);

    showClose(jBlock, jButton, button);
}

function showClose(jBlock, jButton, button) {
    if (jBlock.hasClass('hidden')) {
        jBlock.removeClass('hidden');
        jButton.removeClass('btn-primary').addClass('btn-success');
        button.innerHTML = 'Закрыть';
    } else {
        jBlock.addClass('hidden');
        jButton.removeClass('btn-success').addClass('btn-primary');
        button.innerHTML = 'Посмотреть заказ';
    }
}

function deleteCalculate(calculateId) {
    var  formData = {
        password : $('#masterPassword' + calculateId).val(),
        calculateId : calculateId
    };

    $.ajax({
        type: "POST",
        url: "/manager/delete-calculate",
        data: formData,

        success: function (data) {
            location.reload();
        },
        error: function (error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('.newAmountError').html(errorMessage).show();
        }
    });
}