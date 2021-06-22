{
    let pageOrchestrator = new PageOrchestrator();

    window.addEventListener("load", () => {
        if ((sessionStorage.getItem("role") == null) || (sessionStorage.getItem("userId") == null) || (sessionStorage.getItem("username") == null)) {
          window.location.href = "index.html";
        } else {
          pageOrchestrator.start(); // initialize the components
          pageOrchestrator.refresh();
        } // display initial content
      }, false);

    function PersonalMessage(_username, messagecontainer) {
        this.username = _username;
        this.show = function() {
          messagecontainer.textContent = this.username;
        }
    }

    function PageOrchestrator(){

        var alertContainer = document.getElementById("id_alert");

        this.start = function(){
            personalMessage = new PersonalMessage(sessionStorage.getItem('role'), 
                document.getElementById("id_role"));
            personalMessage.show();

            personalMessage = new PersonalMessage(sessionStorage.getItem('username'), 
                document.getElementById("id_username"));
            personalMessage.show();

            document.querySelector("a[href='Logout']").addEventListener('click', () => {
            window.sessionStorage.removeItem('role');
            window.sessionStorage.removeItem('userId');
            window.sessionStorage.removeItem('username');
          })

        };

        this.refresh = function(){


        };
    }



};