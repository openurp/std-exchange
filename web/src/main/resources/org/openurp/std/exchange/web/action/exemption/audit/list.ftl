[#ftl]
[@b.head/]
  [@b.grid items=applies var="apply"]
    [@b.gridbar]
      //bar.addItem("新增",action.add());
      bar.addItem("审核...", action.single("info"));
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col title="学号" property="externStudent.std.user.code" width="13%"/]
      [@b.col title="姓名" property="externStudent.std.user.name" width="8%"/]
      [@b.col title="交流学校" property="school.name"  width="24%"]
       ${(apply.externStudent.school.name)}
      [/@]
      [@b.col title="学习专业" property="externStudent.majorName" width="15%"/]
      [@b.col title="学习时间"  width="15%"]
        ${apply.externStudent.beginOn?string("yyyy-MM")}~${apply.externStudent.endOn?string("yyyy-MM")}
      [/@]
      [@b.col title="冲抵学分" width="6%" property="exemptionCredits"/]
      [@b.col title="状态" width="5%" property="auditState"]
        <span title="${apply.auditOpinion!}">${apply.auditState}</span>
      [/@]
      [@b.col title="申请时间" width="9%" property="updatedAt"]${apply.updatedAt?string("MM-dd")}[/@]
    [/@]
  [/@]
[@b.foot/]
