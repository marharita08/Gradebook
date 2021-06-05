var request = new XMLHttpRequest();
function search(param, page, mapping) {
    var val = document.getElementById(param).value;
    var url = mapping + "?val=" + val + "&param=" + param + "&page=" + page;
    try {
        request.onreadystatechange = function () {
            if (request.readyState === 4) {
                document.getElementById("placeToShow").innerHTML = request.responseText;
            }
        }
        request.open("GET", url, true);
        request.send();

    } catch (e) {
        alert("Unable to connect to server");
    }
}