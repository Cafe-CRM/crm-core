$(document).ready(function () {
    $('input[type="text"]').get(0).focus();
    $('#buttonAddBoard').on("click", function (e) {
        var data = $('#inputBoardName').serialize();
        var rowVal = $('#inputBoardName').val();
        $('#inputBoardName').val('');
        $('input[type="text"]').get(0).focus();
        var url = "/company/configuration/step/board/add";
        $.ajax({
            type: 'POST',
            url: url,
            data: data,
            success: function (board) {
                var dataId = board.id;
                $('#boardTable').find('> tbody')
                    .append('<tr>'+
                                '<td align="center">' + rowVal + '</td>' +
                                '<td align="center" >' +
                                    '<button type="button" class="btn btn-danger buttonDeleteBoard" data-id=' + board.id + '>Удалить</button></td>' +
                            '</tr>');
            },
            error: function (error) {
                e.preventDefault();
                var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
                $('.errorMessage').html(errorMessage).show();
            }

        });
    });
});

$(document).ready(function () {
    $('#boardTable').on("click", '.buttonDeleteBoard', function (e) {
        var boardId = $(this).data('id');
        var delRow =  $(this);
        var url = "/company/configuration/step/board/delete";
        $.ajax({
            type: 'POST',
            url: url,
            data: {boardId: boardId},
            success: function (board) {
                delRow.closest('tr').remove();
            },
            error: function (error) {
                e.preventDefault();
                var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
                $('.errorMessage').html(errorMessage).show();
            }

        });
    });
});

$(document).ready(function () {
    $('#buttonEndBoard').on("click", function (e) {
        var url = "/boss/company/configuration";
        $.ajax({
            type: 'GET',
            url: url,
            success: function (data) {
            },
            error: function (error) {
                e.preventDefault();
                var errorMessage = '<h4 style="color:red;" align="center">' + error.responseText + '</h4>';
                $('.errorMessage').html(errorMessage).show();
            }

        });
    });
});
