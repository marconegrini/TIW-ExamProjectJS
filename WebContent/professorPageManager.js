{
    let courseList, examDate, pageOrchestrator = new PageOrchestrator();

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
                linkText = document.createTextNode("Show");
                anchor.appendChild(linkText);
                anchor.setAttribute("courseid", course.courseId);
                anchor.addEventListener("click", (e) => {
                    // dependency via module parameter
                    examDate.show(e.target.getAttribute("courseid")); // the list must know the details container
                }, false);   
                anchor.href = "#";
                row.appendChild(linkcell);
                self.listcontainerbody.appendChild(row);
            });
            this.listcontainer.style.visibility = "visible";
        }

        this.autoclick = function(courseId){
            var e = new Event("click");
            //The Document method querySelector() returns the first Element within the document that matches the specified selector
          var selector = "a[courseid='" + courseId + "']";
          var anchorToClick =  
            (courseId) ? document.querySelector(selector) : this.listcontainerbody.querySelectorAll("a")[0];
          if (anchorToClick) anchorToClick.dispatchEvent(e);
        }
    }

    function ExamDate(_alert, _detailcontainer, _detailcontainerbody){
        this.alert = _alert;
        this.detailcontainer = _detailcontainer;
        this.detailcontainerbody = _detailcontainerbody;

        this.reset = function(){
            this.detailcontainer.style.visibility = "hidden";
            this.alert.style.visibility = "hidden";
        };

        this.show = function(courseid){
            var self = this;
            makeCall("GET", "GetCourseDetails?courseid=" + courseid, null, 
                function(req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var examdates = JSON.parse(req.responseText);
                            if (examdates.length == 0) {
                                self.alert.textContent = "No exams yet!";
                                self.detailcontainer.style.visibility = "hidden";
                                self.alert.style.visibility = "visible";
                                return;
                            }
                            self.alert.style.visibility = "hidden";
                            self.detailcontainer.style.visibility = "visible";
                            self.update(examdates); // self visible by closure
                            
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

        this.update = function(examdates){
            var elem, i, row, datecell, linkcell, anchor;
            this.detailcontainerbody.innerHTML = "";
            var self = this;
            examdates.forEach(function(examdate){
                row = document.createElement("tr");
                datecell = document.createElement("td");
                datecell.textContent = examdate.date;
                row.appendChild(datecell);
                linkcell = document.createElement("td");
                anchor = document.createElement("a");
                linkcell.appendChild(anchor);
                linkText = document.createTextNode("Detail");
                anchor.appendChild(linkText);
                anchor.setAttribute("examid", examdate.appelloId);
                anchor.addEventListener("click", (e) => {
                    // dependency via module parameter
                    missionDetails.show(e.target.getAttribute("examid")); // the list must know the details container
                }, false);
                anchor.href = "#";
                row.appendChild(linkcell);
                self.detailcontainerbody.appendChild(row);
            });
            this.detailcontainer.style.visibility = "visible";
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

            courseList = new CourseList(alertContainer, 
                document.getElementById("id_listcontainer"), 
                document.getElementById("id_listcontainerbody")
                );  

            examDate = new ExamDate(alertContainer, 
                document.getElementById("id_detailcontainer"),
                document.getElementById("id_detailcontainerbody"));



            document.querySelector("a[href='Logout']").addEventListener('click', () => {
            window.sessionStorage.removeItem('role');
            window.sessionStorage.removeItem('userId');
            window.sessionStorage.removeItem('username');
          });

        };

        this.refresh = function(currentCourse){  //currentCourse null at start
            alertContainer.textContent = " ";
            courseList.reset();
            examDate.reset();
            courseList.show(function(){
                courseList.autoclick(currentCourse);
            }); //TODO add autolick function 
        };
    }



};/**
 * 
 */