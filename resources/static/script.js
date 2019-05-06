document.addEventListener('DOMContentLoaded', function() {
  var elems = document.querySelectorAll('.dropdown-trigger');
  var options = document.querySelectorAll('#dropdown-option');
  var instances = M.Dropdown.init(elems, options);
});

function makeConnectRequest(orig_name, target_name) {
  $.ajax({
    type: "PUT",
    url: "/profiles/" + orig_name + "/connections",
    data: "target=" + target_name,
    success: function(data, textStatus, request) {
     window.location.href = "/";
    },
    error: function(data, textStatus, request) {
     M.toast({html: data.responseText});
    }
  });
}
// $(document).ready(makeConnectRequest);
