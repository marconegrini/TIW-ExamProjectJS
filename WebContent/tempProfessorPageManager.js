{
    let courseList, examDate, registeredStudents, report, pageOrchestrator = new PageOrchestrator();
    
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
                        } else if(req.status == 400){
                            console.log(message);
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
                    report.reset();
                    registeredStudents.reset();
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
                        } else if(req.status == 400){
                            console.log(message);
                        }else {
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
                    report.reset();
                    registeredStudents.reset();
                    registeredStudents.show(e.target.getAttribute("appelloid")); // the list must know the details container
                }, false);
                anchor.href = "#";
                row.appendChild(linkcell);
                self.detailcontainerbody.appendChild(row);
            });
            this.detailcontainer.style.visibility = "visible";
        }
    }

    function RegisteredStudents(_alert, _studentcontainer, _studentcontainerbody, _pubblicaform, _verbalizzaform, _submitgrade){
        this.alert = _alert;
        this.studentcontainer = _studentcontainer;
        this.studentcontainerbody = _studentcontainerbody;
        this.pubblicaform = _pubblicaform;
        this.verbalizzaform = _verbalizzaform;
        this.submitgrade = _submitgrade;

        this.reset = function(){
            this.alert.style.visibility = "hidden";
            this.studentcontainer.style.visibility = "hidden";
            this.pubblicaform.style.visibility = "hidden";
            this.verbalizzaform.style.visibility = "hidden";
            this.submitgrade.style.visibility = "hidden";
        }

        this.registerEvents = function() {

            this.submitgrade.querySelector("input[type='button']").addEventListener('click', (event) => {
                var self = this;
                report.reset();
                form = event.target.closest("form");
                appelloid = form.querySelector("input[type = 'hidden']").value;
                var gradelist = [];
                var examgrade;
                for(let row of this.studentcontainerbody.rows){  
                    var selectioncell = row.getElementsByTagName("td")[7];  
                    var anchor = selectioncell.getElementsByTagName("a");
                //getting the exam id
                var examid = anchor.item(0).getAttribute("examid");
                //obtaining selection table
                var selection = anchor.item(0).childNodes[0];
                //getting selected grade in table
                var grade = selection.options[selection.selectedIndex].value;
                examgrade = {
                  key : examid,
                  value : grade
              };
              if(grade != "EMPTY"){  
                gradelist.push(examgrade);
            }
        }
        var jsonString = JSON.stringify(gradelist);
        console.log(jsonString);
        makeJsonCall("POST", "UpdateGrade", jsonString, 
            function(req){
                if(req.readyState == 4){
                    var message = req.responseText;
                    console.log(message);
                    if(req.status == 200){
                        console.log(message);
                        self.show(appelloid);
                    } else if(req.status == 403){
                        window.location.href = req.getResponseHeader("Location");
                        window.sessionStorage.removeItem('username');
                        window.sessionStorage.removeItem('role');
                        window.sessionStorage.removeItem('userId');
                    } else{
                        self.alert.textContent = message;
                    }
                }
            })

    });

            this.pubblicaform.querySelector("input[type='button']").addEventListener('click', (event) => {
                var self = this;
                report.reset();
                form = event.target.closest("form");
                appelloid = form.querySelector("input[type = 'hidden']").value;
                makeCall("POST", 'Pubblica?appelloId=' + appelloid, form,
                  function(req) {
                    if (req.readyState == 4) {
                      var message = req.responseText;
                      if (req.status == 200) {
                        self.show(appelloid);
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
            });

            this.verbalizzaform.querySelector("input[type='button']").addEventListener('click', (event) => {
                var self = this;
                report.reset();
                form = event.target.closest("form"),
                appelloid = form.querySelector("input[type = 'hidden']").value;
                makeCall("POST", 'Verbalizza?appelloId=' + appelloid, form,
                  function(req) {
                    if (req.readyState == 4) {
                      var message = req.responseText;
                      console.log(message);
                      if (req.status == 200) {
                        var reportid = req.responseText;
                        self.show(appelloid);
                        report.show(reportid);
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
            });
        }
        
        this.show = function(appelloid){
            var self = this;
            makeCall("GET", "GetRegisteredStudents?appelloid=" + appelloid, null, 
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
                            self.pubblicaform.style.visibility = "visible";
                            self.verbalizzaform.style.visibility = "visible";
                            self.submitgrade.style.visibility = "visible";
                            //setting appelloid in pubblica, verbalizza and submit buttons, 
                            //in order to set, publish or verbalize exams of the correct appello.
                            self.pubblicaform.appelloid.value = appelloid;
                            self.verbalizzaform.appelloid.value = appelloid;
                            self.submitgrade.appelloid.value = appelloid;
                            self.update(registeredStudents); // self visible by closure
                            
                        } else if (req.status == 403) {
                            window.location.href = req.getResponseHeader("Location");
                            window.sessionStorage.removeItem('username');
                            window.sessionStorage.removeItem('role');
                            window.sessionStorage.removeItem('userId');
                        } else if(req.status == 400){
                            console.log(message);
                        }else {
                            self.alert.textContent = message;
                        }
                    }
                }
                );
        }

        this.update = function(registeredStudents){
            var elem, i, row, studentid, surname, name, email, corsoDiLaurea, grade, status, selection, selectioncell, anchor;
            var self = this;
            var pair;
            var studentsgrades;
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

                selectioncell = document.createElement("td");
                anchor = document.createElement("a");
                anchor.setAttribute("examid", registeredStudent.examId);

                selection = document.createElement("SELECT");
                var option0 = document.createElement("OPTION");
                option0.value = "EMPTY";
                option0.text = "";
                selection.appendChild(option0);
                var option1 = document.createElement("OPTION");
                option1.value = "ASSENTE"
                option1.text = "ASSENTE";
                selection.appendChild(option1);
                var option2 = document.createElement("OPTION");
                option2.value = "RIMANDATO"
                option2.text = "RIMANDATO";
                selection.appendChild(option2);
                var option3 = document.createElement("OPTION");
                option3.value = "RIPROVATO"
                option3.text = "RIPROVATO";
                selection.appendChild(option3);
                var option4 = document.createElement("OPTION");
                option4.value = "18"
                option4.text = "18";
                selection.appendChild(option4);
                var option5 = document.createElement("OPTION");
                option5.value = "19"
                option5.text = "19";
                selection.appendChild(option5);
                var option6 = document.createElement("OPTION");
                option6.value = "20"
                option6.text = "20";
                selection.appendChild(option6);
                var option7 = document.createElement("OPTION");
                option7.value = "21"
                option7.text = "21";
                selection.appendChild(option7);
                var option8 = document.createElement("OPTION");
                option8.value = "22"
                option8.text = "22";
                selection.appendChild(option8);
                var option9 = document.createElement("OPTION");
                option9.value = "23"
                option9.text = "23";
                selection.appendChild(option9);
                var option10 = document.createElement("OPTION");
                option10.value = "24"
                option10.text = "24";
                selection.appendChild(option10);
                var option11 = document.createElement("OPTION");
                option11.value = "25"
                option11.text = "25";
                selection.appendChild(option11);
                var option12 = document.createElement("OPTION");
                option12.value = "26"
                option12.text = "26";
                selection.appendChild(option12);
                var option13 = document.createElement("OPTION");
                option13.value = "27"
                option13.text = "27";
                selection.appendChild(option13);
                var option14 = document.createElement("OPTION");
                option14.value = "28"
                option14.text = "28";
                selection.appendChild(option14);
                var option15 = document.createElement("OPTION");
                option15.value = "29"
                option15.text = "29";
                selection.appendChild(option15);
                var option16 = document.createElement("OPTION");
                option16.value = "30"
                option16.text = "30";
                selection.appendChild(option16);
                var option17 = document.createElement("OPTION");
                option17.value = "30 E LODE"
                option17.text = "30 E LODE";
                selection.appendChild(option17);
                
                anchor.appendChild(selection);
                

                selectioncell.appendChild(anchor);
                row.appendChild(selectioncell);
                self.studentcontainerbody.appendChild(row);

            });
this.studentcontainer.style.visibility = "visible";
}
}

function Report(_report, _reportid, _reportdatetime, _reportappelloid,
    _studentreportcontainer, _studentreportcontainerbody, _closeform){

    this.report = _report;
    this.reportid = _reportid;
    this.reportdatetime = _reportdatetime;
    this.reportappelloid = _reportappelloid;
    this.studentreportcontainer = _studentreportcontainer;
    this.studentreportcontainerbody = _studentreportcontainerbody;
    this.closeform = _closeform;

    this.reset = function(){
        this.report.style.visibility = "hidden";
        this.closeform.style.visibility = "hidden";
        this.studentreportcontainerbody.style.visibility = "hidden";
    }

    this.registerEvents = function(){
            //to do register close form button
            var self = this;
            this.closeform.querySelector("input[type='button']").addEventListener('click', (e) => {
                self.reset();
            });
        }

        this.show = function(reportid){
            //to do GET call 
            var self = this;
            makeCall("GET", "GetReport?lastReportIndex=" + reportid, null, 
                function(req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var report = JSON.parse(req.responseText);
                            self.report.style.visibility = "visible";
                            self.closeform.style.visibility = "visible";
                            self.reportid.textContent = report.reportId;
                            self.reportdatetime.textContent = report.dateTime;
                            self.reportappelloid.textContent = report.appelloId;
                            self.update(report.students);  
                        } else if(req.status == 400){
                            console.log(message);
                        }else {
                            self.alert.textContent = message;
                        }
                    }
                }
                );
        }

        this.update = function(studentreportlist){
            //need to change all variables
            var elem, i, row, name, surname, studentid, grade, anchor;
            var self = this;
            this.studentreportcontainerbody.innerHTML = "";

            studentreportlist.forEach(function(studentreport){
                row = document.createElement("tr");

                name = document.createElement("td");
                name.textContent = studentreport.name;
                row.appendChild(name);

                surname = document.createElement("td");
                surname.textContent = studentreport.surname;
                row.appendChild(surname);

                studentid = document.createElement("td");
                studentid.textContent = studentreport.studentId;
                row.appendChild(studentid);

                grade = document.createElement("td");
                grade.textContent = studentreport.grade;
                row.appendChild(grade); 

                self.studentreportcontainerbody.appendChild(row);
            });

            this.studentreportcontainerbody.style.visibility = "visible";
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
                document.getElementById("id_pubblicaform"),
                document.getElementById("id_verbalizzaform"),
                document.getElementById("id_submitgrade"));

            report = new Report(document.getElementById("id_report"),
                document.getElementById("id_reportid"),
                document.getElementById("id_reportdatetime"),
                document.getElementById("id_reportappelloid"),
                document.getElementById("id_studentreportcontainer"),
                document.getElementById("id_studentreportcontainerbody"), 
                document.getElementById("id_closeform"));

            report.registerEvents();
            registeredStudents.registerEvents(report);

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
            report.reset();
        };
    }

};