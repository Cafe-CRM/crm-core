$(document).ready(function () {
    $('#sortTable').dataTable({
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
            {"targets": [4, 5, 6], "orderable": false}],
        "oLanguage": {
            "sEmptyTable": "Нет доступных данных для таблицы!"
        },
    });
});

function deleteIngredient(ingredientId) {
    $.ajax({
        type: "POST",
        url: "/boss/menu/ingredients/get-ingredient-products",
        data: {ingredientId: ingredientId},

        success: function (data) {
            if (data == "") {
                location.reload();
            } else {
                $('#deleteIngredientConfirmModal').modal('show');

                var prodIds = [];

                for (var i = 0; i < data.length; i++) {
                    var id = data[i].id;
                    var name = data[i].name;
                    prodIds.push(id);

                    $('#deleteIngredientTable').find('> tbody').append(
                        '<tr>' +
                        '<td align="center">' + name + '</td>' +
                        '</tr>'
                    );
                }

                $('#confirmDelete').attr("onclick", 'confirmDeleteIngredient(\"' + prodIds + '\")');
                $('#confirmDelete').attr("value", ingredientId);
            }
        },
        error: function (error) {
            //var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            //$('#salaryWorkerError').html(errorMessage).show();
        }
    });

}

function confirmDeleteIngredient(prodIds) {
    var ingredientId = $('#confirmDelete').attr('value');
    $.ajax({
        type: "POST",
        url: "/boss/menu/ingredients/delete-ingredient-from-products",
        data: {prodIds: prodIds, ingredientId: ingredientId},

        success: function (data) {
            if (data == "") {
                location.reload();
            } else {

            }
        },
        error: function (error) {
            //var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
            //$('#salaryWorkerError').html(errorMessage).show();
        }
    });

}