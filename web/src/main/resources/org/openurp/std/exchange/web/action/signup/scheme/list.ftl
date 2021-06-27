[#ftl]
[@b.head/]
[@b.grid items=exchangeSchemes var="exchangeScheme"]
  [@b.gridbar]
    bar.addItem("${b.text("action.new")}",action.add());
    bar.addItem("${b.text("action.modify")}",action.edit());
    bar.addItem("${b.text("action.delete")}",action.remove("确认删除?"));
  [/@]
  [@b.row]
    [@b.boxcol /]
    [@b.col width="15%" property="name" title="名称"/]
    [@b.col width="15%" property="grades" title="年级"/]
    [@b.col width="15%" property="beginAt" title="开始时间"]${(exchangeScheme.beginAt?string("yyyy-MM-dd HH:mm"))!}[/@]
    [@b.col width="15%" property="endAt" title="截至时间"]${(exchangeScheme.endAt?string("yyyy-MM-dd HH:mm"))!}[/@]
    [@b.col width="40%" title="可选学校列表"][#list exchangeScheme.schools?sort_by("name") as school]${school.name!}[#if school_has_next]&nbsp;[/#if][/#list][/@]
  [/@]
[/@]
[@b.form name="exchangeSchemeForm" action=""/]
[@b.foot/]
