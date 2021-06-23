[#ftl]
[@b.head/]
  [@b.grid items=exchangeGrades var="exchangeGrade"]
    [@b.gridbar]
      bar.addItem("${b.text("action.new")}", action.add());
      bar.addItem("${b.text("action.modify")}", action.edit());
      bar.addItem("成绩认定", action.single("convertList"), "action-update");
      bar.addItem("${b.text("action.delete")}", action.remove("确认要删除吗？"));
      [#if exchangeGrades.totalItems gt 10000]
        bar.addItem("导出", function() {
          alert("导出数据每次不能超过10000条，建议分批导出。");
        });
      [#else]
        bar.addItem("导出", action.exportData("exchangeStudent.std.user.code:学号,exchangeStudent.std.user.name:姓名,exchangeStudent.school.name:校外学校,exchangeStudent.level.name:培养层次,exchangeStudent.category.name:教育类别,exchangeStudent.majorName:外校专业,courseName:外校课程,scoreText:外校得分,credits:外校学分,acquiredOn:获得日期,updatedAt:录入时间"));
      [/#if]
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col title="学号" property="exchangeStudent.std.user.code"  width="13%"/]
      [@b.col title="姓名" property="exchangeStudent.std.user.name" width="10%"/]
      [@b.col title="专业" property="exchangeStudent.majorName"  width="15%"]
         <span title="${exchangeGrade.exchangeStudent.school.name}">${exchangeGrade.exchangeStudent.majorName}</span>
      [/@]
      [@b.col title="课程" property="courseName" width="20%"/]
      [@b.col title="得分" property="scoreText" width="5%"/]
      [@b.col title="学分" property="credits" width="5%"/]
      [@b.col title="获得日期" property="acquiredOn" width="7%"]${exchangeGrade.acquiredOn?string("yyyy-MM")}[/@]
      [@b.col title="免修" sortable="false" width="25%"]
        [#if exchangeGrade.courses?size >0 ]
        <span style="font-size:0.8em">[#list exchangeGrade.courses as c]${c.name} ${c.credits}分 [#if c_has_next]<br>[/#if][/#list]</span>
        [#else]--[/#if]
      [/@]
    [/@]
  [/@]
[@b.foot/]
