const saveButton = document.getElementById("submit-btn");
if(saveButton){
    saveButton.addEventListener("click", function(){
        fetch("/api/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                "email" : document.getElementById("username").value,
                "password" : document.getElementById("password").value
            })
        })
        .then((result)=>{
            console.log(result);
            console.log(result.data);
            // alert("save complete");
            // location.replace(`/articles`);
        });
    });
}