ajax();
setInterval(ajax, 60000);
function cookiePanel(id) {
    if ($.cookie(id) == true.toString()) {
        $.cookie(id, "false");
    } else if ($.cookie(id) == false.toString() || $.cookie(id) == null) {
        $.cookie(id, "true");
    }
}

function editClientTimeStart(clientId, event) {
    event.preventDefault();
    var hours = $('#editHours' + clientId).val();
    if (hours < 0 || hours > 23 || hours == "") {
        var errorMessage = '<h4 style="color:red;" align="center">Допустимое значение часов от 0 до 23</h4>';
        $('.messageEdit' + clientId).html(errorMessage).show();
        return false;
    }
    var minutes = $('#editMinutes' + clientId).val();
    if (minutes < 0 || minutes > 59 || minutes == "") {
        var errorMessage = '<h4 style="color:red;" align="center">Допустимое значение минут от 0 до 59</h4>';
        $('.messageEdit' + clientId).html(errorMessage).show();
        return false;
    }

    var url = "/manager/edit-client-time-start";
    var data = {clientId: clientId, hours: hours, minutes: minutes};

    $.post(url, data, function () {
        location.reload()
    })
        .fail(function (response) {
            var errorMessage = '<h4 style="color:red;" align="center">Не удалось обновить время</h4>';
            $('.messageEdit' + clientId).html(errorMessage).show();
            window.setTimeout(function () {
                location.reload()
            }, 1000);
        });
}

function ajaxCardDiscount(id, calcId) {

    $.ajax({
        type: "POST",
        url: "/manager/card/add-card-on-client",
        data: $('#cardSel' + id).serialize(),

        success: function (data) {
            $('#dopDiscount' + id).html(data + '%' + ' + ');
            setTimeout(function () {
                ajaxForCalculate(calcId)
            }, 500);
        },
        error: function () {
            console.log('ajaxCardDiscount сломался? ');
        }
    });
}


function ajaxModal(id, cardEnable) {
    $.ajax({
        type: "POST",
        url: "/manager/output-clients",
        data: $('#formTest' + id).serialize(),

        success: function (data) {
            if (data == "") {
                $('#tb' + id).html(" ");
            } else {
                var allpr = 0;
                var payWithCard = 0;
                var str = "";
                var strForAlert = '';
                var flag = false;
                for (var i = 0; i < data.length; i++) {
                    var hours = +data[i].passedTime.hour < 10 ? ('0' + data[i].passedTime.hour) : data[i].passedTime.hour;
                    var min = +data[i].passedTime.minute < 10 ? ('0' + data[i].passedTime.minute) : data[i].passedTime.minute;
                    var time = hours + ':' + min;
                    var order = "";

                    var cardNull = (cardEnable == 'true') ? '<td class="cent">' + ((data[i].card == null) ? 'Нет' : data[i].card.name) + '</td>' : '';
                    var description = '<td class="cent">' + data[i].description + '</td>';
                    var timeHM = '<td class="cent">' + time + '</td>';
                    var priceTime = '<td class="cent">' + data[i].priceTime + '</td>';
                    var priceMenu = '<td class="cent"><div  class="dropdown"><button onclick="getLayerProductAjax(' + data[i].id + ')"   class="btn btn-primary" data-toggle="dropdown" style="width: 100px;font-size: 20px;height:30px;margin-right: 5px;margin-top: 5px;padding: 0px">' + data[i].priceMenu + 'р' + '</button><div id = "clientDropMenu' + data[i].id + '"  style="width: 250px;font-size: 20px" class="dropdown-menu dropdown-menu-right">' + order + '</div></div></div> </td>';
                    var discount = '<td class="cent">' + ((+data[i].discount) + (+data[i].discountWithCard)) + '</td>';
                    var withCard = (cardEnable == 'true') ? '<td class="cent">' + data[i].payWithCard + '</td>' : '';
                    var cache = '<td class="cent">' + data[i].cache + '</td>';
                    str +='<tr>' + cardNull + description + timeHM + priceTime + priceMenu + discount + withCard + cache + '</tr>';

                    if (cardEnable == 'true') {
                        payWithCard += data[i].payWithCard;
                        if (data[i].card != null && data[i].card.balance < data[i].payWithCard) {
                            strForAlert += (data[i].card.name + ', ');
                            flag = true;
                        }
                    }
                    allpr += data[i].cache;
                }

                if (flag) {
                    if (confirm("Карта: " + strForAlert + "будет оплачена в долг.\nВы уверены?")) {
                        $('#checkModal' + id).modal();
                    }

                } else {
                    $('#checkModal' + id).modal();
                }


                $('#head' + id).html($('#head1' + id).text());
                var allText = (cardEnable == 'true') ? payWithCard + 'р<br/>' + allpr + 'р' : allpr;
                $('#all' + id).html(allText);
                $('#tb' + id).html(str);
            }
        },
        error: function () {
            console.log('ajaxModal сломался? ');
        }
    });
}

function deleteClients() {
    var boxes2 = $('.clientsToDel');
    var isChecked = false;
    for(var x = 0; x < boxes2.length; x++) {
        isChecked = boxes2[x].checked;
        if(isChecked) {
            return;
        }
    }
        var errorMessage = '<h4 style="color:red;" align="center">Выберите клиентов для удаления</h4>';
        $('.clientDel').html(errorMessage).show();
        window.setTimeout(function () {
            location.reload()
        }, 1000);
}


function getLayerProductAjax(clientId) {
    $.ajax({
        type: "POST",
        url: "/manager/get-layer-products-on-client",
        data: {clientId: clientId},

        success: function (data) {
            var str = " ";
            for (var i = 0; i < data.length; i++) {
                str += '<li>' + data[i].name + ' №' + data[i].id + ' (' + data[i].cost + 'р)' + '</li>';
            }
            $('#clientDropMenu' + clientId).html(str);
        },
        error: function () {
            console.log('getLayerProductAjax сломался? ');
        }
    });
}

function ajaxForCalculate(calcId) {
    $.ajax({
        type: "POST",
        url: "/manager/calculate-price-on-calculate",
        data: {calculateId: calcId},
        success: function (data) {
            for (var i = 0; i < data.length; i++) {
                $('#price' + data[i].id).html(data[i].allPrice);
            }
        },
        error: function () {
            console.log('ajaxForCalculate сломался? ');
        }
    });
}

function ajax() {
    $.ajax({
        type: "POST",
        url: "/manager/calculate-price",
        success: function (data) {
            for (var i = 0; i < data.length; i++) {
                $('#price' + data[i].id).html(data[i].allPrice);
            }
        },
        error: function () {
            console.log('ajax сломался? ');
        }
    });
}

function check(id) {
    if ($('#checked' + id).is(':checked')) {
        $(".class" + id).prop("checked", true);
    } else {
        $(".class" + id).prop("checked", false);
    }
}

function check1(id) {
    if ($('#checked1' + id).is(':checked')) {
        $(".class1" + id).prop("checked", true);
    } else {
        $(".class1" + id).prop("checked", false);
    }
}

function subAjax(id, calcId) {
    $.ajax({
        type: "POST",
        url: "/manager/update-fields-client",
        data: $('#updateForm' + id).serialize(),
        success: function (data) {
            $('#clientMenuDescription' + id).text(data);
            setTimeout(function () {
                ajaxForCalculate(calcId)
            }, 500);
        },
        error: function () {
            console.log('subAjax сломался? ');
        }
    });
}

function createLayerProductAjax(prodId, calcId) {
    $('#productId' + calcId).val(prodId);
    $.ajax({
        type: "POST",
        url: "/manager/create-layer-product",
        data: $('#addProductOnClientForm' + calcId).serialize(),
        success: function (data) {
            var arr = data.clients;
            for (var i = 0; i < arr.length; i++) {
                $('#ajaxMenu' + arr[i].id + ' li:last').after('<li id = "pr' + data.id + '">' + data.name + ' №' + data.id + ' (' + data.cost + 'р)' + '</li>');
            }
            setTimeout(function () {
                getOpenClientsOnCalculateAjax(calcId)
            }, 500);
            prog(calcId);
            setTimeout(function () {
                ajaxForCalculate(calcId)
            }, 500);
        },
        error: function () {
            console.log('createLayerProductAjax сломался? ');
        }
    });
}

function createLayerProductWithFloatingPriceAjax(prodId, calcId, inputId) {
    $('#productId' + calcId).val(prodId);
    var price = $(('#' + inputId) + prodId + calcId).val();
    if (price == "" || price <= 0) {
        return false;
    }
    var data = $('#addProductOnClientForm' + calcId).serializeArray();
    data.push({name: 'productPrice', value: price});
    $.ajax({
        type: "POST",
        url: "/manager/create-layer-product-floating-price",
        data: data,
        success: function (data) {
            var arr = data.clients;
            for (var i = 0; i < arr.length; i++) {
                $('#ajaxMenu' + arr[i].id + ' li:last').after('<li id = "pr' + data.id + '">' + data.name + ' №' + data.id + ' (' + data.cost + 'р)' + '</li>');
            }
            setTimeout(function () {
                getOpenClientsOnCalculateAjax(calcId)
            }, 500);
            prog(calcId);
            setTimeout(function () {
                ajaxForCalculate(calcId)
            }, 500);
        },
        error: function () {
            console.log('createLayerProductWithFloatingPriceAjax сломался? ');
        }
    });
}

function addLayerProductOnClientAjax(layerProdId, calcId) {
    $('#productId' + calcId).val(layerProdId);
    $.ajax({
        type: "POST",
        url: "/manager/add-client-on-layer-product",
        data: $('#addProductOnClientForm' + calcId).serialize(),
        success: function (data) {
            var arr = data.clients;
            for (var i = 0; i < arr.length; i++) {
                if (document.getElementById('ajaxMenu' + arr[i].id).querySelector("#pr" + data.id) == null) {
                    $('#ajaxMenu' + arr[i].id + ' li:last').after('<li id = "pr' + data.id + '">' + data.name + ' №' + data.id + ' (' + data.cost + 'р)' + '</li>');
                }
            }
            setTimeout(function () {
                getOpenClientsOnCalculateAjax(calcId)
            }, 500);
            prog(calcId);
            setTimeout(function () {
                ajaxForCalculate(calcId)
            }, 500);
        },
        error: function () {
            console.log('addLayerProductOnClientAjax сломался? ');
        }
    });
}

function deleteLayerProductOnClientAjax(layerProdId, calcId) {
    $('#productId' + calcId).val(layerProdId);
    $.ajax({
        type: "POST",
        url: "/manager/delete-product-with-client",
        data: $('#addProductOnClientForm' + calcId).serialize(),
        success: function (data) {
            var arr = data.clients;
            for (var i = 0; i < arr.length; i++) {
                var v = document.getElementById('ajaxMenu' + arr[i].id).querySelector("#pr" + data.id);
                if (v != null) {
                    v.remove();
                }
            }
            getProductOnCalculateAjax(calcId);
            setTimeout(function () {
                getOpenClientsOnCalculateAjax(calcId)
            }, 500);
            prog(calcId);
            setTimeout(function () {
                ajaxForCalculate(calcId)
            }, 500);
        },
        error: function () {
            console.log('deleteLayerProductOnClientAjax сломался? ');
        }
    });
}

function getProductOnCalculateAjax(calcId) {
    $.ajax({
        type: "POST",
        url: "/manager/get-products-on-calculate",
        data: {calculateId: calcId},
        success: function (data) {
            var str;
            if (data != "") {
                for (var i = 0; i < data.length; i++) {
                    str += '<tr style="font-size: 20px;"><td>' + data[i].name + ' №' + data[i].id + '</td><td>' + data[i].description + '</td><td>' + data[i].cost + '</td><td><button onclick="addLayerProductOnClientAjax(' + data[i].id + ',' + calcId + ')" style="background:rgba(255,0,0,0);float: left;margin-left: 20px; border: none;"> <i class="fa fa-check" aria-hidden="true" style="font-size: 30px;color:#910405;"></i></button><button onclick="deleteLayerProductOnClientAjax(' + data[i].id + ',' + calcId + ')" style="background:rgba(255,0,0,0);float: left;margin-left: 20px; border: none;"><i class="fa fa-times" aria-hidden="true" style="font-size: 30px;color:#910405;"></i></button></td></tr>';
                }
            } else {
                str = null;
            }

            $("#prodOnCalculate" + calcId).html(str);
        },
        error: function () {
            console.log('getProductOnCalculateAjax сломался? ');
        }
    });
}

function getOpenClientsOnCalculateAjax(calcId) {
    $.ajax({
        type: "POST",
        url: "/manager/get-open-clients-on-calculate",
        data: {calculateId: calcId},
        success: function (data) {
            for (var i = 0; i < data.length; i++) {
                $('#priceMenu' + data[i].id).html(data[i].priceMenu);
            }
        },
        error: function () {
            console.log('getOpenClients сломался? ');
        }
    });
}

//2 снизу это скрипты поиска по продуктам/категориям
$(document).ready(function () {
    $("#search").keyup(function () {
        _this = this;

        $.each($("#mycateg a"), function () {
            if ($(this).text().toLowerCase().indexOf($(_this).val().toLowerCase()) === -1) {
                $(this).hide();
            } else {
                $(this).show();
            }
        });
    });
});

$(document).ready(function () {
    $("#searchPr").keyup(function () {
        _this = this;

        $.each($(".mytable tbody tr"), function () {
            if ($(this).text().toLowerCase().indexOf($(_this).val().toLowerCase()) === -1) {
                $(this).hide();
            } else {
                $(this).show();
            }
        });
    });
});

function prog(calcId) {
    var myTime = setInterval(
        function () {
            if (+$('#progress' + calcId).val() != 100) {
                $('#progress' + calcId).val(+$('#progress' + calcId).val() + 7);
            } else {
                $('#progress' + calcId).val(0);
                clearInterval(myTime);
                myTime = 0;
            }
        }, 10);
}

function closeClientDebt(calculateId) {
    if (isBlank($('#debtorName' + calculateId).val())) {
        var errorMessage = '<h4 style="color:red;" align="center">' + 'Обязательно укажите имя должника!' + '</h4>';
        $('#debtorNameError' + calculateId).html(errorMessage).show();
    } else {
        var url = '/manager/close-client-debt';

        var checkedValue = document.getElementsByClassName('class' + calculateId);
        var arrayID = [];
        for(var i = 0 ; i < checkedValue.length ; i++) {
            if(checkedValue[i].checked){
                arrayID.push(checkedValue[i].value);
            }
        }

        var  formData = {
            clientsId : arrayID,
            calculateId : calculateId,
            debtorName : $('#debtorName' + calculateId).val(),
            paidAmount : $('#paidAmount' + calculateId).val()
        };
        $.ajax({
            type: 'POST',
            url: url,
            data: formData,
            success: function (data) {
                var successMessage = '<h4 style="color:green;" align="center">' + data + '</h4>';
                $('.messageAd').html(successMessage).show();
                window.setTimeout(function () {
                    location.reload()
                }, 1000);
            },
            error: function (error) {
                var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
                $('.messageAd').html(errorMessage).show();

            }
        });
    }
}

function isBlank(str) {
    return str.length === 0 || str.trim() === ""
}

function removeDebtBoss(id) {
    var url = '/manager/tableDebt/deleteBoss';

    var request = $.post(url, {debtId: id}, function () {
        location.reload();
    });
}

function setClientTimePause(clientId) {
    $.ajax({
        type: "POST",
        url: "/manager/pause",
        data: {
            clientId: clientId
        },

        success: function (data) {
            location.reload();
        }
    });
}

function setClientTimeUnpause(clientId) {

    $.ajax({
        type: "POST",
        url: "/manager/unpause",
        data: {
            clientId: clientId
        },

        success: function (data) {
            location.reload();
        }
    });
}

function closeClient(calculateId) {

    var checkedValue = document.getElementsByClassName('class' + calculateId);
    var arrayID = [];
    for(var i = 0 ; i < checkedValue.length ; i++) {
        if(checkedValue[i].checked){
            arrayID.push(checkedValue[i].value);
        }
    }
    var  formData = {
        clientsId : arrayID,
        calculateId : calculateId
    };

    $.ajax({
        type: "POST",
        url: "/manager/close-client",
        data: formData,

        success: function (data) {
            location.reload();
        },
        error: function (error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('.errorMessage').html(errorMessage).show();
        }
    });
}