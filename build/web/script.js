var rows = document.querySelectorAll('#data_table tr:not(:first-child)');
var selected;
//Можно-ли сократить
rows.forEach(function (row, i) {
    row.onclick = function () {
        if (this === selected) {
            parent.document.getElementById('row_index').setAttribute('value', -1);
            this.classList.remove('selected');
            selected = undefined;
        } else {
            parent.document.getElementById('row_index').setAttribute('value', i);
            this.classList.add('selected');
            if (selected) {
                selected.classList.remove('selected');
            }
            selected = this;
        }
    };
});