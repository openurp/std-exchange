[#ftl]
[@b.toolbar title="${apply.externStudent.std.user.name}的跨校交流"]
  bar.addBack();
[/@]
[@b.card class="card-info card-outline"]
  [@b.card_header]
         <i class="fas fa-school"></i>&nbsp;${apply.externStudent.school.name}<span style="font-size:0.8em">(${apply.externStudent.beginOn?string("yyyy-MM")}~${apply.externStudent.endOn?string("yyyy-MM")})</span>
         [#if apply.auditState!="通过"]
         <div class="btn-group">
         [@b.a onclick="return audit('${apply.id}',1)" class="btn btn-sm btn-info"]<i class="fa fa-check"></i>审核通过[/@]
         </div>
         [/#if]
         [@b.a onclick="return audit('${apply.id}',0)" class="btn btn-sm btn-warning"]<i class="fa fa-undo"></i>退回修改[/@]
         [#if apply.auditState=="通过"]<span style="font-size:0.8em;color:red">如果通过后退回修改，将会级联删除已经生成的成绩。</span>[/#if]
   [/@]
[#assign std= apply.externStudent.std/]
<table class="infoTable">
    <tr>
      <td class="title" width="10%">学号：</td>
      <td width="23%">${(std.user.code)!}</td>
      <td class="title" width="10%">姓名：</td>
      <td width="23%">${std.user.name?html}</td>
      <td class="title" width="10%">修读专业：</td>
      <td>${(apply.externStudent.majorName?html)!}</td>
    </tr>
    <tr>
      <td class="title">培养层次：</td>
      <td>${apply.externStudent.level.name}</td>
      <td class="title">教学类别：</td>
      <td>${apply.externStudent.category.name}</td>
      <td class="title">填写时间：</td>
      <td>${(apply.updatedAt?string("yyyy-MM-dd HH:mm"))!}</td>
    </tr>
    <tr>
      <td class="title">累计学分：</td>
      <td>${apply.credits}分,冲抵${apply.exemptionCredits}分</td>
      <td class="title">成绩材料：</td>
      <td>[#if transcriptPath??]
         <a href="${transcriptPath}" target="_blank"><i class="fa fa-paperclip"></i>下载附件</a>
         [#else]--[/#if]
      </td>
      <td class="title">审核状态：</td>
      <td><span class="[#if apply.auditState=="通过"]text-success[#else]text-danger[/#if]">${apply.auditState}${apply.auditOpinion!}</span></td>
    </tr>
  </table>
    [@b.grid items=grades sortable="false" var="grade" ]
        [@b.row]
            [@b.col title="序号" width="5%"]${grade_index+1}[/@]
            [@b.col property="courseName" title="课程名称" width="25%"/]
            [@b.col property="credits"  title="学分" width="5%"/]
            [@b.col property="scoreText" title="成绩" width="5%"/]
            [@b.col property="acquiredOn" title="获得年月" width="10%"]${grade.acquiredOn?string("yyyy-MM")}[/@]
            [@b.col title="免修冲抵" width="30%"]
               [#list grade.courses as c]
                 ${c.code} ${c.name} ${c.credits}分[#if c_has_next]<br>[/#if]
               [/#list]
            [/@]
            [@b.col property="remark" title="说明" width="20%"/]
        [/@]
    [/@]
[/@]
  [@b.form name="applyForm" action="!audit"]
    <input name="passed" value="" type="hidden"/>
    <input name="id" value="" type="hidden"/>
    <input name="auditOpinion" value="" type="hidden"/>
  [/@]
<script>
   function audit(id,passed){
       var form=document.applyForm;
       var msg="确定审核通过?"
       if(passed=="0"){
          var auditOpinion = prompt("请写明驳回原因：")
          if(auditOpinion){
             form['auditOpinion'].value=auditOpinion;
          }else{
            return false;
          }
          msg="确定审核不通过?"
       }
       if(confirm(msg)){
         form['id'].value=id;
         form['passed'].value=passed;
         bg.form.submit(form);
         return false;
       }else{
         return false;
       }
   }
</script>
