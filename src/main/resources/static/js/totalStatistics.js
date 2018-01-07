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
    var totalGivenDebt = 0;
    var totalRepaidDebt = 0;

    Array.from(document.getElementById("givenPrice").rows).forEach(function (item) {
        totalGivenDebt += +item.querySelector(".specificGivenPrice").innerHTML;
    });

    Array.from(document.getElementById("repaidPrice").rows).forEach(function (item) {
        totalRepaidDebt += +item.querySelector(".specificRepaidPrice").innerHTML;
    });

    if (totalGivenDebt === 0 && totalRepaidDebt !== 0) {
        createAndInsertTotalDebtCell("repaidPrice", totalRepaidDebt);
    } else if (totalGivenDebt !== 0 && totalRepaidDebt === 0) {
        createAndInsertTotalDebtCell("givenPrice", totalGivenDebt);
    } else if (totalGivenDebt !== 0 && totalRepaidDebt !== 0) {
        createAndInsertTotalDebtCell("givenPrice", totalGivenDebt);
        createAndInsertTotalDebtCell("repaidPrice", totalRepaidDebt);
    }



    if($("#clubCardPaymentSum").length) {
        var totalSumWithCard = [0, 0, 0, 0, 0, 0];
        Array.from(document.getElementById("clients").rows).forEach(function(item) {
            if(item.querySelector('.commonCheck')) {
                totalSum[0] += +item.querySelector('.commonCheck').innerHTML;
            }
            if(item.querySelector('.cashPayment')) {
                totalSumWithCard[1] += +item.querySelector('.cashPayment').innerHTML;
            }
            if(item.querySelector('.clubCardPayment')) {
                totalSumWithCard[2] += +item.querySelector('.clubCardPayment').innerHTML;
            }
            if(item.querySelector('.menuOtherCost')) {
                totalSumWithCard[3] += +item.querySelector('.menuOtherCost').innerHTML;
            }
            if(item.querySelector('.menuDirtyCost')) {
                totalSumWithCard[4] += +item.querySelector('.menuDirtyCost').innerHTML;
            }
            if(item.querySelector('.timeCost')) {
                totalSumWithCard[5] += +item.querySelector('.timeCost').innerHTML;
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
                    totalSum[0] += +item.querySelector('.commonCheck').innerHTML;
                }
                if(item.querySelector('.menuOtherCost')) {
                    totalSum[1] += +item.querySelector('.menuOtherCost').innerHTML;
                }
                if(item.querySelector('.menuDirtyCost')) {
                    totalSum[2] += +item.querySelector('.menuDirtyCost').innerHTML;
                }
                if(item.querySelector('.timeCost')) {
                    totalSum[3] += +item.querySelector('.timeCost').innerHTML;
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
    if (tbodyId === "repaidPrice") {
        cell.style.background = "#CEFFD0";
    } else {
        cell.style.background = "#FFD7D7";
    }
}