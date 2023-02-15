[#ftl]
[@b.head/]
  [@b.toolbar title="交换生申请信息 添加/修改"]
    bar.addBack();
  [/@]
  [#include "../lib/step.ftl"/]
  [@displayStep ['确认个人信息','确认家庭信息','填写报名信息'] 0/]
<div class="container" style="width:95%">
  [@b.card]
    [@b.card_body]
    [#assign person=std.person/]
  [@b.form name="applyForm" action="!savePerson" theme="list" ]
    [@b.field label="姓名"]${(std.name)!}[/@]
    [@b.field label="性别"]${person.gender.name}[/@]
    [@b.field label="证件号码"]${person.code}[/@]
    [@b.field label="出生日期"]${person.birthday?string("yyyy-MM-dd")}[/@]
    [@b.select label="民族" name="person.nation.id" items=nations?sort_by("code") value=person.nation! required="true"/]
    [@b.select label="政治面貌" name="person.politicalStatus.id" items=politicalStatuses?sort_by("code") value=person.politicalStatus!
               empty="..." required="true"/]
    [@b.textfield label="籍贯" name="person.homeTown" value=person.homeTown! required="true"/]
    [@b.textfield label="出生地" name="person.birthplace" value=person.birthplace! required="true"/]

    [@b.email label="电子邮箱" name="contact.email" value=contact.email! required="true"/]
    [@b.textfield label="手机" name="contact.mobile" value=contact.mobile! required="true"/]
    [@b.textfield label="本人通讯地址" name="contact.address" value=contact.address! required="true" style="width:300px"/]

    [@b.formfoot]
      <input type="hidden" name="schemeId" value="${(scheme.id)!}"/>
      [@b.submit value="保存，下一步填写家庭信息" /]
    [/@]
  [/@]
  [/@]
[/@]
</div>
[@b.foot/]
