var data_rows = document.querySelectorAll('.data_row');
var reset = document.getElementById('reset');
var apply = document.getElementById('apply');
[].forEach.call(data_rows, function (data_row) {
    data_row.ondblclick = function () {
        turn_on(data_row, true);
        reset.removeAttribute('hidden');
        apply.removeAttribute('hidden');
    };
});
function turn_on(data_row) {
    [].forEach.call(data_row.getElementsByTagName('input'), function (input) {
        input.removeAttribute('disabled');
        input.removeAttribute('title');
    });
}