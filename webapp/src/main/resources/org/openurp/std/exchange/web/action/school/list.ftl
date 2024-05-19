[#ftl]
[@b.head/]
[@b.grid items=externSchools var="externSchool"]
  [@b.gridbar]
    bar.addItem("${b.text("action.new")}",action.add());
    bar.addItem("${b.text("action.modify")}",action.edit());
    bar.addItem("${b.text("action.delete")}",action.remove("确认删除?"));
  [/@]
  [@b.row]
    [@b.boxcol /]
    [@b.col width="20%" property="code" title="代码"]${externSchool.code}[/@]
    [@b.col width="55%" property="name" title="名称"/]
    [@b.col width="10%" property="beginOn" title="生效日期"]${externSchool.beginOn!}[/@]
    [@b.col width="10%" property="endOn" title="失效日期"]${externSchool.endOn!}[/@]
  [/@]
[/@]
[@b.foot/]
