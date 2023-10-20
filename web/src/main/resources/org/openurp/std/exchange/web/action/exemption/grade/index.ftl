[#ftl]
[@b.head/]
  [@b.toolbar title="外校成绩管理"/]
<div class="search-container">
  <div class="search-panel">
        [@b.form title="ui.searchForm" name="searchForm" action="!search" target="externGrades" theme="search"]
          <input type="hidden" name="orderBy" value="externGrade.updatedAt desc"/>
          [@b.textfields names="externGrade.exchangeStudent.std.code;学号,externGrade.exchangeStudent.std.name;姓名"/]
          [@b.textfields names="externGrade.exchangeStudent.school.name;学校,externGrade.exchangeStudent.majorName;外校专业,externGrade.courseName;外校课程,externGrade.credits;外校学分"/]
          [@b.datepicker id="fromAt" label="录入起时" name="fromAt" format="yyyy-MM-dd" maxDate="#F{$dp.$D(\\'toAt\\')}"/]
          [@b.datepicker id="toAt" label="录入止时" name="toAt" format="yyyy-MM-dd" minDate="#F{$dp.$D(\\'fromAt\\')}"/]
          [@b.select label="是否认定" name="hasCourse" items={ "1": "是", "0": "否" } empty="..."/]
        [/@]
  </div>
  <div class="search-list">
     [@b.div id="externGrades"/]
  </div>
</div>
  <script>
    $(function() {
      $(document).ready(function() {
        bg.form.submit(document.searchForm, "${b.url("!search")}", "externGrades");
      });
    });
  </script>
[@b.foot/]
