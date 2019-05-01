document.addEventListener('DOMContentLoaded', function() {
  var elems = document.querySelectorAll('.dropdown-trigger');
  var options = document.querySelectorAll('#dropdown-option');
  var instances = M.Dropdown.init(elems, options);
});

function makePutRequest(orig_name, target_name) {
  $.ajax({
    type: "PUT",
    url: "/profiles/" + orig_name,
    // data: "name=name&location=location",
    success: function(msg){
      // console.log(this.url);
      console.log(msg);
    }
  });
}
$(document).ready(makePutRequest);
