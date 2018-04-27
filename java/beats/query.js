var API_END_POINT = "";
var refreshInterval = 3000;
var container;

function setAPIEndPoint(apie) {
    API_END_POINT = apie;
}

function dtos(x) {
    return new Date(x).toLocaleString();
}

function tdiff(x) {
    x = new Date().getTime() - x;
    return Math.round(x / 1000) + "s ago";
}

function tof2(x) { return parseFloat(Math.round(x * 100) / 100).toFixed(2); }

function btom(x) {
    return tof2(x / 1024 / 1024) + "MB";
}

function insert_common_header() {

    var appwindow = document.getElementsByClassName("appwindow")[0];
    var hdr = document.createElement("div");
    hdr.classList += "header";

    hdr.innerHTML = `
                    <a href='index.html'>
                        <span class='beats-label'>Beats</span>
                    </a>
                    <div style="float: right">
                        <a class="navbarlink" href='help.html'>Help</a>
                    </div>
                    <div style="float: right">
                        <a class="navbarlink" href='api.html'>API</a>
                    </div>`;

    if(appwindow.childElementCount != 0)
        appwindow.insertBefore(hdr, appwindow.childNodes[0]);
    else
        appwindow.appendChild(hdr);
}


window.onload = function schedule() {
    insert_common_header();
    prolog();
    if(API_END_POINT == "")
        return;
    refresh(API_END_POINT);
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
