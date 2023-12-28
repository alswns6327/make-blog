const deleteButton = document.getElementById("delete-btn");

if(deleteButton){
    deleteButton.addEventListener("click", event => {
        const id = document.getElementById("article-id").value;
        function success(){
            alert("delete complet");
            location.replace("/articles");
        }

        function fail() {
            alert("delete fail");
            location.replace("/articles");
        }

        httpRequest("DELETE", `/api/articles/${id}`, null, success, fail);
    });
}

const modifyButton = document.getElementById("modify-btn");

if(modifyButton){
    modifyButton.addEventListener("click", event => {
        let params = new URLSearchParams(location.search);
        const id = params.get("id");

        let body = JSON.stringify({
            title: document.getElementById("title").value,
            content: document.getElementById("content").value
        });

        function success(){
            alert("update complete");
            location.replace(`/articles/` + id)
        };
        function fail(){
            alert("update fail");
            location.replace(`/articles/` + id)
        };

        httpRequest("PUT", `/api/articles/${id}`, body, success, fail);
    });
}

const saveButton = document.getElementById("save-btn");
if(saveButton){
    saveButton.addEventListener("click", function(){
        let body = JSON.stringify({
            title: document.getElementById("title").value,
            content: document.getElementById("content").value,
        });

        function success(){
            alert("save complete");
            location.replace(`/articles`);
        }

        function fail() {
            alert("save fail");
            location.replace("/articles");
        }

        httpRequest("POST", "/api/articles", body, success, fail);
    });
}

function getCookie(key){
    var result = null;
    var cookie = document.cookie.split(";");

    cookie.some(item => {
        item = item.replace(" ", "");

        var dic = item.split("=");

        if(key === dic[0]){
            result = dic[1];
            return true;
        }
    });

    return result;
}

// HTTP요청
function httpRequest(method, url, body, success, fail){
    fetch(url,{
        method: method,
        headers:{
            // localstorage의 token을 header에 추가
            Authorization: "Bearer " + localStorage.getItem("access_token"),
            "Content-Type": "application/json",
        },
        body:body,
    }).then((response) => {
        if(response.status === 200 || response.status === 201 || response.status === 204){
            return success();
        }
        const refresh_token = getCookie("refresh_token");
        if(response.status === 401 && refresh_token){
            fetch("/api/token", {
                method: "POST",
                headers: {
                    Authorization: "Bearer " + localStorage.getItem("access_token"),
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    refreshToken: refresh_token,
                }),
            })
            .then(res=>{
                if(res.ok){
                    return res.json();
                }
            })
            .then(result => {
                //재발급 성공시 로컬 스토리지에 새로 저장
                localStorage.setItem("access_token", result.accessToken);
                httpRequest(method, url, body, success, fail);
            })
            .catch(e => fail());
        } else {
            return fail();
        }
    });
}