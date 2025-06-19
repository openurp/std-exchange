[#ftl]
[@b.head/]
<div class="container" style="width:95%">
<nav class="navbar navbar-default" role="navigation">
  <div class="container-fluid">
    <div class="navbar-header">
        <a class="navbar-brand" href="#"><i class="fas fa-graduation-cap"></i>跨校交流学习经历</a>
    </div>
    <ul class="nav navbar-nav navbar-right">
        <li>
        [#if exemptionCredit??]
        [@b.form class="navbar-form navbar-left" role="search" action="!editNew"]
            [@b.a class="btn btn-sm btn-info" href="!editExternStudent?project.id="+project.id]<i class="fas fa-plus"></i>添加(上限${exemptionCredit.maxValue})[/@]
        [/@]
        [#else]
         没有你的冲抵上限，尚不能添加。
        [/#if]
        </li>
    </ul>
    </div>
</nav>

  [#list externStudents as externStudent]
  [@b.form name="removeExternForm_"+externStudent.id  action="!remove?externStudent.id="+externStudent.id + "&project.id="+externStudent.std.project.id + "&_method=delete"][/@]
  [#assign title]
     <i class="fas fa-school"></i> &nbsp;${externStudent.school.name}<span style="font-size:0.8em">(${externStudent.beginOn?string("yyyy-MM")}~${externStudent.endOn?string("yyyy-MM")})</span>
  [#if applies.get(externStudent)??]
     [#assign apply=applies.get(externStudent)/]
     [#if apply.status =="通过"]审核通过[#else]
     <div class="btn-group">
     [@b.a href="!editExternStudent?externStudent.id="+externStudent.id class="btn btn-sm btn-info"]<i class="far fa-edit"></i>修改[/@]
     [@b.a href="!editApplies?externStudent.id="+externStudent.id class="btn btn-sm btn-info"]<i class="far fa-edit"></i>匹配冲抵[/@]
     </div>
       [@b.a href="!remove?apply.id="+apply.id + "&project.id=" + apply.externStudent.std.project.id  onclick="return removeExtern(this);" class="btn btn-sm btn-warning"]<i class="fas fa-times"></i>删除申请[/@]
     [/#if]
  [#else]
     [@b.a href="!remove?externStudent.id="+externStudent.id + "&project.id=" + externStudent.std.project.id  onclick="return removeExtern(this);" class="btn btn-sm btn-warning"]<i class="fas fa-times"></i>删除学习经历[/@]
     [@b.a href="!editGrades?externStudent.id="+externStudent.id class="btn btn-sm btn-info"]<i class="far fa-edit"></i>开始申请[/@]
  [/#if]
  [/#assign]
  [@b.card class="card-info card-outline"]
     [@b.card_header]
      ${title}
     [/@]
      [#if applies.get(externStudent)??][#include "info.ftl"/][#else][#include "info_noapply.ftl"/][/#if]
  [/@]
  [/#list]
</div>
<script>
   function removeExtern(elem){
       if(confirm("确定删除?")){
         return bg.Go(elem,null)
       }else{
         return false;
       }
   }
</script>
[@b.foot/]
