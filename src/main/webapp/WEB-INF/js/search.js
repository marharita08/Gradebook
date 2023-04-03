var request = new XMLHttpRequest();
function search(param, entity) {
    var val = document.getElementById(param).value;
    var url = entity + "/search?val=" + val + "&param=" + param;
    try {
        request.onreadystatechange = function () {
            if (request.readyState === 4) {
                showTable(JSON.parse(request.responseText));
            }
        }
        request.open("GET", url, true);
        request.send();
    } catch (e) {
        alert("Unable to connect to server");
    }
}
