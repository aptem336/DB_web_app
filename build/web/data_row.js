var data_rows = document.querySelectorAll('.data_row');
var reset = document.getElementById('reset');
var apply = document.getElementById('apply');
[].forEach.call(data_rows, function (data_row, i) {
    data_row.ondblclick = function () {
        turn_on(data_row);
        reset.removeAttribute('hidden');
        apply.removeAttribute('hidden');
    };
    if (i < data_rows.length - 1) {
        data_row.querySelector('[name="type"]').onclick = function () {
            data_row.querySelector('[name="index"]').removeAttribute('disabled');
        };
    }
});
function turn_on(data_row) {
    [].forEach.call(data_row.querySelectorAll('input, select'), function (input) {
        input.removeAttribute('disabled');
        input.removeAttribute('title');
    });
}
