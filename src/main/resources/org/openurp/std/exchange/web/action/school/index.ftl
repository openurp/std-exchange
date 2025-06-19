[#ftl]
[@b.head/]
[#include "../nav.ftl"/]
<div class="search-container">
  <div class="search-panel">
    [@b.form name="externSchoolSearchForm" action="!search" target="externSchoollist" title="ui.searchForm" theme="search"]
      [@b.textfields names="externSchool.code;代码"/]
      [@b.textfields names="externSchool.name;名称"/]
      <input type="hidden" name="orderBy" value="externSchool.code"/>
    [/@]
  </div>
  <div class="search-list">
    [@b.div id="externSchoollist" href="!search?orderBy=externSchool.code"/]
  </div>
</div>
[@b.foot/]
