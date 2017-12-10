$(document).on('click', '#saveNewProductData', function () {
    var id = $(this).data('id');
    $('.messageAdd' + id).html(errorMessage).hide();

    var setName = $("#addName" + id).val();
    var setDes = $("#addDes" + id).val();
    var setCost = $("#addCost" + id).val();
    var setSelfCost = $("#addSelfCost" + id).val();

    if (!productDataValidating(setName, setCost, setSelfCost) || !validateStaffPercent(id)) {
        var errorMessage = '<h4 style="color:red;" align="center">Неверный формат данных!</h4>';
        if(setName == ''){
            errorMessage = '<h5 style="color:red;" align="center">Поле название обязательно</h5>';
        }
        if(!(setCost ^ 0 === setCost) || !(setSelfCost ^ 0 === setSelfCost)){
            errorMessage += '<h5 style="color:red;" align="center">Себестоимость и Цена должны быть больше либо равны 0 (не используйте запятые)</h5>';
        }
        $('.messageAdd' + id).html(errorMessage).show();
        return false;
    }

    var ingredient = [];
    var amount = [];
    var myMap = new Object();
    var i;

    var needAddSelfCost = true;
    for (i = 0; i < ingredient.length; i++) {
        if (amount[i] > 0 && needAddSelfCost) {
            needAddSelfCost = false;
        }
        myMap[ingredient[i]] = amount[i];
    }
    if (!needAddSelfCost) {
        setSelfCost = 0;
    }

    var staffPercentObj = getStaffPercentObj(id);

    var ingredientsAndAmounts = getIngredientsAndAmountsObj(id);

    var wrapper = {
        idCat: id,
        name: setName,
        description: setDes,
        cost: setCost,
        names: ingredientsAndAmounts.ingredient,
        amount: ingredientsAndAmounts.amount,
        selfCost: setSelfCost,
        staffPercentPosition: staffPercentObj.position,
        staffPercentPercent: staffPercentObj.percent
    };

    $.ajax({
        type: "POST",
        url: "/boss/menu/addProduct",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        data: JSON.stringify(wrapper),
        success: function (result) {
            location.reload();
        },
        error: function (e) {
            console.log(e.responseText);
            console.log(e.message);

            if (e.responseText.substr(0, 13) === 'Couldn\'t find') {
                var errorMessage = '<h4 style="color:red;" align="center">Не найден ингредиент для рецепта</h4>';
                $('.messageAdd' + id).html(errorMessage).show();
            } else if (e.responseText.slice(0, 23) === 'Negative or zero amount') {
                var errorMessage = '<h4 style="color:red;" align="center">Отрицательное или нулевое количество ингредиента в рецепте</h4>';
                $('.messageAdd' + id).html(errorMessage).show();
            } else if (e.responseText.substr(0, 19) === 'There are no enough') {
                var errorMessage = '<h4 style="color:red;" align="center">Не хватает ингредиентов для создания рецепта</h4>';
                $('.messageAdd' + id).html(errorMessage).show();
            } else if (e.responseText === 'Null input in createRecipe') {
                var errorMessage = '<h4 style="color:red;" align="center">ошибка при создании рецепта: ' + e.responseText + '</h4>';
                $('.messageAdd' + id).html(errorMessage).show();
            } else if (e.responseText === 'Null input in getRecipeCost') {
                var errorMessage = '<h4 style="color:red;" align="center">ошибка при расчете стоимости рецепта: ' + e.responseText + '</h4>';
                $('.messageAdd' + id).html(errorMessage).show();
            } else if (e.responseText === 'selfCost field have to be greater or equal to zero') {
                var errorMessage = '<h4 style="color:red;" align="center">Себестоимость должна быть больше либо равна 0</h4>';
                $('.messageAdd' + id).html(errorMessage).show();
            } else if (e.responseText === 'Длина названия должна быть от 1 до 30 символов') {
                var errorMessage = '<h4 style="color:red;" align="center">Длина названия должна быть от 1 до 30 символов</h4>';
                $('.messageAdd' + id).html(errorMessage).show();
            } else {
                var errorMessage = '<h4 style="color:red;" align="center">Не удалось добавить продукт!</h4>';
                $('.messageAdd' + id).html(errorMessage).show();
            }
        }
    });
});

function closeModal() {
    var modal = $('.dima_modal');
    modal.fadeOut();
}


function del(id) {
    var formData = {
        del: id,
    }
    $.ajax({
        type: "POST",
        url: "/boss/menu/deleteProduct",
        data: formData,
        success: function (result) {
            $("#tr" + id).remove();
            $("#allTR" + id).remove();
        },
        error: function (e) {
        }
    });
}

function del2(id) {
    var formData = {
        del: id,
    }
    $.ajax({
        type: "POST",
        url: "/boss/menu/deleteProduct",
        data: formData,
        success: function (result) {
            $(document).find("#tr" + id).remove();
            $(document).find("#allTR" + id).remove();
        },
        error: function (e) {
        }
    });
}

function productDataValidating(name, cost, selfCost) {
    return (name && cost && selfCost && cost >= 0 && selfCost >= 0);
}

$(document).on('click', '#saveEditProductData', function () {
    var id = $(this).data("id");
    $('.messageEdit' + id).html(errorMessage).hide();

    var prodId = $("#updId" + id).val();
    var name = $("#updName" + id).val();
    var des = $("#updDes" + id).val();
    var cost = $("#updCost" + id).val();
    var selfCost = $("#updSelfCost" + id).val();

    if (!productDataValidating(name, cost, selfCost)) {
        var errorMessage = '<h4 style="color:red;" align="center">Неверный формат данных!' +
            '<h5 style="color:red;" align="center">Поле название обязательно</h5>'+
            '<h5 style="color:red;" align="center">Себестоимость и Цена должны быть больше либо равны 0</h5>';
        $('.messageEdit' + id).html(errorMessage).show();
        return false;
    }

    var formData = {
        id: prodId,
        name: name,
        description: des,
        cost: cost,
        selfCost: selfCost
    };

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/boss/menu/updProd",
        data: JSON.stringify(formData),
        dataType: 'json',
        success: function (result) {
            $("#b" + id).html(result.name);
            $("#c" + id).html(result.description);
            $("#d" + id).html(result.cost);
            $("#e" + id).html(result.selfCost);
            $("#allB" + id).html(result.name);
            $("#allC" + id).html(result.description);
            $("#allD" + id).html(result.cost);
            $("#allE" + id).html(result.selfCost);
            $('#allName' + id).val(result.name);
            $('#allDes' + id).val(result.description);
            $('#allCost' + id).val(result.cost);
            $('#selfCost' + id).val(result.cost);

            $("#p" + id).modal('hide');
        },
        error: function (e) {
            var errorMessage = '<h4 style="color:red;" align="center">Не удалось изменить данные!' +
                '<h5 style="color:red;" align="center">Поле название обязательно</h5>'+
                '<h5 style="color:red;" align="center">Себестоимость и Цена должны быть больше либо равны 0</h5>';
            $('.messageEdit' + id).html(errorMessage).show();
        }
    });
});

$(document).ready(function () {

    (function ($) {

        $('#filter').keyup(function () {

            var rex = new RegExp($(this).val(), 'i');
            $('.searchable tr').hide();
            $('.searchable tr').filter(function () {
                return rex.test($(this).text());
            }).show();
        })
    }(jQuery));
});

$(document).ready(function () {
    $("#myTab a").click(function (e) {
        e.preventDefault();
        $(this).tab('show');
    });
});

$('.super').click(function () {
    var pageName = $(this).data('page');
    // ajax-запрос
    $.ajax({
        url: '/boss/menu/upd',
        cache: false,
        success: function (html) {

            $("#bossview").html(html);
        }
    });
});

function showModal(id) {
    var modal = $('.dima_modal');
    modal.find("#d_id").val(id);
    $.ajax({
        type: "POST",
        url: "/boss/menu/getProduct",
        data: ({
            id: id
        }),
        success: function (product) {

            modal.find("#d_name").val(product.name);
            modal.find("#d_des").val(product.description);
            modal.find("#d_cost").val(product.cost);
            modal.find("#d_selfCost").val(product.selfCost);

        },
        error: function (e) {
            alert("Ошибка ;( ");
            console.log("ERROR: ", e);
        }
    });

    modal.fadeIn();
}

$(document).on('click', '#saveEditProductDataAll', function () {
    var id = $(this).data("id");
    $('.messageEditAll' + id).html(errorMessage).hide();

    var prodId = $("#allId" + id).val();
    var name = $("#allName" + id).val();
    var des = $("#allDes" + id).val();
    var cost = $("#allCost" + id).val();
    var selfCost = $("#selfCost" + id).val();

    if (!productDataValidating(name, cost, selfCost)) {
        var errorMessage = '<h4 style="color:red;" align="center">Неверный формат данных!11 &#013;&#010; fd</h4>';
        $('.messageEditAll' + id).html(errorMessage).show();
        return false;
    }

    var formData = {
        id: prodId,
        name: name,
        description: des,
        cost: cost,
        selfCost: selfCost
    };

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/boss/menu/updProd",
        data: JSON.stringify(formData),
        dataType: 'json',
        success: function (result) {
            $("#b" + id).html(result.name);
            $("#c" + id).html(result.description);
            $("#d" + id).html(result.cost);
            $("#e" + id).html(result.selfCost);
            $('#allB' + id).html(result.name);
            $("#allC" + id).html(result.description);
            $("#allD" + id).html(result.cost);
            $("#allE" + id).html(result.selfCost);
            $('#updName' + id).val(result.name);
            $('#updDes' + id).val(result.description);
            $('#updCost' + id).val(result.cost);

            $("#allP" + id).modal('hide');
        },
        error: function (e) {
            var errorMessage = '<h4 style="color:red;" align="center">Не удалось изменить данные!</h4>';
            $('.messageEditAll' + id).html(errorMessage).show();
        }
    });
});

function closeModal() {
    var modal = $('.dima_modal');
    modal.fadeOut();
}

$(document).ready(function () {
    (function ($) {
        $('#filter').keyup(function () {

            var rex = new RegExp($(this).val(), 'i');
            $('.searchable tr').hide();
            $('.searchable tr').filter(function () {
                return rex.test($(this).text());
            }).show();
        })
    }(jQuery));
});

$(document).ready(function () {
    $("#myTab a").click(function (e) {
        e.preventDefault();
        $(this).tab('show');
    });
});
$('.super').click(function () {
    var pageName = $(this).data('page');
    // ajax-запрос
    $.ajax({
        url: '/boss/menu/upd',
        cache: false,
        success: function (html) {
            $("#bossview").html(html);
        }
    });
});

function sendLogLevel() {
    var levelMap = {level: $('#chooseLogLevel').val()};
    if (isLogLevel($('#chooseLogLevel').val())) {
        $.ajax({
            type: "POST",
            url: "/boss/property/logLevel",
            data: levelMap,
            success: function (result) {
                $('.form-group').html('Уровень логирования задан.');
            },
            error: function (e) {
                var json = '<h4 style="color:aqua">Действующий пароль введён неверно</h4>';
            }
        });

    } else {
        var json = '<h4 style="color:red">Неизвестный уровень логгирования</h4>';
        $('.modal-title').html(json);
    }
}

function isLogLevel(log) {
    return log == 'INFO' || log == 'ERROR'
        || log == 'DEBUG' || log == 'WARN'
}

$(document).ready(function () {
    $('#formMailSetting').on('submit', function (e) {
        e.preventDefault();
        var email = $("#settingsEmail").val();
        if (!validateGmail(email)) {
            return;
        }
        var formData = {
            settingsName: $("#settingsName").val(),
            password: $("#settingsPassword").val(),
            email: email
        };
        $.ajax({
            type: "POST",
            url: "/boss/settings/mail-setting/add",
            data: formData,
            success: function (result) {
                location.reload();
            },
            error: function (e) {
                $("#errorModal").modal('show')
            }
        });
    });
});

function validateGmail(gmail) { //Validates the email address
    var emailRegex = /^[\w.+\-]+@gmail\.com$/;
    return emailRegex.test(gmail);
}

function applySMTPSettings(id) {
    var formData = {
        settingsId: id,
    }
    $.ajax({
        type: "POST",
        url: "/boss/settings/mail-setting/change-settings",
        data: formData,
        success: function (result) {
            var successMessage = '<h4 style="color:green;" align="center">' + result + '</h4>';
            $('.messageAd').html(successMessage).show();
            window.setTimeout(function () {
                location.reload()
            }, 1000);
        },
        error: function (e) {
            var errorMessage = '<h4 style="color:red;" align="center">' + e.responseText + '</h4>';
            $('.messageAd').html(errorMessage).show();
        }
    });
}

function removeSettings(id) {
    var url = '/boss/settings/mail-setting/del-settings';

    var request = $.post(url, {settingsId: id},
        window.setTimeout(function () {
            location.reload()
        }, 100)
    )
}


function test(id) {
    var ingredient = [];
    var amount = [];
    var map = new Object();
    var i = 0;
    var table_parent = $(document).find('#recipeBlock' + id);
    table_parent.find('.mySelect option:selected').each(function () {
        ingredient.push($(this).val());
    });
    table_parent.find('.inputClassTest').each(function () {
        amount.push($(this).val());
    });
    for (i = 0; i < ingredient.length; i++) {
        map[ingredient[i]] = amount[i];
    }
};

function calculateCostPrice(categoryId) {
    var totalCostPrice = 0;
    var ingredients = $('#recipeBlock' + categoryId).find('.ingredients');
    for (var i = 0; i < ingredients.length; i++) {
        var countIngredient = 0;
        var priceIngredient = 0.0;
        $(ingredients[i]).find('input,select').each(function (index, element) {
            elType = element.nodeName.toLowerCase();
            if (elType === 'input') {
                countIngredient = parseInt(element.value);
            } else if (elType === 'select') {
                priceIngredient = parseFloat($(element).find(':selected').attr('data-price'));
            }
        });
        totalCostPrice += countIngredient * priceIngredient;
    }
    $('#addSelfCost' + categoryId).val(totalCostPrice.toFixed(1));
}

function getEditHtmlOnAddProduct(hrefPrefix, product, isFloatingPrice) {
    var divPrefix = hrefPrefix == 'p' ? '' : 'All';
    var inputPrefix = hrefPrefix == 'p' ? 'upd' : 'all';
    var selfCostId = (hrefPrefix == 'p' ? 'updSelfCost' : 'selfCost') + product.productId;
    var modalDivId = (hrefPrefix == 'p' ? 'p' : 'allP') + product.productId;

    return '<div  align="center" class="modal fade"id="' + modalDivId + '"tabindex="-1"' +
        'roles="dialog" aria-labelledby="myModalLabel">' +
        ' <div class="modal-dialog" roles="document">' +
        ' <div class="modal-content">' +
        ' <div class="modal-header">' +
        ' <button type="button" class="close" data-dismiss="modal"aria-label="Close"><span' +
        ' aria-hidden="true">&times;</span></button>' +
        ' <h4 class="modal-title" id="myModalLabel">Редактирование </h4>' +
        ' </div>' +
        ' <div class="modal-body">' +
        ' <div class="messageEdit' + divPrefix + product.productId + '"></div>' +
        ' <div class="form-group">' +
        ' <input required="" type="hidden" class="form-control" id="' + inputPrefix + 'Id' + product.productId + '"' +
        ' value="' + product.productId + '"/>' +
        ' <label>Название</label>' +
        ' <input type="text" class="form-control"' +
        ' id="' + inputPrefix + 'Name' + product.productId + '" name="name" value="' + product.name + '"/> </div>' +
        ' <div class="form-group">' +
        ' <label>Описание</label>' +
        ' <input type="text" class="form-control" id="' + inputPrefix + 'Des' + product.productId + '"' +
        ' name="description" value="' + product.description + '"/> </div>' +
        ' <div class="form-group">' +
        ' <label>Цена</label>' +
        ' <input type="text" class="form-control"' +
        ' id="' + inputPrefix + 'Cost' + product.productId + '" name="cost" required="" pattern="\d+" title="только цифры" ' +
        ' value="' + product.cost + '"' + (isFloatingPrice === true ? 'disabled="disabled"' : '') + '"/>' +
        ' </div>' +
        ' <div class="form-group">' +
        ' <label>Себестоимость</label>' +
        ' <input pattern="\d+" type="text" class="form-control" id="' + selfCostId + '" name="selfCost"' +
        ' title="только цифры" value="' + product.selfCost + '"/>' +
        '</div> </div>' +
        '<button id="saveEditProductData' + divPrefix + '" type="button" name="upd"' +
        'class="btn btn-lg btn-primary btn-block" data-id="' + product.productId + '">Сохранить </button>' +
        '<div class="modal-footer">' +
        '<button id="close" type="button" name="upd" class="btn btn-default" data-dismiss="modal"> Отмена </button>' +
        '</div> </div> </div> </div>';
}

function addPositionPercent(categoryId) {
    var items = $('#positionPercentContainer' + categoryId + ' > .item');
    var positions = $(items.find('select')[0]).children('option');

    if (positions.length == items.length) {//невозможно добавить больше,чем должностей
        return;
    }
    $(items[0]).clone().appendTo('#positionPercentContainer' + categoryId);
}


function delPositionPercent(item, categoryId) {
    var items = $('#positionPercentContainer' + categoryId + ' > .item');
    if (items.length > 1) {
        $(item).parents('.item').remove();
    }
}

function validateStaffPercent(categoryId) {
    var items = $('#positionPercentContainer' + categoryId + ' > .item');
    var totalPercents = 0;
    var selectedPositions = [];
    var dublicatePosition = false;
    items.find('input,select').each(function (index, element) {
        elType = element.nodeName.toLowerCase();
        if (elType === 'input') {
            totalPercents += parseFloat(element.value);
        } else if (elType === 'select') {
            positionId = $(element).find(':selected').val();
            if (selectedPositions.indexOf(positionId) >= 0) {
                dublicatePosition = true;
            } else {
                selectedPositions.push(positionId);
            }
        }
    });

    return !dublicatePosition && totalPercents <= 100;
}

function getStaffPercentObj(categoryId) {
    var obj = {"position": [], "percent": []};
    var percents = $('#positionPercentContainer' + categoryId + ' > .item').find('input');
    var positions = $('#positionPercentContainer' + categoryId + ' > .item').find('select');
    for (var i = 0; i < percents.length; i++) {
        percent = parseFloat($(percents[i]).val());
        if (percent > 0) {
            obj['percent'].push(percent);
            obj['position'].push($(positions[i]).val());
        }
    }
    return obj;
}

function getIngredientsAndAmountsObj(categoryId) {
    var obj = {"ingredient": [], "amount": []};
    var amounts = $('#recipeBlock' + categoryId + ' > .ingredients').find('input');
    var ingredients = $('#recipeBlock' + categoryId + ' > .ingredients').find('select');
    for (var i = 0; i < amounts.length; i++) {
        amount = parseFloat($(amounts[i]).val());
        if (amount > 0) {
            obj['amount'].push(amount);
            obj['ingredient'].push($(ingredients[i]).val());
        }
    }
    return obj;
}

function editStaffPercent(id) {
    if (!validateStaffPercent('')) {
        var errorMessage = '<h4 style="color:red;" align="center">Неверный формат данных!</h4>';
        $('.errorMessage').html(errorMessage).show();
        return;
    }

    var staffPercentObj = getStaffPercentObj('');
    var wrapper = {
        idCat: id,
        staffPercentPosition: staffPercentObj.position,
        staffPercentPercent: staffPercentObj.percent
    };
    $.ajax({
        type: "POST",
        url: "/boss/menu/edit/staffPercent",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        data: JSON.stringify(wrapper),
        success: function () {
            location.reload();

        },
        error: function (e) {
            alert("Неверный формат данных -/");
        }
    });
    $('.errorMessage').html(errorMessage).hide();
}

function addIngredient(id) {
    var ingredientsRow = $('#recipeBlock' + id + ' > .ingredients');
    $(ingredientsRow[0]).clone().appendTo('#recipeBlock' + id);
    calculateCostPrice(id);
}

function deleteIngredient(item, id) {
    var items = $('#recipeBlock' + id + ' > .ingredients');
    var inputs = [];
    items.find('.inputClassTest').each(function () {
        inputs.push($(this).val());
    });
    if (items.length > 1) {
        $(item).parents('.ingredients').remove();
    }
    calculateCostPrice(id);
}
