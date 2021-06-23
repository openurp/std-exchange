[#ftl]
[@b.head/]
<div class="search-container">
  <div class="search-panel">
    [@b.form name="exchangeSchoolSearchForm" action="!search" target="exchangeSchoollist" title="ui.searchForm" theme="search"]
      [@b.textfields names="exchangeSchool.code;代码"/]
      [@b.textfields names="exchangeSchool.name;名称"/]
      <input type="hidden" name="orderBy" value="exchangeSchool.code"/>
    [/@]
  </div>
  <div class="search-list">
    [@b.div id="exchangeSchoollist" href="!search?orderBy=exchangeSchool.code"/]
  </div>
</div>
[@b.foot/]
