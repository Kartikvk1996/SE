var API_END_POINT = "/status";
var refreshInterval = 3000;
var container;

function setAPIEndPoint(apie) {
    API_END_POINT = apie;
}

function dtos(x) {
    return new Date(x).toLocaleString();
}

function tof2(x) { return parseFloat(Math.round(x * 100) / 100).toFixed(2); }

function btom(x) {
    return tof2(x / 1024 / 1024) + "MB";
}

window.onload = function schedule() {
    prolog();
    var timerId = setInterval(
        refresh,
        refreshInterval,
        API_END_POINT
    );
}

function refresh(endpoint) {
    /* query the status */
    var jdata = queryData(endpoint);

    display(jdata);
}


function queryData(endpoint) {
    var jdata;
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200)
            jdata = JSON.parse(this.responseText);
    }
    xhttp.open("GET", endpoint, false);
    xhttp.send();
    return jdata;
}
