const deleteButton = document.getElementById("delete-btn");

if(deleteButton){
    deleteButton.addEventListener("click", event => {
        const id = document.getElementById("article-id").value;
        fetch(`/api/articles/${id}`, {
            method: "DELETE"
        })
        .then(() => {
            alert("delete complet");
            location.replace("/articles");
        });
    });
}

const modifyButton = document.getElementById("modify-btn");

if(modifyButton){
    modifyButton.addEventListener("click", event => {
        let params = new URLSearchParams(location.search);
        const id = params.get("id");
        fetch(`/api/articles/${id}`, {
            method: "PUT",
            headers: {
               "Content-Type": "application/json",
            },
            body: JSON.stringify({
                title: document.getElementById("title").value,
                content: document.getElementById("content").value
            })
        })
        .then(()=>{
            alert("update complete");
            location.replace(`/articles/${id}`)
        });
    });
}

const saveButton = document.getElementById("save-btn");
if(saveButton){
    saveButton.addEventListener("click", function(){
        fetch("/api/articles", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                title: document.getElementById("title").value,
                content: document.getElementById("content").value,
            })
        })
        .then((result)=>{
            alert("save complete");
            location.replace(`/articles`);
        });
    });
}