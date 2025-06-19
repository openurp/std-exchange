[#ftl]
[@b.head/]
  [@b.toolbar title="校外学习成绩添加/修改"]
    bar.addBack();
  [/@]
[#include "../lib/step.ftl"/]
[@displayStep ['填写外校学习经历','填写学习成绩','填写冲抵免修的本校课程,提交审核'] 1/]

<div class="container" style="width:95%">
[@b.card]
  [@b.card_body]
  [@b.form name="externStudentForm" action="!saveGrades" theme="list"  enctype="multipart/form-data"  onsubmit="checkAttachment"]
    [@b.field label="学号"]${(externStudent.std.code)!} ${(externStudent.std.name)!}[/@]
    [@b.field label="校外学校"]${externStudent.school.name}(${externStudent.beginOn?string("yyyy-MM")}~${externStudent.endOn?string("yyyy-MM")})[/@]
    [@b.field label="成绩证明材料"]
     <input type="file" name="transcript" >
     [#if apply.transcriptPath??]已上传[/#if]
    [/@]
    [#list grades?sort_by("id") as m]
    [@b.field label="课程"+(m_index+1)]
          <input name="grade_${m_index+1}.id" type="hidden" value="${m.id}">
          <input name="grade_${m_index+1}.courseName" maxlength="100" style="width:300px" value="${m.courseName}">
          <input name="grade_${m_index+1}.credits" type="number" maxlength="2" style="width:80px"  value="${m.credits}">
          <input name="grade_${m_index+1}.scoreText"  maxlength="10"  style="width:70px"  value="${m.scoreText}">
          <input name="grade_${m_index+1}.acquiredOn" style="width:100px"  class="Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'${externStudent.beginOn?string('yyyy-MM-dd')}',maxDate:'${externStudent.endOn?string('yyyy-MM-dd')}'})" value="${m.acquiredOn?string('yyyy-MM-dd')}">
          <input name="grade_${m_index+1}.remark" style="width:200px" maxlength="50" value="${m.remark!}">
    [/@]
    [/#list]
    [#assign start=grades?size+1]
    [#assign maxLines= 20/]
    [#if grades?size > 9]
      [#assign maxLines = grades?size + 1 /]
    [/#if]
    [#list start..maxLines as i]
    [@b.field label="课程"+i]
      <input name="grade_${i}.courseName" value="" maxlength="100" style="width:300px" placeholder="课程名称" title="课程名称">
      <input name="grade_${i}.credits" value="" type="number" maxlength="2"  style="width:80px" placeholder="学分" title="学分">
      <input name="grade_${i}.scoreText" maxlength="10" value="" style="width:70px" placeholder="分数/等第" title="成绩">
      <input name="grade_${i}.acquiredOn" value="" class="Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'${externStudent.beginOn?string('yyyy-MM-dd')}',maxDate:'${externStudent.endOn?string('yyyy-MM-dd')}'})" style="width:100px" placeholder="获得年月" title="获得年月">
      <input name="grade_${i}.remark" style="width:200px" maxlength="50" value="" placeholder="说明" title="说明">
    [/@]
    [/#list]
    [@b.formfoot]
      <input type="hidden" name="externStudent.id" value="${(externStudent.id)!}"/>
      [@b.submit value="保存,进入选择免修课程"/]
    [/@]
  [/@]
  [/@]
[/@]
<script>
   function checkAttachment(form){
    [#if !apply.transcriptPath??]
    if("" == form['transcript'].value){
      alert("请上传成绩相关证明材料");
      return false;
    }
    [/#if]
    return true;
  }
</script>
</div>
