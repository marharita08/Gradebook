var request = new XMLHttpRequest();
function checkUsername(userID) {
    var val = document.getElementById("username").value;
    var url = "/Gradebook/checkUsername?val=" + val + "&id=" + userID;
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