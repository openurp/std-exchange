[#ftl]
[@b.head/]
  [@b.grid items=exemptionCredits var="exemptionCredit"]
    [@b.gridbar]
      bar.addItem("新增",action.add());
      bar.addItem("修改",action.edit());
      bar.addItem("导入",action.method('importForm'))
      bar.addItem("删除",action.remove())
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col title="学号" property="std.user.code" width="15%"/]
      [@b.col title="姓名" property="std.user.name" width="10%"/]
      [@b.col title="已冲抵学分" property="exempted"  width="15%"/]
      [@b.col title="上限" property="maxValue" width="15%"]
          [#if exemptionCredit.maxValue ==0 ]--[#else]${exemptionCredit.maxValue}[/#if]
      [/@]
      [@b.col title="说明" property="remark"  width="20%"/]
      [@b.col title="最近更新" property="updatedAt" width="20%"]
        ${exemptionCredit.updatedAt?string("yyyy-MM-dd HH:mm")}
      [/@]
    [/@]
  [/@]
[@b.foot/]
