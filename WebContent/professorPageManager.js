{
    let courseList, pageOrchestrator = new PageOrchestrator();

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

    function CourseList(_alert, _listcontainer, _listcontainerbody){
        this.alert = _alert;
        this.listcontainer = _listcontainer;
        this.listcontainerbody = _listcontainerbody;

        this.reset = function(){
            this.listcontainer.style.visibility = "hidden";
        }

        this.show = function(next){
            var self = this;
            makeCall("GET", "GetProfessorCourses", null, 
                function(req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var courseList = JSON.parse(req.responseText);
                            if (courseList.length == 0) {
                                self.alert.textContent = "No courses yet!";
                                return;
                            }
                            self.update(courseList); // self visible by closure
                            if (next) next(); // show the default element of the list if present

                        } else if (req.status == 403) {
                            window.location.href = req.getResponseHeader("Location");
                            window.sessionStorage.removeItem('username');
                            window.sessionStorage.removeItem('role');
                            window.sessionStorage.removeItem('userId');
                        } else {
                            self.alert.textContent = message;
                        }
                    }
                }
            );

        }

        this.update = function(courseList){
            var elem, i, row, codecell, namecell, linkcell, anchor;
            this.listcontainerbody.innerHTML = "";
            var self = this;
            courseList.forEach(function(course){
                row = document.createElement("tr");
                codecell = document.createElement("td");
                codecell.textContent = course.code;
                row.appendChild(codecell);
                namecell = document = document.createElement("td");
                namecell.textContent = course.name;
                row.appendChild(namecell);
                //creating a linking cell
                linkcell = document.createElement("td");
                anchor = document.createElement("a");
                linkcell.appendChild(anchor);
                //adding linking text
                linkText = document.createTextNode("Detail");
                anchor.appendChild(linkText);
                anchor.setAttribute("courseid", course.courseId);
                anchor.addEventListener("click", (e) => {
                    // dependency via module parameter
                    examSession.show(e.target.getAttribute("courseid")); // the list must know the details container
                }, false);   
                anchor.href = "#";
                row.appendChild(linkcell);
                self.listcontainerbody.appendChild(row);
            });
        }

        this.autoclick = function(courseId){

        }
    }

    function ExamSession(){

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

            courseList = new CourseList(alertContainer, 
                document.getElementById("id_listcontainer"), 
                document.getElementById("id_listcontainerbody")
                );  

            document.querySelector("a[href='Logout']").addEventListener('click', () => {
            window.sessionStorage.removeItem('role');
            window.sessionStorage.removeItem('userId');
            window.sessionStorage.removeItem('username');
          })

        };

        this.refresh = function(){  //currentCourse null at start
            alertContainer.textContent = " ";
            //courseList.reset();
            //examSession.reset();
            courseList.show(); //TODO add autolick function 


        };
    }



};