var buttons = document.querySelectorAll('button');
var clicked;
[].forEach.call(buttons, function (button, i) {
    button.onclick = function () {
        if (clicked) {
            clicked.style.opacity = 1.0;
        }
        clicked = this;
        clicked.style.opacity = 0.75;
    };
});