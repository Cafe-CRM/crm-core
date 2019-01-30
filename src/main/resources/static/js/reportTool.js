$('#datesClientdownload').click(function (event) {
    event.preventDefault();
    var url = "/rest/reports/createClientsData";
    var selectedWeekDays = [];
    $("input:checkbox[name=selectedWeekDays]:checked").each(function(){
        selectedWeekDays.push($(this).val());
    });
    $.ajax({
        type: 'POST',
        url: url,
        data: {
            startDate: $("#start").val(),
            endDate: $("#end").val(),
            weekDays: selectedWeekDays
        },
        success: function () {
            window.location.replace("/rest/reports/getClientsData")
        }
    });
});

$('#drawChart').click(function (event) {
    event.preventDefault();
    if (document.getElementById("line-chart").style.display != "block") {
        let url = '/rest/reports/getDataForChart';
        let selectedWeekDays = [];
        $("input:checkbox[name=selectedWeekDays]:checked").each(function(){
            selectedWeekDays.push($(this).val());
        })
        let params = {
            "startDate": $("#start").val(),
            "endDate": $("#end").val(),
            "weekDays": selectedWeekDays
        };
        var coord = [];
        var values = [];
        $.get(url, params, function get(list) {
        }).done(function (list) {
            for (let i = 0; i < list.length; i++) {
                coord.push(list[i].date.dayOfMonth + "." + list[i].date.monthValue + "." + list[i].date.year);
                values.push(list[i].clientsNumber);
            }

            new Chart(document.getElementById("line-chart"), {
                type: 'line',
                bezierCurve : false,
                data: {
                    labels: coord,
                    datasets: [{
                        data: values,
                        // label: "Attendance",
                        borderColor: "#3e95cd",
                        fill: true,
                        lineTension: 0
                    }
                    ]
                },
                options: {
                    title: {
                        display: false,
                        // text: 'Посещаемость по дням'
                    },
                    legend: {
                        display: false
                    },
                    scales: {
                        xAxes: [{
                            ticks: {
                                fontColor: '#5bc0de'
                            },
                            gridLines: {
                                display: true,
                            }

                        }],
                        yAxes: [{
                            ticks: {
                                fontColor: '#5bc0de'
                            }
                        }]

                    }
                },
            });

        });
        document.getElementById("line-chart").style.display = "block";
    } else {
        document.getElementById("line-chart").style.display = "none";
    }
});

$('#drawProductChart').click(function (event) {
    event.preventDefault();
    if (document.getElementById("line-chart").style.display != "block") {
        let url = '/rest/reports/getDataForChartProducts';
        let selectedProducts = [];
        $("input:checkbox[name=productId]:checked").each(function(){
            selectedProducts.push($(this).val());
        });
        let selectedWeekDays = [];
        $("input:checkbox[name=selectedWeekDays]:checked").each(function(){
            selectedWeekDays.push($(this).val());
        })

        let params = {
            "startDate": $("#start").val(),
            "endDate": $("#end").val(),
            "weekDays": selectedWeekDays,
            "products": selectedProducts
        };
        var coord = [];
        var values = [];
        $.get(url, params, function get(list) {
        }).done(function (listProductSales) {
            for (let k = 0; k < listProductSales.length; k++) {
                coord.push(listProductSales[k].date.dayOfMonth + "." + listProductSales[k].date.monthValue + "." + listProductSales[k].date.year);
                values.push(listProductSales[k].count);
            }
            new Chart(document.getElementById("line-chart"), {
                type: 'line',
                bezierCurve : false,
                data: {
                    labels: coord,
                    datasets: [{
                        data: values,
                        // label: "Attendance",
                        borderColor: "#3e95cd",
                        fill: true,
                        lineTension: 0
                    }
                    ]
                },
                options: {
                    title: {
                        display: false,
                        // text: 'Посещаемость по дням'
                    },
                    legend: {
                        display: false
                    },
                    scales: {
                        xAxes: [{
                            ticks: {
                                fontColor: '#5bc0de'
                            },
                            gridLines: {
                                display: true,
                                // color: 'white'
                            }

                        }],
                        yAxes: [{
                            ticks: {
                                fontColor: '#5bc0de'
                            }
                        }]

                    }
                },
            });
        });
        document.getElementById("line-chart").style.display = "block";
    } else {
        document.getElementById("line-chart").style.display = "none";
    }
});