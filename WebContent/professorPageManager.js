{
    let courseList, examDate, registeredStudents, resultDetail, pageOrchestrator = new PageOrchestrator();
    
    window.addEventListener("load", () => {
        if ((sessionStorage.getItem("role") == null) || (sessionStorage.getItem("userId") == null) || (sessionStorage.getItem("username") == null)) {
          window.location.href = "index.html";
        } else {    
          pageOrchestrator.start(); // initialize components
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
            this.alert.style.visibility = "hidden";
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
                                self.alert.style.visibility = "visible";
                                return;
                            }
                            self.alert.style.visibility = "hidden";
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
                    registeredStudents.reset();
                    resultDetail.reset();
                    examDate.show(e.target.getAttribute("courseid"), self.listcontainer); // the list must know the details container
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

        this.show = function(courseid, _courselistcontainer){
            this.courselistcontainer = _courselistcontainer;
            var self = this;
            makeCall("GET", "GetCourseDetails?courseid=" + courseid, null, 
                function(req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var examdates = JSON.parse(req.responseText);
                            if (examdates.length == 0) {
                                self.alert.textContent = "No exams yet!";
                                self.alert.style.visibility = "visible";
                                self.detailcontainer.style.visibility = "hidden";
                                return;
                            }
                            self.alert.style.visibility = "hidden";
                            self.detailcontainer.style.visibility = "visible";
                            self.update(examdates); 
                            
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
                anchor.setAttribute("appelloid", examdate.appelloId);
                anchor.addEventListener("click", (e) => {   
                    // dependency via module parameter
                    resultDetail.reset();
                    registeredStudents.show(e.target.getAttribute("appelloid")); // the list must know the details container
                }, false);
                anchor.href = "#";
                row.appendChild(linkcell);
                self.detailcontainerbody.appendChild(row);
            });
            this.detailcontainer.style.visibility = "visible";
        }
    }

    function RegisteredStudents(_alert, _studentcontainer, _studentcontainerbody, _pubblicacontainer, _verbalizzacontainer){
        this.alert = _alert;
        this.studentcontainer = _studentcontainer;
        this.studentcontainerbody = _studentcontainerbody;
        this.pubblicacontainer = _pubblicacontainer;
        this.verbalizzacontainer = _verbalizzacontainer; 

        this.reset = function(){
            this.alert.style.visibility = "hidden";
            this.studentcontainer.style.visibility = "hidden";
            this.pubblicacontainer.style.visibility = "hidden";
            this.verbalizzacontainer.style.visibility = "hidden";
        }

        this.show = function(appelloId){
            var self = this;
            makeCall("GET", "GetRegisteredStudents?appelloid=" + appelloId, null, 
                function(req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var registeredStudents = JSON.parse(req.responseText);
                            if (registeredStudents.length == 0) {
                                self.alert.style.visibility = "visible";
                                self.alert.textContent = "No registered students yet!";
                                self.studentcontainer.style.visibility = "hidden";
                                return;
                            }
                            self.alert.style.visibility = "hidden";
                            self.studentcontainer.style.visibility = "visible";
                            self.pubblicacontainer.style.visibility = "visible";
                            self.verbalizzacontainer.style.visibility = "visible";
                            self.update(registeredStudents); // self visible by closure
                            
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

        this.update = function(registeredStudents){
            var elem, i, row, studentid, surname, name, email, corsoDiLaurea, grade, status, linkcell, anchor;
            var self = this;
            this.studentcontainerbody.innerHTML = "";
            registeredStudents.forEach(function(registeredStudent){
                row = document.createElement("tr");

                studentid = document.createElement("td");
                studentid.textContent = registeredStudent.studentId;
                row.appendChild(studentid);

                surname = document.createElement("td");
                surname.textContent = registeredStudent.studentSurname;
                row.appendChild(surname);

                name = document.createElement("td");
                name.textContent = registeredStudent.studentName;
                row.appendChild(name);

                email = document.createElement("td");
                email.textContent = registeredStudent.studentEmail;
                row.appendChild(email);   

                corsoDiLaurea = document.createElement("td");
                corsoDiLaurea.textContent = registeredStudent.corsoDiLaurea;
                row.appendChild(corsoDiLaurea); 

                grade = document.createElement("td");
                grade.textContent = registeredStudent.grade;
                row.appendChild(grade); 

                status = document.createElement("td");
                status.textContent = registeredStudent.status;
                row.appendChild(status); 

                linkcell = document.createElement("td");
                anchor = document.createElement("a");
                linkcell.appendChild(anchor);
                linkText = document.createTextNode("Modify");

                anchor.appendChild(linkText);
                anchor.setAttribute("examid", registeredStudent.examId);
                anchor.addEventListener("click", (e) => {
                     resultDetail.show(e.target.getAttribute("examid"));
                }, false);

                anchor.href = "#";
                row.appendChild(linkcell);
                self.studentcontainerbody.appendChild(row);
            });
            this.studentcontainer.style.visibility = "visible";
        }
    }

    function ResultDetails(options){
        this.alert = options['alert'];
        this.resultcontainer = options['resultcontainer'];
        this.name = options['name'];
        this.surname = options['surname'];
        this.id = options['id'];
        this.email = options['email'];
        this.cdl = options['cdl'];
        this.updatecontainer = options['updatecontainer'];

        this.reset = function(){
            this.resultcontainer.style.visibility = "hidden";
            this.updatecontainer.style.visibility = "hidden";
        }
        
       
        this.show = function(examid){
            var self = this; 
            makeCall("GET", "GetExamDetails?examId=" + examid, null,
                function(req){
                    if(req.readyState == 4){
                        var message = req.responseText;
                        if(req.status == 200){
                            var exam = JSON.parse(req.responseText);
                            self.update(exam);
                            self.resultcontainer.style.visibility = "visible";
                            switch(exam.status){
                                case "INSERITO":
                                    break;
                                case "NONINSERITO":
                                    self.updatecontainer.style.visibility = "visible";
                                    break;
                                case "PUBBLICATO":
                                    break;
                                case "RIFIUTATO":
                                    break;
                                case "VERBALIZZATO":
                                    break;
                            }
                        } else if(req.status == 403) {
                            window.location.href = req.getResponseHeader("Location");
                            window.sessionStorage.removeItem('role');
                            window.sessionStorage.removeItem('userId');
                            window.sessionStorage.removeItem('username');
                        } else {
                            self.alert.textContent = message;
                        }
                    }
                }
            );
        }

        this.update = function(exam){
            console.log(exam);
            this.name.textContent = exam.studentName;
            this.surname.textContent = exam.studentSurname;
            this.email.textContent = exam.studentEmail;
            this.id.textContent = exam.studentId;
            this.cdl.textContent = exam.corsoDiLaurea;
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
                document.getElementById("id_listcontainerbody"));  

            examDate = new ExamDate(alertContainer, 
                document.getElementById("id_detailcontainer"),
                document.getElementById("id_detailcontainerbody"));

            registeredStudents = new RegisteredStudents(alertContainer, 
                document.getElementById("id_studentcontainer"),
                document.getElementById("id_studentcontainerbody"),
                document.getElementById("id_pubblicacontainer"),
                document.getElementById('id_verbalizzacontainer'));

            resultDetail = new ResultDetails({
                alert : alertContainer,
                resultcontainer : document.getElementById("id_resultcontainer"),
                name : document.getElementById("id_studentname"),
                surname : document.getElementById("id_studentsurname"),
                id : document.getElementById("id_studentid"),
                email : document.getElementById("id_studentemail"),
                cdl : document.getElementById("id_studentcdl"),
                updatecontainer : document.getElementById("id_updatecontainer")
            });

            //resultDetail.registerEvents(this);

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
            }); 
            registeredStudents.reset();
            resultDetail.reset();
        };
    }



};/**
 * 
 */