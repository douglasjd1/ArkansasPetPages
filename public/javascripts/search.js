document.getElementById('search').onkeyup = function(){
 myNameFunction();
}

function myNameFunction()
{
 var nameInput, filter, table, div, a;
 nameInput = document.getElementById("search");
 filter = nameInput.value.toUpperCase();
 table = document.getElementById("row");
 div = table.getElementsByTagName("div");

 for (i = 0; i < div.length; i++) {
   a=div[i].getElementsByTagName("a")[0];
   if (a) {
     if (a.innerHTML.toUpperCase().indexOf(filter) > -1) {
       div[i].style.display = "";
     } else {
       div[i].style.display = "none";
     }
   }
 }
};