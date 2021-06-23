(function() { // avoid variables ending up in the global scope

  document.getElementById("loginbutton").addEventListener('click', (e) => {
    var form = e.target.closest("form");
    if (form.checkValidity()) {
      //function makeCall(method, url, formElement, cback, reset = true)
      makeCall("POST", 'CheckLogin', e.target.closest("form"),
        function(x) {
          if (x.readyState == XMLHttpRequest.DONE) {
            var message = x.responseText;
            switch (x.status) {
              case 200:
                //sessionStorage presenta uno scope più ristretto di localStorage. Rimane visibile
                //all'interno della stessa tab.
                var user = JSON.parse(x.responseText);
                sessionStorage.setItem('role', user.role);
                console.log(user.role);
                sessionStorage.setItem('username', (user.name + ' ' + user.surname));
                console.log(user.name + user.surname);
                if(user.role == "professor"){
                  sessionStorage.setItem('userId', user.professorId);
                  console.log(user.professorId);
                  window.location.href = "HomeProfessor.html";
                } else if(user.role == "student"){
                  sessionStorage.setItem('userId', user.studentId);
                  console.log(user.studentId);
                  window.location.href = "HomeStudent.html";
                }
                break;

              case 400: // bad request
                document.getElementById("errormessage").textContent = message;
                break;
              case 401: // unauthorized
                  document.getElementById("errormessage").textContent = message;
                  break;
              case 500: // server error
              document.getElementById("errormessage").textContent = message;
                break;
            }
          }
        }
      );
    } else {
       form.reportValidity();
    }
  });

})();
