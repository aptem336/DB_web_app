var data_rows = document.querySelectorAll('.data_row');
var delete_buttons = document.querySelectorAll('.delete');
var reset = document.getElementById('reset');
var apply = document.getElementById('apply');
[].forEach.call(data_rows, function (data_row) {
    data_row.ondblclick = function () {
        turn_on(data_row);
        reset.removeAttribute('hidden');
        apply.removeAttribute('hidden');
    };
});
[].forEach.call(delete_buttons, function (delete_button, i) {
    delete_button.onclick = function () {
        data_rows[i].querySelector('[name="index"]').removeAttribute('disabled');
    };
});
function turn_on(data_row) {
    [].forEach.call(delete_buttons, function (delete_button) {
        delete_button.setAttribute('disabled', '');
    });
    [].forEach.call(data_row.querySelectorAll('input, select'), function (input) {
        input.removeAttribute('disabled');
    });
}
