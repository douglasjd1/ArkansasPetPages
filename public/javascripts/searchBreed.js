document.getElementById('searchBreed').onkeyup = function(){
 myBreedFunction();
}

function myBreedFunction()
{
 var breedInput, filter, table, div, p;
 breedInput = document.getElementById("searchBreed");
 filter = breedInput.value.toUpperCase();
 table = document.getElementById("row");
 div = table.getElementsByTagName("div");

 for (i = 0; i < div.length; i++) {
   p=div[i].getElementsByTagName("p")[0];
   if (p) {
     if (p.innerHTML.toUpperCase().indexOf(filter) > -1) {
       div[i].style.display = "";
     } else {
       div[i].style.display = "none";
     }
   }
 }
};