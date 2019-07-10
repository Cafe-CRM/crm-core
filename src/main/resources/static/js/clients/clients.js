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

function ajaxTest() {
    $.ajax({
        type: "POST",
        url: "/manager/calculate-price-test",
        success: function (data) {
            console.log('succes test')
        },
        error: function () {
            console.log('ajax сломался? ');
        }
    });
}

function ajaxTestEmpty() {
    $.ajax({
        type: "POST",
        url: "/manager/calculate-price-test-empty",
        success: function (data) {
            console.log('succes test empty');
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
            var menuId = "pr" + data.id;
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
        error: function (error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('#calcDescError').modal('show');
            $('#errorMessage').html(errorMessage);
        }
    });
}

var allProdArr = [];
var testArr = [];

function createLayerProductWithFloatingPriceAjax(prodId, calcId, inputId) {
    $('#productId' + calcId).val(prodId);
    var price = $(('#' + inputId) + prodId + calcId).val();
    if (price === "" || price <= 0) {
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
            var menuId = "pr" + data.id;
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
        error: function (error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('#calcDescError').modal('show');
            $('#errorMessage').html(errorMessage);
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
            var count = getCheckBoxCount(calcId, layerProdId);
            for (var j = 0; j < count; j++) {
                var menuId = "pr" + data.id;
                var ai = testArr.indexOf(menuId);
                testArr.splice(ai, 1);
            }

            for (var i = 0; i < arr.length; i++) {
                if (document.getElementById('ajaxMenu' + arr[i].id).querySelector("#pr" + data.id) == null) {
                    $('#ajaxMenu' + arr[i].id + ' li:last').after('<li id = "pr' + data.id + '">' + data.name + ' №' + data.id + ' (' + data.cost + 'р)' + '</li>');
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
            var arr = data;
            var menuId = "pr" + layerProdId;
            var count = getDeletedCheckBoxCount(calcId);
            for (var j = 0; j < count; j++) {
                testArr.push(menuId);
            }
            for (var i = 0; i < arr.length; i++) {
                var v = document.getElementById('ajaxMenu' + arr[i].id).querySelector("#pr" + layerProdId);
                if (v !== null) {
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
            if (data.length !== 0) {
                allProdArr = [];
                testArr = [];
                for (var j = 0; j < data.length; j++) {
                    var menuId1 = "pr" + data[j].id;
                    allProdArr.push(menuId1);
                }
                checkBoxIterate(calcId);
                for (var i = 0; i < data.length; i++) {
                    var menuId = "pr" + data[i].id;
                    var trId = "id=" +"\"" + "forpr" + data[i].id +"\"";
                    if (testArr.indexOf(menuId) === -1) {
                        str += '<tr ' + trId + ' style="font-size: 20px;"><td>' + data[i].name + ' №' + data[i].id + '</td>' +
                            '<td>' + data[i].description + '</td><td>' + data[i].cost + '</td>' +
                            '<td class="buttonTd"><button class="share-order order-button inactive" disabled="disabled" onclick="addLayerProductOnClientAjax(' + data[i].id + ',' + calcId + ')"> <i class="fa fa-check" aria-hidden="true"></i></button>' +
                            '<button class="delete-order order-button" onclick="deleteLayerProductOnClientAjax(' + data[i].id + ',' + calcId + ')"><i class="fa fa-times" aria-hidden="true"></i></button></td></tr>';
                    } else {
                        str += '<tr ' + trId + ' style="font-size: 20px;"><td>' + data[i].name + ' №' + data[i].id + '</td>' +
                            '<td>' + data[i].description + '</td><td>' + data[i].cost + '</td>' +
                            '<td class="buttonTd"><button class="share-order order-button" onclick="addLayerProductOnClientAjax(' + data[i].id + ',' + calcId + ')"> <i class="fa fa-check" aria-hidden="true"></i></button>' +
                            '<button class="delete-order order-button" onclick="deleteLayerProductOnClientAjax(' + data[i].id + ',' + calcId + ')"><i class="fa fa-times" aria-hidden="true"></i></button></td></tr>';
                    }
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

function getDeletedCheckBoxCount(calcId) {
    var box = "div#boxWrapper" + calcId + " div div input";
    var count = 0;
    $(box).each(function (index, element) {
        if ($(element).prop("checked")) {
            count++
        }
    });
    return count;
}

function getCheckBoxCount(calcId, layerProdId) {
    var box = "div#boxWrapper" + calcId + " div div input";
    var count = 0;
    var externalArr = [];
    $(box).each(function (index, element) {
        if ($(element).prop("checked")) {
            var clientMenuId = $(element).attr('value');
            var productId = "pr" + layerProdId;
            //arr
            var str = "div#ajaxMenu" + clientMenuId + " div li";
            var sstr = "div#ajaxMenu" + clientMenuId + " li";
            var ownProducts = []; //pr00
            if (typeof $(str).html() !== "undefined") {
                $(str).each(function (index, element) {
                    ownProducts.push($(element).attr('id'));
                });
            } else if (typeof $(sstr).html() !== "undefined") {
                $(sstr).each(function (index, element) {
                    ownProducts.push($(element).attr('id'));
                });
            }
            if  (ownProducts.indexOf(productId) === -1) {
                count++
            }
            $(ownProducts).each(function (index, element) {
                if (element !== "") {
                    externalArr.push(element);
                }
            });

        }
    });
    return count;
}

function checkBoxIterate(calcId) {
    var box = "div#boxWrapper" + calcId + " div div input";
    $(box).each(function (index, element) {
        if ($(element).prop("checked")) {
            var clientMenuId = $(element).attr('value');
            var str = "div#ajaxMenu" + clientMenuId + " div li";
            var sstr = "div#ajaxMenu" + clientMenuId + " li";
            if (typeof $(str).html() !== "undefined") {

                var isOpening = $("#checkMenu" + clientMenuId).prop("checked");

                var cleanArr = allProdArr.filter(function (item) {
                    var isRepeated = true;
                    $(str).each(function (index, element) {
                        if (item === $(element).attr('id')) {
                            isRepeated = false;
                        }
                    });
                    return isRepeated;
                });
                if (isOpening) {
                    $(cleanArr).each(function (index, element) {
                        var id = "#for" + element;
                        var shareBt = "tr" + id + " td.buttonTd button.share-order";
                        $(shareBt).removeAttr("disabled").removeClass("inactive");
                        if (testArr.indexOf(element) === -1) {
                            testArr.push(element);
                        }
                    });
                }


            } else if (typeof $(sstr).html() !== "undefined") {

                var isOpening1 = $("#checkMenu" + clientMenuId).prop("checked");

                var cleanArr1 = allProdArr.filter(function (item) {
                    var isRepeated = true;
                    $(sstr).each(function (index, element) {
                        if (item === $(element).attr('id')) {
                            isRepeated = false;
                        }
                    });
                    return isRepeated;
                });
                if (isOpening1) {
                    $(cleanArr1).each(function (index, element) {
                        var id = "#for" + element;
                        var shareBt = "tr" + id + " td.buttonTd button.share-order";
                        $(shareBt).removeAttr("disabled").removeClass("inactive");
                        //alert("push element!");
                        if (testArr.indexOf(element) === -1) {
                            testArr.push(element);
                        }
                    });
                }


            }
        }
    });
}


function openCloseButtons(clientMenuId) {
    var str = "div#ajaxMenu" + clientMenuId + " div li";
    var sstr = "div#ajaxMenu" + clientMenuId + " li";
    if (typeof $(str).html() !== "undefined"){
        addButtonIterate(str, clientMenuId);
    } else if (typeof $(sstr).html() !== "undefined") {
        addButtonIterate(sstr, clientMenuId);
    }
}

function addButtonIterate(arr, clientMenuId) {
    var isOpening = $("#checkMenu" + clientMenuId).prop("checked");

    var cleanArr = allProdArr.filter(function (item) {
        var isRepeated = true;
        $(arr).each(function (index, element) {
            if (item === $(element).attr('id')) {
                isRepeated = false;
            }
        });
        return isRepeated;
    });
    if (isOpening) {
        $(cleanArr).each(function (index, element) {
            var id = "#for" + element;
            var shareBt = "tr" + id + " td.buttonTd button.share-order";
            $(shareBt).removeAttr("disabled").removeClass("inactive");
            testArr.push(element);
        });
    } else {
        $(cleanArr).each(function (index, element) {
            var id = "#for" + element;
            var shareBt = "tr" + id + " td.buttonTd button.share-order";
            if (getCount(element, testArr) === 1) {
                $(shareBt).attr("disabled", "disabled").addClass("inactive");
                testArr.splice(testArr.indexOf(element), 1);
            } else if (getCount(element, testArr) > 1) {
                testArr.splice(testArr.indexOf(element), 1);
            }
        });
    }
}

function getCount(ownID, arr) {
    var count = 0;
    for (var i = 0; i < arr.length; i++) {
        if (arr[i] === ownID) {
            count++
        }
    }
    return count;
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
    var inputTest = localStorage.getItem('objectToPass');
    if(inputTest == 'true') {
        var appBanners = document.getElementsByClassName('close-without-recepient'), i;
        for (var i = 0; i < appBanners.length; i ++) {
            appBanners[i].style.display = 'none';
        }
    }
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

    var inputTest = localStorage.getItem('objectToPass');
    if(inputTest == 'true') {
        var appBanners = document.getElementsByClassName('close-without-recepient'), i;
        for (var i = 0; i < appBanners.length; i ++) {
            appBanners[i].style.display = 'none';
        }
    }



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
            type: "GET",
            url: "/boss/settings/close-client/get-setting-client",
            success: function (data) {
                if (data.enabled === true) {
                    $.ajax({
                        type: "POST",
                        url: "/manager/precheck",
                        data: {
                            clientsId : arrayID
                        },

                        success: function (data) {
                            $.ajax({
                                type: 'POST',
                                url: 'http://localhost:8081/',
                                data: {
                                    preCheckForTime : data[0],
                                    totalAmount : data[1],
                                    preCheckForFood : data[2]
                                },
                                success: function () {

                                    $.ajax({
                                        type: 'POST',
                                        url: url,
                                        data: formData,
                                        success: function (data) {
                                            var successMessage = '<h4 style="color:green;" align="center">Клиенты расчитаны!</h4>';
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
                            });
                        },
                        error: function (error) {
                            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
                            $('.newAmountError').html(errorMessage).show();
                        }
                    });
                }
                else if (data.enabled === false) {
                    $.ajax({
                        type: 'POST',
                        url: url,
                        data: formData,
                        success: function (data) {
                            var successMessage = '<h4 style="color:green;" align="center">Клиенты расчитаны!</h4>';
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
        })

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

/*$(".changeSumButton").click(function () {
    sendToken();
});

function sendToken() {
    $.ajax({
        type: "POST",
        url: "/manager/send-modify-amount-pass",

        error: function (error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('.newAmountError').html(errorMessage).show();
        }
    });
}*/

function sendRecalculateToken(calcId) {
    var checkedValue = document.getElementsByClassName('class' + calcId);
    var arrayID = [];
    for(var i = 0 ; i < checkedValue.length ; i++) {
        if(checkedValue[i].checked){
            arrayID.push(checkedValue[i].value);
        }
    }

    $.ajax({
        type: "POST",
        url: "/manager/send-modify-amount-pass",
        data: {calcId : calcId, newAmount : $('#newAmount' + calcId).val(), clientsId : arrayID},

        success: function (data) {
            $('#newAmount' + calcId).prop('disabled',true);
            $('#sendCode' + calcId).html('Отправить код повторно');
            $('#promptText' + calcId).html('В админ-конференцию был послан короткий код. \n Используйте его для подтверждения действия.');
            $('#confirmPassword' + calcId).removeClass('hidden');
            $('.confirmButton').removeClass('hidden');
        },
        error: function (error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('.newAmountError').html(errorMessage).show();
        }
    });
}

function sendDeleteClientToken(calcId) {

    var checkedValue = document.getElementsByClassName('class' + calcId);
    var arrayID = [];
    for(var i = 0 ; i < checkedValue.length ; i++) {
        if(checkedValue[i].checked){
            arrayID.push(checkedValue[i].value);
        }
    }

    $.ajax({
        type: "POST",
        url: "/manager/send-delete-client-pass",
        data: {clientsId: arrayID, calcId: calcId},

        success: function (data) {
            $('#newAmount' + calcId).prop('disabled',true);
            $('#sendCode' + calcId).html('Отправить код повторно');
            $('#promptText' + calcId).html('В админ-конференцию был послан короткий код. \n Используйте его для подтверждения действия.');
            $('#confirmPassword' + calcId).removeClass('hidden');
            $('.confirmButton').removeClass('hidden');
        },
        error: function (error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('.newAmountError').html(errorMessage).show();
        }
    });

}


function sendDeleteClients(calcId) {

    $.ajax({
        url: "/manager/delete-clients",
        type: "POST",
        dataType: "html",
        data: $("#formTest" + calcId).serialize(),
        success: function(response) {
            location.reload();
        },
        error: function(error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('.deleteClientError').html(errorMessage).show();
        }
    });

}


function closeClientWithNewAmount(calculateId) {
    var checkedValue = document.getElementsByClassName('class' + calculateId);
    var arrayID = [];
    for(var i = 0 ; i < checkedValue.length ; i++) {
        if(checkedValue[i].checked){
            arrayID.push(checkedValue[i].value);
        }
    }
    var  formData = {
        newAmount : $('#newAmount' + calculateId).val(),
        password : $('#pass' + calculateId).val(),
        clientsId : arrayID,
        calculateId : calculateId
    };

    $.ajax({
        type: "GET",
        url: "/boss/settings/close-client/get-setting-client",
        success: function (data) {
            if (data.enabled === true) {
                $.ajax({
                    type: "POST",
                    url: "/manager/precheck-with-new-sum",
                    data: {
                        clientsId : arrayID,
                        newAmount : $('#newAmount' + calculateId).val()
                    },

                    success: function (data) {
                        //location.reload();
                        $.ajax({
                            type: 'POST',
                            url: 'http://localhost:8081/',
                            data: {
                                preCheckForTime: data[0],
                                totalAmount: data[1],
                                preCheckForFood: data[2]
                            },
                            success: function () {
                                $.ajax({
                                    type: "POST",
                                    url: "/manager/close-new-sum-client",
                                    data: formData,

                                    success: function (data) {
                                        location.reload();
                                    },
                                    error: function (error) {
                                        var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
                                        $('.newAmountError').html(errorMessage).show();
                                    }
                                });

                            },
                            error: function (error) {
                                var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
                                $('.newAmountError').html(errorMessage).show();
                            }
                        });
                    },
                    error: function (error) {
                        var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
                        $('.newAmountError').html(errorMessage).show();
                    }
                });
            }
            else if (data.enabled === false) {
                $.ajax({
                    type: "POST",
                    url: "/manager/close-new-sum-client",
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
        }
    })
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
        url: "/manager/precheck",
        data: { calculateId : calculateId
        },

        success: function (data) {
            //location.reload();
            formDataPrint = {
                preCheckForTime : data[0],
                totalAmount : data[1],
                preCheckForFood : data[2]
            }
            $.ajax({
                type: 'POST',
                url: '/http://localhost:8081/',
                data: formDataPrint,
                success : function (data) {
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
            });
        },
        error: function (error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('.newAmountError').html(errorMessage).show();
        }
    });

    // $.ajax({
    //     type: "POST",
    //     url: "/manager/close-client",
    //     data: formData,
    //
    //     success: function (data) {
    //         //location.reload();
    //         $.ajax({
    //             type: 'POST',
    //             url: 'http://localhost:8081/',
    //             data: {
    //                 preCheckForTime : data[0],
    //                 totalAmount : data[1],
    //                 preCheckForFood : data[2]
    //             }, success: function (data) {
    //                 location.reload()
    //             }
    //         });
    //     },
    //     error: function (error) {
    //         var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
    //         $('.errorMessage').html(errorMessage).show();
    //     }
    // });
}

function editDescription(calculateId, description) {
    var desc = $('#head1' + calculateId);
    var input = $('#editDescInput' + calculateId);

    if (desc.is(":visible")) {
        desc.hide();
        if (input.val() === "") {
            input.val(description);
        }
        input.show();
    } else if (input.val() === description) {
        input.val(description);
        input.hide();
        desc.show();
    } else {
        validateAndPost(calculateId, input.val());
    }
}

function validateAndPost(calculateId, description) {
    if (description === "") {
        $('#calcDescError').modal('show');
        $('#errorMessage').html('<h4 style="color:red;" align="center">Описание стола не может быть пустым!</h4>')
    } else {
        changeCalcDesc(calculateId, description);
    }
}

function successFunc(calculateId, description) {
    var desc = $('#head1' + calculateId);
    var button = $('#editDesc' + calculateId);
    var input = $('#editDescInput' + calculateId);
    var menuDesc = $('#menuDesc' + calculateId);
    desc.show();
    desc.text(input.val());
    menuDesc.html(input.val());
    //input.val(description);
    button.on
    input.hide();
    button.html("Изменить описание");
}

function changeButton(calculateId) {
    $('#editDesc' + calculateId).html("Применить");
}

function changeCalcDesc(calculateId, description) {


    var  formData = {
        calculateId : calculateId,
        description : description
    };

    $.ajax({
        type: "POST",
        url: "/manager/change-calculate-description",
        data: formData,

        success: function (data) {
            //successFunc(calculateId, description);
            location.reload();
        },
        error: function (error) {
            $('#calcDescError').modal('show');
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('#errorMessage').html(errorMessage);
        }
    });
}

function openNewCalc() {
    var boardId = $('#boardId').find('option:selected').val();
    var number = $('#number').val();
    var description = $('#description').val();

    if (description === "") {
        $('#calcModalErrorMessage').html('<h4 style="color:red;" align="center">Описание стола не может быть пустым!</h4>')
    } else if (boardId === "") {
        $('#calcModalErrorMessage').html('<h4 style="color:red;" align="center">Выберите стол!</h4>')
    } else {
        sendNewCalc(boardId, number, description);
    }
}

function sendNewCalc(boardId, number, description) {
    var  formData = {
        boardId : boardId,
        number : number,
        description: description
    };

    $.ajax({
        type: "POST",
        url: "/manager/add-calculate",
        data: formData,

        success: function (data) {
            location.reload();
        },
        error: function (error) {
            $('#calcDescError').modal('show');
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('#calcModalErrorMessage').html(errorMessage);
        }
    });
}
function preCheck(calculateId) {
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
        type: "GET",
        url: "/boss/settings/close-client/get-setting-client",
        success: function (data) {
            if (data.enabled === true) {
                $.ajax({
                    type: "POST",
                    url: "/manager/precheck",
                    data: {
                        clientsId : arrayID
                    },

                    success: function (data) {
                        //location.reload();
                        $.ajax({
                            type: 'POST',
                            url: 'http://localhost:8081/',
                            data: {
                                preCheckForTime: data[0],
                                totalAmount: data[1],
                                preCheckForFood: data[2]
                            },
                            success: function () {
                                $.ajax({
                                    type: "POST",
                                    url: "/manager/close-client",
                                    data: formData,

                                    success: function (data) {
                                        location.reload();
                                    },
                                    error: function (error) {
                                        var errorMessage = '<h4 style="color:red;" align="center">' + error.statusText + '</h4>';
                                        $('.errorMessage').html(errorMessage).show();
                                    }
                                });

                            },
                            error: function (error) {
                                var errorMessage = '<h4 style="color:red;" align="center">' + error.statusText + '</h4>';
                                $('.errorMessage').html(errorMessage).show();
                            }
                        });
                    },
                    error: function (error) {
                        var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
                        $('.errorMessage').html(errorMessage).show();
                    }
                });
            }
            else if (data.enabled === false) {
                $("#close-without-recepient").hide()
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
        }
    })

}

function closeClientWithouPrecheck(calculateId) {

    var checkedValue = document.getElementsByClassName('class' + calculateId);
    var arrayID = [];
    for(var i = 0 ; i < checkedValue.length ; i++) {
        if(checkedValue[i].checked){
            arrayID.push(checkedValue[i].value);
        }
    }
    var  formData = {
        password : $('#passwordClient' + calculateId).val(),
        clientsId : arrayID,
        calculateId : calculateId
    };

    $.ajax({
        type: "POST",
        url: "/manager/close-client-without-precheck",
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

function sendToken(calculateId) {
    $.ajax({
        type: "POST",
        url: "/manager/send-close-client-pass",
        data: {calculateId : calculateId},

        error: function (error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('.Error').html(errorMessage).show();
        }
    });
}
