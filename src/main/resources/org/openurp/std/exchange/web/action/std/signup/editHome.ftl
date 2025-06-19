[#ftl]
[@b.head/]
  [@b.toolbar title="交换生申请信息 添加/修改"]
    bar.addBack();
  [/@]
  [#include "../lib/step.ftl"/]
  [@displayStep ['确认个人信息','确认家庭信息','填写报名信息'] 1/]
  <style>
  fieldset.listset li > label.title{
    min-width:120px;
  }
  </style>
<div class="container" style="width:95%">
  [@b.card]
    [@b.card_body]
    [#assign person=std.person/]
  [@b.form name="applyForm" action="!saveHome" theme="list" ]
    [@b.textfield label="家庭地址" name="home.address" value=home.address! required="true" style="width:300px"/]
    [@b.textfield label="家庭主要成员 (1)" name="relation1.name" value=relation1.name! required="true" placeholder="姓名"/]
    [@b.select label="与本人关系" name="relation1.relationship.id" items=relationships?sort_by("code") value=relation1.relationship! required="true"/]
    [@b.textfield label="单位、职务" name="relation1.duty" value=relation1.duty! required="true" maxlength="200" style="width:300px"/]
    [@b.textfield label="联系电话" name="relation1.phone" value=relation1.phone! required="true"/]

    [@b.textfield label="家庭主要成员 (2)" name="relation2.name" value=relation2.name!  placeholder="姓名"/]
    [@b.select label="与本人关系" name="relation2.relationship.id" items=relationships?sort_by("code") value=relation2.relationship!/]
    [@b.textfield label="单位、职务" name="relation2.duty" value=relation2.duty! maxlength="200" style="width:300px"/]
    [@b.textfield label="联系电话" name="relation2.phone" value=relation2.phone!/]

    [@b.formfoot]
      <input type="hidden" name="schemeId" value="${(scheme.id)!}"/>
      [@b.submit value="保存，下一步填写报名信息" /]
    [/@]
  [/@]
  [/@]
[/@]
</div>
[@b.foot/]
