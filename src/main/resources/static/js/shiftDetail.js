
var totalPrice = 0;

function insertTotalPrice(element, profit, loss) {
    if (profit !== loss) {
        var modifiedAmount = totalPrice + profit - loss;
        document.getElementById(element).innerHTML += modifiedAmount + " (Изначально: " + totalPrice + ")";
    } else {
        document.getElementById(element).innerHTML += totalPrice;
    }
    totalPrice = 0;
}

function setPrice(price) {
    totalPrice += price;
}
