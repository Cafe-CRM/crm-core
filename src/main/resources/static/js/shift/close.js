function switchBonusInputVisibility(id) {
    if ($('#inputBonus' + id).attr('type') == 'hidden') {
        $('#inputBonus' + id).attr('type', 'text');
    } else {
        $('#inputBonus' + id).attr('type', 'hidden');
    }
}


function checkInputDataWithShortage() {
    checkNoteInputData();
}


function checkInputData() {
    checkNoteInputData();
    checkCashInputData();
}

function checkNoteInputData() {
    $('.classInputNote').each(function() {
        var noteValue = this.value;
        if(!$.trim(noteValue).length) {
            $('.collapseNotes').collapse('show');
        }
    });
}

function checkCashInputData() {
    var totalCashBox = parseFloat($("#totalCashBox").val());
    var cashBox = parseFloat($("#cashBox").val());
    var payWithCard = parseFloat($("#payWithCard").val());
    var bankCashBox = parseFloat($("#bankCashBox").val());
    if (isNaN(cashBox) || isNaN(bankCashBox)){
        var errorMessage = '<h4 style="color:red;" align="center">Есть незаполненные поля</h4>';
        $('.errorMessage').html(errorMessage).show();
    }
    if ($("#cashBox").val() == ''){
        $("#cashBox").css('border', 'red 3px solid');
    } else {
        $("#cashBox").css('border', '');
    }
    if ($("#bankCashBox").val() == ''){
        $("#bankCashBox").css('border', 'red 3px solid');
    } else {
        $("#bankCashBox").css('border', '');
    }
    var budget = cashBox + bankCashBox;
    if (budget < totalCashBox) {
        $("#cashBox").css('border', 'red 3px solid');
        $("#bankCashBox").css('border', 'red 3px solid');
        $('#errorBlock').html('Сверка бюджета не прошла!');
        $("#closeShiftButtonShortage").css('visibility', 'visible');

        var shortfallAmount = (totalCashBox - budget);
        var shortfallMessage = '<h4 style="color:red;" align="center">В кассе не хватает '+shortfallAmount+' рублей</h4>';
        $('.shortfallAmount').html(shortfallMessage).show();
    } else if (budget >= totalCashBox) {
        $('#budget').attr('type', 'submit');
    }
}

function recalculation() {
    var usersBonuses = 0;
    $('.userBonus').each(function() {
        var bonus = $(this).val();
        if ($.isNumeric(bonus)) {
            usersBonuses += parseInt(bonus);
        }
    });
    $.ajax({
        type: "POST",
        url: "/manager/shift/recalculation",
        data: {usersBonuses: usersBonuses},
        success: function (data) {
            $('#usersTotalShiftSalary').val(data[0]);
            $('#totalCashBox').val(data[1]);
        },
        error: function (error) {
            console.log(error.responseText);
        }
    });
}

$(document).ready(function () {
    $('#checklistInputForm').submit(function (e) {
        e.preventDefault();
        var form = document.getElementById('checklistInputForm');
        var inputs = form.getElementsByTagName('input');
        var isChecked = false;
        for(var x = 0; x < inputs.length; x++) {
            if(inputs[x].type == 'checkbox') {
               isChecked = inputs[x].checked;
                if(!isChecked) {
                    $('#checklistModal').find('.showMessage').text('Вы выбрали не все пункты');
                    break;
                }
            }
        }
        if(isChecked) {
            var checklistModal = $('#checklistModal');
            var addShiftModal = $('#add');
            addShiftModal.modal('show');
            checklistModal.modal('hide');
        }
    });
});

$(document).ready(function () {
    $('#checklistOnCloseInputForm').submit(function (e) {
        var form = document.getElementById('checklistOnCloseInputForm');
        var inputs = form.getElementsByTagName('input');
        var isChecked = false;
        for(var x = 0; x < inputs.length; x++) {
            if(inputs[x].type == 'checkbox') {
                isChecked = inputs[x].checked;
                if(!isChecked) {
                    $('#checklistOnCloseModal').find('.showMessage').text('Вы выбрали не все пункты');
                    break;
                }
            }
        }
        if(isChecked) {
            var checklistModal = $('#checklistOnCloseModal');
            checklistModal.modal('hide');
        } else {
            e.preventDefault();
        }
    });
});