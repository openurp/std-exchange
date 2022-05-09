[#ftl]
[@b.head/]
<div class="container" style="width:95%" id="apply_info">
<nav class="navbar navbar-default" role="navigation">
  <div class="container-fluid">
    <div class="navbar-header">
        <a class="navbar-brand" href="#"><i class="fas fa-graduation-cap"></i>交换生报名</a>
    </div>
    <ul class="nav navbar-nav navbar-right">
        <li>
        [@b.form class="navbar-form navbar-left" role="search" action="!editPerson"]
            [#list schemes as p]
            [@b.a class="btn btn-sm btn-info" href="!editPerson?schemeId="+p.id]<i class="fas fa-plus"></i>添加[/@]
            [/#list]
        [/@]
        </li>
    </ul>
    </div>
</nav>
[@b.messages/]
  [#list applies as apply]
  [@b.form name="removeApplyForm_"+apply.scheme.id  action="!remove?schemeId="+ apply.scheme.id + "&_method=delete"][/@]
  [#assign title]
     <i class="fas fa-school"></i> &nbsp;${apply.scheme.name} ${apply.scheme.program.name}
     [#if apply.scheme.opened]
      <span style="font-size:0.8em" title="报名时间" data-toggle="tooltip">(${apply.scheme.beginAt?string("yyyy-MM-dd HH:mm")}~${apply.scheme.endAt?string("yyyy-MM-dd HH:mm")})</span>
      [@b.a href="!editPerson?schemeId="+apply.scheme.id class="btn btn-sm btn-info"]<i class="far fa-edit"></i>修改[/@]
      [@b.a href="!remove?schemeId="+apply.scheme.id  onclick="return removeApply(${apply.scheme.id});" class="btn btn-sm btn-warning"]<i class="fas fa-times"></i>删除[/@]
      &nbsp;
      [@b.a class="btn btn-success" href="!download?schemeId=" +apply.scheme.id role="button" target="_blank"]<i class="fas fa-download"></i>下载申请表[/@]
     [/#if]
  [/#assign]
  [@b.card class="card-info card-outline"]
     [@b.card_header]
      ${title}
     [/@]
     [#include "info.ftl"/]
  [/@]
  [/#list]
</div>
<script>
   function removeApply(id){
       if(confirm("确定删除?")){
         return bg.form.submit(document.getElementById("removeApplyForm_"+id));
       }else{
         return false;
       }
   }
   bg.load(["bootstrap"],function () {
      $('#apply_info [data-toggle="tooltip"]').tooltip()
    })
</script>
[@b.foot/]
