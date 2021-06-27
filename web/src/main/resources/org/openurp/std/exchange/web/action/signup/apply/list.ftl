[#ftl]
[@b.head/]
[@b.grid items=exchangeApplies var="exchangeApply"]
  [@b.gridbar]
    bar.addItem("查看",action.info());
    bar.addItem("删除",action.remove());
    bar.addItem("下载",action.single('download',null,null,"_blank"))
    bar.addItem("${b.text("action.export")}",
       action.exportData("std.user.code:学号,std.user.name:姓名,std.person.gender.name:性别,std.person.birthday:出生日期," +
       "std.person.nation.name:民族,std.person.politicalStatus.name:政治面貌,std.person.homeTown:籍贯,std.person.birthplace:出生地," +
       "std.person.code:身份证号,mobile:电话,address:联系地址,email:电子信箱,gpa:绩点,credits:获得总学分," +
       "rankInMajor:专业排名,std.state.department.name:学院,std.state.major.name:专业,std.state.grade:年级,choice1:志愿1," +
       "choice2:志愿2,updatedAt:报名时间",null,'fileName=交换生报名信息一览表'));
  [/@]
  [@b.row]
    [@b.boxcol /]
    [@b.col width="9%" property="std.user.code" title="学号"/]
    [@b.col width="8%" property="std.user.name" title="姓名"][@b.a href="!info?id=${exchangeApply.id}"]${exchangeApply.std.user.name}[/@][/@]
    [@b.col width="13%" property="std.state.department.name" title="院系"/]
    [@b.col width="6%" property="gpa" title="绩点"/]
    [@b.col width="26%" title="第一志愿"]${exchangeApply.getChoice(1?int).school.name} ${exchangeApply.getChoice(1?int).major}[/@]
    [@b.col width="26%" title="第二志愿"]${(exchangeApply.getChoice(2?int).school.name)!} ${(exchangeApply.getChoice(2?int).major)!} [/@]
    [@b.col width="7%" property="updatedAt" title="报名时间"]${exchangeApply.updatedAt?string("yy-MM-dd")}[/@]
  [/@]
[/@]
[@b.foot/]
