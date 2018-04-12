$(document).ready(function () {
    $('#sortedTable').dataTable({
        // rowReorder: true,
        // "scrollY": "46vh",
        "scrollCollapse": false,
        "autoWidth": true,
        "lengthChange": false,
        "paging": false,
        "info": false,
        "searching": false,
        // "ordering": false
        "order": [[0, "asc"]],
        "columnDefs": [{"targets": [], "visible": false},
            {"targets": [2,3], "orderable": false}],
        "oLanguage": {
            "sEmptyTable": "Нет доступных данных для таблицы!"
        },
    });
});

function removeCategory(id) {
    var url = '/boss/cost/category/delete';
    var formData = {
    id: id
    };

    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        success: function (data) {
            location.reload();
        },
        error: function (error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            var modal = $('#errorModal');
            modal.find('.messageAd').text(error.responseText);
            modal.modal('show');
        }
    });
}

function categoryEditModal(rowIdx, isChecked) {
    var table = $('#sortedTable').DataTable();
    var numberOfRows = table.data().length;
    $('.messageAd').html("");
    for (var i = 0; i < numberOfRows; i++) {
        var data = table.row(i).data();
        var id = data[0];
        if (id == rowIdx) {
            var category = data[1];
            var isSalary = (isChecked == 'true');
            $('#editId').val(id);
            $('#editCategory').val(category);
            $('#isSalary').prop('checked', isSalary);
            return false;
        }
    }
}

$(document).ready(function () {
    $('#formEditCategory').submit(function (e) {
        e.preventDefault();
        $('.messageAd').html('').hide();
        if ($('#editCategory').val() == "") {
            var errorMessage = '<h4 style="color:red;" align="center">Не указана категория расхода</h4>';
            $('.messageAd').html(errorMessage).show();
            return false;
        }
        var url = '/boss/cost/category/edit';
        var formData = $('#formEditCategory').serialize();

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
    });
});

function sendSalaryCategoryFromList(categoryId) {
    var url = "/company/configuration/step/costCategory/check-as-salary";
    $.ajax({
        type: 'POST',
        url: url,
        data: {categoryId: categoryId, isSalary: true, isChangeable: true},
        success: function (category) {
            location.reload();

            /*$('#reconfiguredMessage').remove();

            var successMessage = '<h4 style="color:green;" align="center">Категория "' + category.name + '" отмечена как зарплатная.</h4>';
            $('.errorMessage').html(successMessage).show();*/
        },
        error: function (error) {
            var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            $('.errorMessage').html(errorMessage).show();
        }

    });
}

$(document).ready(function () {
    $('#formAddCategory').submit(function (e) {
        e.preventDefault();
        $('.messageAd').html('').hide();
        if ($('#addCategory').val() == "") {
            var errorMessage = '<h4 style="color:red;" align="center">Не указана категория расхода</h4>';
            $('.messageAd').html(errorMessage).show();
            return false;
        }
        var url = '/boss/cost/category/add';
        var formData = $('#formAddCategory').serialize();

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
    });
});