[#ftl]
[@b.head/]
[@b.toolbar title="报名信息"/]
<div class="search-container">
  <div class="search-panel">
      [@b.form name="exchangeApplySearchForm" action="!search" target="exchangeApplylist" title="ui.searchForm" theme="search"]
          [@b.select name="exchangeApply.scheme.id" label="报名批次" items=schemes /]
          [@b.textfields names="exchangeApply.std.code;学号"/]
          [@b.textfields names="exchangeApply.std.name;姓名"/]
          [@b.textfields names="exchangeApply.std.state.department.name;院系"/]
          [@b.select name="school.id" label="学校" items=schools empty="..."/]
          <input type="hidden" name="orderBy" value="exchangeApply.updatedAt desc"/>
      [/@]
  </div>
  <div class="search-list">
    [#if schemes?size > 0]
      [@b.div id="exchangeApplylist" href="!search?exchangeApply.scheme.id="+ schemes?first.id+"&orderBy=exchangeApply.updatedAt desc"/]
    [#else]
      <div id="exchangeApplylist">没有找到报名批次，请先制定报名批次</div>
    [/#if]
  </div>
</div>
[@b.foot/]
