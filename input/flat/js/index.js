document.addEventListener("DOMContentLoaded", function(){
  Ajax.promiseRequest({ url : "https://gist.githubusercontent.com/Somnid/9e65c455c3757ca008e6/raw/4e303d2bf2198fb07a3c8592bdcb42a2e07f202b/watch-data.flat" }).then(function(result){
    console.log(Flat.parse(result));
  });
});