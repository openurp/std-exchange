[#ftl]
[@b.head/]
[@b.toolbar title="冲抵学分上限"/]
<div class="search-container">
  <div class="search-panel">
      [@b.form name="searchForm" action="!search" title="ui.searchForm" target="listFrame" theme="search"]
     [#include "searchForm.ftl"/]
     [/@]
  </div>
  <div class="search-list">
     [@b.div id="listFrame"/]
  </div>
</div>
 <script>
  var form = document.searchForm;
  function search(pageNo,pageSize,orderBy){
    form.target="listFrame";
    form.action="${b.url('!search')}";
    bg.form.submit(form)
  }
  search();
 </script>
[@b.foot/]
