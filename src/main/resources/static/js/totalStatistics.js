function addRowBottom(profit, loss) {
    if (profit !== loss) {
        var difference = profit - loss;
        var el = document.getElementById('clients');
        var row = el.insertRow(-1);
        var diffCel = row.insertCell(-1);
        var infoCell = row.insertCell(-1);
        diffCel.innerHTML = difference;

        if (document.getElementById('commonCheckSum') !== null) {
            diffCel.classList.add("commonCheck");
            infoCell.innerHTML = "Перерасчёт в плюс: " + profit + ". В минус: " + loss;
            infoCell.colSpan = 3;
        } else if (document.getElementById('clubCardPaymentSum') !== null) {
            diffCel.classList.add("cashPaymentSum");
            infoCell.innerHTML = "Перерасчёт в плюс: " + profit + ". В минус: " + loss;
            infoCell.colSpan = 4;
        }

        if (difference > 0) {
            row.style.background = "#CEFFD0";
        } else {
            row.style.background = "#FFD7D7";
        }
    }
}

jQuery(document).ready( function() {
    var totalGivenOtherDebt = 0;
    var totalRepaidOtherDebt = 0;

    Array.from(document.getElementById("givenOtherPrice").rows).forEach(function (item) {
        totalGivenOtherDebt += +item.querySelector(".specificGivenPrice").innerHTML;
    });

    Array.from(document.getElementById("repaidOtherPrice").rows).forEach(function (item) {
        totalRepaidOtherDebt += +item.querySelector(".specificRepaidPrice").innerHTML;
    });

    if (totalGivenOtherDebt === 0 && totalRepaidOtherDebt !== 0) {
        createAndInsertTotalDebtCell("repaidOtherPrice", totalRepaidOtherDebt);
    } else if (totalGivenOtherDebt !== 0 && totalRepaidOtherDebt === 0) {
        createAndInsertTotalDebtCell("givenOtherPrice", totalGivenOtherDebt);
    } else if (totalGivenOtherDebt !== 0 && totalRepaidOtherDebt !== 0) {
        createAndInsertTotalDebtCell("givenOtherPrice", totalGivenOtherDebt);
        createAndInsertTotalDebtCell("repaidOtherPrice", totalRepaidOtherDebt);
    }


    var totalGivenCashBoxDebt = 0;
    var totalRepaidCashBoxDebt = 0;

    Array.from(document.getElementById("givenCashBoxPrice").rows).forEach(function (item) {
        totalGivenCashBoxDebt += +item.querySelector(".specificGivenPrice").innerHTML;
    });

    Array.from(document.getElementById("repaidCashBoxPrice").rows).forEach(function (item) {
        totalRepaidCashBoxDebt += +item.querySelector(".specificRepaidPrice").innerHTML;
    });

    if (totalGivenCashBoxDebt === 0 && totalRepaidCashBoxDebt !== 0) {
        createAndInsertTotalDebtCell("repaidCashBoxPrice", totalRepaidCashBoxDebt);
    } else if (totalGivenCashBoxDebt !== 0 && totalRepaidCashBoxDebt === 0) {
        createAndInsertTotalDebtCell("givenCashBoxPrice", totalGivenCashBoxDebt);
    } else if (totalGivenCashBoxDebt !== 0 && totalRepaidCashBoxDebt !== 0) {
        createAndInsertTotalDebtCell("givenCashBoxPrice", totalGivenCashBoxDebt);
        createAndInsertTotalDebtCell("repaidCashBoxPrice", totalRepaidCashBoxDebt);
    }


    if($("#clubCardPaymentSum").length) {
        var totalSumWithCard = [0, 0, 0, 0, 0, 0];
        Array.from(document.getElementById("clients").rows).forEach(function(item) {
            if(item.querySelector('.commonCheck')) {
                totalSum[0] += +item.querySelector('.commonCheck').innerHTML;
            }
            if(item.querySelector('.cashPayment')) {
                totalSumWithCard[1] += +(item.querySelector('.cashPayment').innerHTML).replace(",", "");
            }
            if(item.querySelector('.clubCardPayment')) {
                totalSumWithCard[2] += +(item.querySelector('.clubCardPayment').innerHTML).replace(",", "");
            }
            if(item.querySelector('.menuOtherCost')) {
                totalSumWithCard[3] += +(item.querySelector('.menuOtherCost').innerHTML).replace(",", "");
            }
            if(item.querySelector('.menuDirtyCost')) {
                totalSumWithCard[4] += +(item.querySelector('.menuDirtyCost').innerHTML).replace(",", "");
            }
            if(item.querySelector('.timeCost')) {
                totalSumWithCard[5] += +(item.querySelector('.timeCost').innerHTML).replace(",", "");
            }
        });
        document.getElementById("commonCheckSum").innerHTML += " " + Math.round(totalSumWithCard[0] * 100) / 100.00;
        document.getElementById("cashPaymentSum").innerHTML += " " + Math.round(totalSumWithCard[1] * 100) / 100.00;
        document.getElementById("clubCardPaymentSum").innerHTML += " " + Math.round(totalSumWithCard[2] * 100) / 100.00;
        document.getElementById("menuOtherSum").innerHTML += " " + Math.round(totalSumWithCard[3] * 100) / 100.00;
        document.getElementById("menuDirtySum").innerHTML += " " + Math.round(totalSumWithCard[4] * 100) / 100.00;
        document.getElementById("timeCostSum").innerHTML += " " + totalSumWithCard[5];
    } else {
        var totalSum = [0, 0, 0, 0];
        Array.from(document.getElementById("clients").rows).forEach(
            function(item) {
                if(item.querySelector('.commonCheck')) {
                    totalSum[0] += +(item.querySelector('.commonCheck').innerHTML).replace(",", "");
                }
                if(item.querySelector('.menuOtherCost')) {
                    totalSum[1] += +(item.querySelector('.menuOtherCost').innerHTML).replace(",", "");
                }
                if(item.querySelector('.menuDirtyCost')) {
                    totalSum[2] += +(item.querySelector('.menuDirtyCost').innerHTML).replace(",", "");
                }
                if(item.querySelector('.timeCost')) {
                    totalSum[3] += +(item.querySelector('.timeCost').innerHTML).replace(",", "");
                }
            });
        document.getElementById("commonCheckSum").innerHTML += " " + Math.round(totalSum[0] * 100) / 100.00;
        document.getElementById("menuOtherSum").innerHTML += " " + Math.round(totalSum[1] * 100) / 100.00;
        document.getElementById("menuDirtySum").innerHTML += " " + Math.round(totalSum[2] * 100) / 100.00;
        document.getElementById("timeCostSum").innerHTML += " " + totalSum[3];
    }

});

function createAndInsertTotalDebtCell(tbodyId, totalDebt) {
    var tbody = document.getElementById(tbodyId);
    var row = tbody.getElementsByTagName("tr")[0];
    var rowsCount = tbody.getElementsByTagName("tr").length;
    var cell = row.insertCell(0);
    cell.innerHTML = totalDebt;
    cell.rowSpan = rowsCount;
    if (tbodyId === "repaidOtherPrice" || tbodyId === "repaidCashBoxPrice") {
        cell.style.background = "#CEFFD0";
    } else {
        cell.style.background = "#FFD7D7";
    }
}