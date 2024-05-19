[#ftl]
[@b.head/]
  [@b.toolbar title="交换生申请信息 添加/修改"]
    bar.addBack();
  [/@]
  [#include "../lib/step.ftl"/]
  [@displayStep ['确认个人信息','确认家庭信息','填写报名信息'] 2/]
  <style>
  fieldset.listset li > label.title{
    min-width:130px;
  }
  </style>
<div class="container" style="width:95%">
  [@b.card]
    [@b.card_body]
    [#assign person=std.person/]
  [@b.form name="applyForm" action="!saveApply" theme="list" ]
    [#assign optionNames=["--","(第一志愿)","(第二志愿)"]/]
    [@b.field label="学号"]${(std.code)!}[/@]
    [@b.field label="年级"]${std.state.grade}[/@]
    [@b.field label="院系"]${std.state.department.name}[/@]
    [@b.field label="专业"]${std.state.major.name} ${(std.state.direction.name)!}[/@]
    [@b.textfield label="平均绩点" name="apply.gpa" value=apply.gpa! required="true" readOnly="true" /]
    [@b.textfield label="累计获得总学分" name="apply.credits" value=apply.credits! required="true" readOnly="true" /]
    [@b.textfield label="成绩排名" name="apply.rankInMajor" value=apply.rankInMajor! required="true"
                  placeholder="名次/同专业学生人数" comment="名次/同专业学生人数"/]
    [#list 1..2 as idx]
    [@b.field label="申请学校"+optionNames[idx] required= (idx==1)?string]
      [@b.select name="choice"+idx+".school.id" items=scheme.schools?sort_by("name") empty="...." value=(apply.getChoice(idx).school.id)! theme="html" /]
      <input type="text" name="choice${idx}.major" maxlength="100" placeholder="输入专业名" title="输入专业名" value="${(apply.getChoice(idx).major)!}" style="width:200px"/]
    [/@]
    [/#list]
    [@b.textarea label="个人陈述" name="apply.statements" value=apply.statements! required="true"
                 placeholder="何时受过何种奖励，有何学术论文及科研经历，政治表现、外语水平；参加交流的目的、计划以及当前已具备的条件等"
                 maxlength="1000" cols="80" rows="30" comment="最多不超过700字"/]
    [@b.formfoot]
      <input type="hidden" name="schemeId" value="${(scheme.id)!}"/>
      [@b.submit value="提交" /]
    [/@]
  [/@]
  [/@]
[/@]
</div>
[@b.foot/]
