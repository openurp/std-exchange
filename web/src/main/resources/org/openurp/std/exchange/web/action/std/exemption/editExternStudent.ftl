[#ftl]
[@b.head/]
  [@b.toolbar title="校外学习经历添加/修改"]
    bar.addBack();
  [/@]
  [#include "../lib/step.ftl"/]
  [@displayStep ['填写外校学习经历','填写学习成绩','填写冲抵免修的本校课程,提交审核'] 0/]
<div class="container" style="width:95%">
  [@b.card]
    [@b.card_body]
  [@b.form name="externStudentForm" action="!saveExternStudent" theme="list" ]
    [#assign elementSTYLE = "width: 200px"/]
    [@b.field label="学号"]${(externStudent.std.user.code)!}[/@]
    [@b.field label="姓名"]${(externStudent.std.user.name)!}[/@]
    [@b.field label="校外学校" ]
      [@b.select name="externStudent.school.id" items=schools?sort_by("name") empty="...手工添加..." value=(externStudent.school.id)! theme="html" /]
      或<input type="text" name="newSchool" maxlength="100" placeholder="列表中没有,手动添加学校" title="列表中没有,手动添加学校" style=elementSTYLE/]
    [/@]
    [@b.select label="培养层次" name="externStudent.level.id" items=levels required="true" value=(externStudent.level.id)! style=elementSTYLE/]
    [@b.textfield label="外校专业" name="externStudent.majorName" value=(externStudent.majorName)! required="true" maxlength="100" style=elementSTYLE/]
    [@b.startend label="就读时间" name="externStudent.beginOn,externStudent.endOn" start=(externStudent.beginOn)! end=(externStudent.endOn)! required="true"/]
    [@b.formfoot]
      <input type="hidden" name="externStudent.id" value="${(externStudent.id)!}"/>
      <input type="hidden" name="project.id" value="${(externStudent.std.project.id)!}"/>
      <input type="hidden" name="externStudent.category.id" value="${externStudent.std.project.category.id}"/>
      [@b.submit value="保存,进入填写学习成绩" /]
    [/@]
  [/@]
  [/@]
[/@]
</div>
[@b.foot/]
