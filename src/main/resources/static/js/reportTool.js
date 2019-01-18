$('#datesClientdownload').click(function (event) {
    event.preventDefault();
    var url = "/rest/reports/createClientsData";
    var period = {
        "startDate": $("#start").val(),
        "endDate": $("#end").val()
    };

    $.ajax({
        type: 'POST',
        url: url,
        datatype: 'application/json',
        // data: JSON.stringify(period),
        // data: {
        //     start: $("#start").val(),
        //     end: $("#end").val()
        // },
        data: {
            startDate: $("#start").val(),
            endDate: $("#end").val()
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
        let params = {
            "startDate": $("#start").val(),
            "endDate": $("#end").val()
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
                    // labels: [1500,1600,1700,1750,1800,1850,1900,1950,1999,2050],
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
    // document.getElementById("line-chart").setAttribute("hidden", !Boolean(document.getElementById("line-chart").getAttribute("hidden")))

    // var url = "/rest/reports/createClientsData";
    // var period = {
    //     "startDate": $("#start").val(),
    //     "endDate": $("#end").val()
    // };



});