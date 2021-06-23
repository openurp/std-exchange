[#ftl]
[@b.head/]
  [@b.toolbar title = "<span style=\"color:blue\">" + grade.exchangeStudent.std.user.name + "（<span style=\"padding-left: 1px; padding-right: 1px\">" + grade.exchangeStudent.std.user.code + "</span>）于" + grade.acquiredOn?string("yyyy-MM-dd") +"获得“"+ grade.courseName + "”</span>课程及认定明细"]
    bar.addItem("返回", function() {
      bg.form.submit(document.searchForm);
    }, "backward.png");
  [/@]
  [#if convertedGrades?size>0]
  [@b.grid items=convertedGrades?sort_by(["course", "code"]) var="courseGrade" sortable="false"]
    [@b.gridbar]
      bg.form.addInput(action.getForm(), "grade.id", "${grade.id}");
      bar.addItem("取消", action.single("removeCourseGrade", "确认要取消冲抵吗？"), "action-edit-delete");
    [/@]
    [@b.row]
      [@b.boxcol type="radio"/]
      [@b.col title="课程代码" property="course.code"/]
      [@b.col title="课程名称" property="course.name" width="25%"/]
      [@b.col title="课程类别" property="courseType.name" width="15%"/]
      [@b.col title="学年学期" property="semester.code"]${courseGrade.semester.schoolYear} ${courseGrade.semester.name}[/@]
      [@b.col title="学分" property="course.credits"  width="6%"/]
      [@b.col title="成绩" property="score"  width="6%"]${(courseGrade.scoreText)!"--"}[/@]
      [@b.col title="绩点" property="gp"  width="6%"]${(courseGrade.gp?string("0.#"))!'--'}[/@]
      [@b.col title="修读类别" property="courseTakeType.name"/]
      [@b.col title="更新时间" property="updatedAt"  width="15%"]${courseGrade.updatedAt?string("yy-MM-dd HH:mm")}[/@]
    [/@]
  [/@]
  [/#if]
 [@b.toolbar title = "添加新的认定课程"]
    bar.addItem("认定", function() {
      var fillSize = 0;
      var planCourseIds = "";

      var form = document.gradeDistributeForm;
      $(form).find("[name^=scoreText]").each(function() {
        if ($(this).val().trim().length) {
          planCourseIds += (planCourseIds.length > 0 ? "," : "") + $(this).prev().val();
        }
      });

      bg.form.addInput(form, "planCourseIds", planCourseIds);
      bg.form.submit(form, "${b.url("!convert")}");
    }, "action-new");
  [/@]
  [@b.form name="gradeDistributeForm" action="!convert"]
  <input type="hidden" name="grade.id" value="${grade.id}"/>
  <div class="grid" style="border:0.5px solid #006CB2">
  <table class="gridtable">
    <thead class="gridhead">
      <tr>
        <th width="10%">课程代码</th>
        <th width="20%">课程名称</th>
        <th width="15%">课程类别</th>
        <th width="50px">学分</th>
        <th width="130px">开课学期</th>
        <th width="100px">记录方式</th>
        <th width="80px">成绩(分数)</th>
        <th width="60px">修读类别</th>
        <th width="60px">考核方式</th>
      </tr>
    </thead>
    <tbody>
      [#list planCourses?sort_by(["course","name"]) as planCourse]
      <tr class="${(0 == planCourse_index % 2)?string("griddata-even", "griddata-odd")}">
        <td>${planCourse.course.code}</td>
        <td>${planCourse.course.name}</td>
        <td>${planCourse.group.courseType.name}</td>
        <td>${planCourse.course.credits}</td>
        <td title="第${planCourse.terms}学期 ">${semesters.get(planCourse).schoolYear} ${semesters.get(planCourse).name} 学期</td>
        <td>
          <select name="gradingMode.id${planCourse.id}" style="width: 100px" onchange="displayScore(this.value,${planCourse.id})">
            [#list gradingModes as gradingMode]
            <option value="${gradingMode.id}"[#if 1 == gradingMode.id] selected[/#if]>${gradingMode.name}</option>
            [/#list]
          </select>
        </td>
        <td>
          <input type="hidden" name="planCourse.id${planCourse.id}" value="${planCourse.id}"/><input type="text" name="scoreText${planCourse.id}" value="" maxlength="5" style="width: 50px"/>
          <div id="score${planCourse.id}" style="display:none">
                          （<input type="text" name="score${planCourse.id}" value="" maxlength="10" style="width: 50px"/>）
          </div>
        </td>
        <td>${ExemptionType.name}</td>
        <td>${planCourse.course.examMode.name}</td>
      </tr>
      [/#list]
    </tbody>
  </table>
  </div>
  [/@]

  <script>
    var gradingModes={};
    [#list gradingModes as gradingMode]
      gradingModes['g${gradingMode.id}']=${gradingMode.numerical?string('1','0')}
    [/#list]
    function displayScore(gradingModeId,planCourseId){
      if(gradingModes['g'+gradingModeId]=='1'){
        document.getElementById('score'+planCourseId).style.display="none";
      }else{
        document.getElementById('score'+planCourseId).style.display="";
      }
    }
  </script>
[@b.foot/]
