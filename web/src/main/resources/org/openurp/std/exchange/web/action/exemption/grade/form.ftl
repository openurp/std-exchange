[#ftl]
[@b.head/]
  [@b.toolbar title="校外成绩添加、修改"]
    bar.addBack();
  [/@]
  [@b.form name="exchangeGradeForm" action="!save" target="exchangeGrades" theme="list"]
    [#assign elementSTYLE = "width: 200px"/]
    [#if (exchangeGrade.id)?exists]
      [@b.field label="学习经历"]<span style="display: inline-block;">${(exchangeGrade.exchangeStudent.std.user.code)!} ${(exchangeGrade.exchangeStudent.std.user.name)!} ${(exchangeGrade.exchangeStudent.school.name)!} ${(exchangeGrade.exchangeStudent.beginOn?string('yyyy-MM'))}~${(exchangeGrade.exchangeStudent.endOn?string("yyyy-MM"))}</span>[/@]
    [#else]
      [@b.field label="学号"]
       <input name="stdCode" style=elementSTYLE placeholder="输入学号后，点击页面空白处，即可获取该学生信息">
       <span id="stdName"></span>
      [/@]
      [@b.select label="学习经历" id="exchangeStudentSelect" name="exchangeGrade.exchangeStudent.id"  style="width:400px" required="true"/]
    [/#if]
    [@b.textfield label="外校课程" name="exchangeGrade.courseName" value=(exchangeGrade.courseName)! required="true" maxlength="100" style=elementSTYLE/]
    [@b.textfield label="外校学分" name="exchangeGrade.credits" value=(exchangeGrade.credits)! required="true" maxlength="5" check="match('number')" style=elementSTYLE/]
    [@b.textfield label="外校得分" name="exchangeGrade.scoreText" value=(exchangeGrade.scoreText)! required="true" maxlength="5" style=elementSTYLE/]
    [@b.datepicker label="获得日期" name="exchangeGrade.acquiredOn" value=(exchangeGrade.acquiredOn?string('yyyy-MM-dd'))! format="yyyy-MM-dd" required="true" style=elementSTYLE/]
    [@b.textfield label="备注" name="exchangeGrade.remark" value=(exchangeGrade.remark)! required="false" maxlength="100" style="width:300px"/]
    <div style="margin-left: 50px;color: blue">说明：一个学生相同获得日期相同课程只能出现一次。</div>
    [@b.formfoot]
      <input type="hidden" name="exchangeGrade.id" value="${(exchangeGrade.id)!}"/>
      [@b.submit value="提交"/]
    [/@]
  [/@]
  [#if !(exchangeGrade.id)?exists]
  <script>
    $(function() {
      function init(form) {
        var formObj = $(form);
        var stdNameObj = formObj.find("#stdName");

        formObj.find("[name=stdCode]").blur(function() {
          var thisObj = $(this);
          thisObj.parent().find(".error").remove();
          thisObj.parent().next().find(".error").remove();
          stdNameObj.empty();
          var code = thisObj.val().trim();
          if (code.length == 0) {
            throwError(thisObj.parent(), "请输入一个有效的学号");
            stdNameObj.html("<br>");
          } else {
            $.ajax({
              "type": "POST",
              "url": "${b.url("!loadStudent")}",
              "async": false,
              "dataType": "json",
              "data": {
                "q": code
              },
              "success": function(data) {
                $('#exchangeStudentSelect').empty();
                if(data.length>0){
                 for(var i=0;i< data.length;i++){
                   $('#exchangeStudentSelect').append($('<option>', {
                        value: data[i].value,
                        text : data[i].text
                   }));
                 }
                }else{
                  throwError(thisObj.parent().next(), "请输入一个存在外校学习经历的学号，谢谢！");
                  stdNameObj.html("<br>");
                  thisObj.val("");
                }
              }
            });
          }
        });

        formObj.find(":submit").click(function() {
          var errObj = formObj.find("[name=stdCode]").parent().find(".error");
          if (errObj.size()) {
            formObj.find("[name=stdCode]").parent().append(errObj);
          }
        });
      }

      function throwError(parentObj, msg) {
        var errObj = parentObj.find(".error");
        if (!errObj.size()) {
          errObj = $("<label>");
          errObj.addClass("error");
          parentObj.append(errObj);
        }
        errObj.text(msg);
      }

      $(document).ready(function() {
        init(document.exchangeGradeForm);
      });
    });
  </script>
  [/#if]
[@b.foot/]
