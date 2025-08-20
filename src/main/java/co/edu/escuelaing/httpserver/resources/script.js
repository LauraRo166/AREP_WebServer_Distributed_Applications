function sendGet() {
    let nameVar = document.getElementById("nameGet").value;
    const xhttp = new XMLHttpRequest();
    xhttp.onload = function () {
        try {
            const contentType = this.getResponseHeader("Content-Type");
            if (contentType && contentType.includes("application/json")) {
                const json = JSON.parse(this.responseText);
                document.getElementById("responseGet").innerText =
                    "Hola " + json.name;
            } else {
                document.getElementById("responseGet").innerText = this.responseText;
            }
        } catch (err) {
            document.getElementById("responseGet").innerText = "Error";
        }
    }
    xhttp.open("GET", "/app/hello?name=" + encodeURIComponent(nameVar));
    xhttp.send();
}

function sendPost() {
    let nameVar = document.getElementById("namePost").value;
    const xhttp = new XMLHttpRequest();
    xhttp.onload = function () {
        try {
            const contentType = this.getResponseHeader("Content-Type");
            if (contentType && contentType.includes("application/json")) {
                const json = JSON.parse(this.responseText);
                document.getElementById("responsePost").innerText =
                    json.name;
            } else {
                document.getElementById("responsePost").innerText = this.responseText;
            }
        } catch (err) {
            document.getElementById("responsePost").innerText = "Error";
        }
    }
    xhttp.open("POST", "/app/hellopost?name=" + encodeURIComponent(nameVar));
    xhttp.send();
}