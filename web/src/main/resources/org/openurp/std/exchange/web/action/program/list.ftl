[#ftl]
[@b.head/]
[@b.grid items=exchangePrograms var="exchangeProgram"]
  [@b.gridbar]
    bar.addItem("${b.text("action.new")}",action.add());
    bar.addItem("${b.text("action.modify")}",action.edit());
    bar.addItem("${b.text("action.delete")}",action.remove("确认删除?"));
  [/@]
  [@b.row]
    [@b.boxcol /]
    [@b.col width="20%" property="name" title="名称"/]
    [@b.col width="30%" property="beginOn" title="有效期"]
       ${(exchangeProgram.beginOn?string("yyyy-MM-dd"))!}~${(exchangeProgram.endOn?string("yyyy-MM-dd"))!}
    [/@]
    [@b.col width="40%" title="可选学校列表"]
      [#list exchangeProgram.schools?sort_by("name") as school]${school.name!}[#if school_has_next]&nbsp;[/#if][/#list]
    [/@]
  [/@]
[/@]
[@b.form name="exchangeProgramForm" action=""/]
[@b.foot/]
