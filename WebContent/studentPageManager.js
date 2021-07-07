{
    let courseList, examDate, examResult, pageOrchestrator = new PageOrchestrator();

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
        makeCall("GET", "GetStudentCourses", null, 
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
                    examResult.reset();
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
            makeCall("GET", "GetStudentExams?courseid=" + courseid, null, 
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
                anchor.setAttribute("appelloid", examdate.appelloId);
                anchor.addEventListener("click", (e) => {
                    // dependency via module parameter
                    examResult.reset();
                    examResult.show(e.target.getAttribute("appelloId")); // the list must know the details container
                }, false);
                anchor.href = "#";
                row.appendChild(linkcell);
                self.detailcontainerbody.appendChild(row);
            });
            this.detailcontainer.style.visibility = "visible";
        }

    }

    function ExamResult(options){
        this.alert = options['alert'];
        this.resultcontainer = options['resultcontainer'];
        this.studentname = options['studentname'];
        this.studentsurname = options['studentsurname'];
        this.studentid = options['studentid'];
        this.studentemail = options['studentemail'];
        this.studentgrade = options['studentgrade'];
        this.rifiutaform = options['rifiutaform'];
        this.resultmessage = options['resultmessage'];

        this.reset = function(){
            this.resultcontainer.style.visibility = "hidden";
            this.studentname.style.visibility = "hidden";
            this.studentsurname.style.visibility = "hidden";
            this.studentid.style.visibility = "hidden";
            this.studentemail.style.visibility = "hidden";
            this.studentgrade.style.visibility = "hidden";
            this.rifiutaform.style.visibility = "hidden";
            this.resultmessage.style.visibility = "hidden";
        }

        this.registerEvents = function(){
            this.rifiutaform.querySelector("input[type='button']").addEventListener('click', (event) => {
                var self = this;
                form = event.target.closest("form");
                examid = form.querySelector("input[type = 'hidden'][name = 'examid']").value;
                appelloid = form.querySelector("input[type = 'hidden'][name = 'appelloid']").value;
                console.log(examid);
                makeCall("POST", 'RifiutaEsame?examid=' + examid, form, 
                    function(req) {
                        if (req.readyState == 4) {
                          var message = req.responseText;
                          console.log(message);
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
                })
            });
        }


        this.show = function(appelloid){
            var self = this;
            makeCall("GET", "GetExamResult?appelloid=" + appelloid, null, 
                function(req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        console.log(message);
                        if (req.status == 200) {
                            var exam = JSON.parse(req.responseText);
                            console.log(exam);
                            self.resultcontainer.style.visibility = "visible";
                            self.update(exam);
                            switch(exam.status){
                                case 'NONINSERITO':
                                    self.resultmessage.textContent = "Voto non ancora definito";
                                    self.resultcontainer.style.visibility = "hidden";
                                    self.resultmessage.style.visibility = "visible";
                                    self.studentname.style.visibility = "hidden";
                                    self.studentsurname.style.visibility = "hidden";
                                    self.studentid.style.visibility = "hidden";
                                    self.studentemail.style.visibility = "hidden";
                                    self.studentgrade.style.visibility = "hidden";
                                    break;
                                case 'INSERITO':
                                    self.resultmessage.textContent = "Voto non ancora definito";
                                    self.resultcontainer.style.visibility = "hidden";
                                    self.resultmessage.style.visibility = "visible";
                                    self.studentname.style.visibility = "hidden";
                                    self.studentsurname.style.visibility = "hidden";
                                    self.studentid.style.visibility = "hidden";
                                    self.studentemail.style.visibility = "hidden";
                                    self.studentgrade.style.visibility = "hidden";
                                    break;    
                                case 'PUBBLICATO':
                                    self.studentname.style.visibility = "visible";
                                    self.studentsurname.style.visibility = "visible";
                                    self.studentemail.style.visibility = "visible";
                                    self.studentgrade.style.visibility = "visible";
                                    self.studentid.style.visibility = "visible";
                                    if(exam.grade == "RIMANDATO" || exam.grade == "RIPROVATO"){
                                        self.rifiutaform.style.visibility = "hidden";
                                    } else {
                                        self.rifiutaform.examid.value = exam.examId;
                                        self.rifiutaform.appelloid.value = exam.appelloId;
                                       self.rifiutaform.style.visibility = "visible";
                                    }
                                    break;
                                case 'RIFIUTATO':
                                    self.studentname.style.visibility = "visible";
                                    self.studentsurname.style.visibility = "visible";
                                    self.studentid.style.visibility = "visible";
                                    self.studentemail.style.visibility = "visible";
                                     self.studentgrade.style.visibility = "visible";
                                    self.rifiutaform.style.visibility = "hidden";
                                    self.resultmessage.textContent = "Il voto è stato rifiutato";
                                    self.resultmessage.style.visibility = "visible";   
                                    break;
                                case "VERBALIZZATO":
                                    self.studentname.style.visibility = "visible";
                                    self.studentsurname.style.visibility = "visible";
                                    self.studentid.style.visibility = "visible";
                                    self.studentemail.style.visibility = "visible";
                                    self.studentgrade.style.visibility = "visible";
                                    self.rifiutaform.style.visibility = "hidden";
                                    break;    
                            }
                        } else if (req.status == 403) {
                            window.location.href = req.getResponseHeader("Location");
                            window.sessionStorage.removeItem('username');
                            window.sessionStorage.removeItem('role');
                            window.sessionStorage.removeItem('userId');
                        } else {
                            self.alert.textContent = message;
                        }
                    }
                });
        }

        this.update = function(exam){
            this.studentname.textContent = exam.studentName;
            this.studentsurname.textContent = exam.studentSurname;
            this.studentid.textContent = exam.studentId;
            this.studentemail.textContent = exam.studentEmail;
            this.studentgrade.textContent = exam.grade;
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

            examDate = new ExamDate(document.getElementById("id_alert"),
                document.getElementById("id_detailcontainer"),
                document.getElementById("id_detailcontainerbody"));

            examResult = new ExamResult({
                alert : document.getElementById("id_alert"),
                resultcontainer : document.getElementById("id_resultcontainer"),
                studentname : document.getElementById("id_studentname"),
                studentsurname : document.getElementById("id_studentsurname"),
                studentid : document.getElementById("id_studentid"),
                studentemail : document.getElementById("id_studentemail"),
                studentgrade : document.getElementById("id_studentgrade"),
                rifiutaform : document.getElementById("id_rifiutaform"),
                resultmessage : document.getElementById("id_resultmessage")});

            examResult.registerEvents();

            document.querySelector("a[href='Logout']").addEventListener('click', () => {
                window.sessionStorage.removeItem('role');
                window.sessionStorage.removeItem('userId');
                window.sessionStorage.removeItem('username');
            })

        };

        this.refresh = function(currentCourse){  //currentCourse null at start
            alertContainer.textContent = " ";
            courseList.reset();
            examDate.reset();
            courseList.show(function(){
             courseList.autoclick(currentCourse);
         });
            examResult.reset();
        };
    }



};