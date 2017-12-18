
var totalPrice = 0;

function insertTotalPrice(element) {
    document.getElementById(element).innerHTML += totalPrice;
    totalPrice = 0;
}

function setPrice(price) {
    totalPrice += price;
}
