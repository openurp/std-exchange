[#ftl]
[@b.head/]
  [@b.toolbar title="交换生申请信息 添加/修改"]
    bar.addBack();
  [/@]
  [#include "../lib/step.ftl"/]
  [@displayStep ['填写外校学习经历','填写学习成绩','填写冲抵免修的本校课程,提交审核'] 0/]
<div class="container" style="width:95%">
  [@b.card]
    [@b.card_body]
  [@b.form name="applyForm" action="!save" theme="list" ]
    [#assign elementSTYLE = "width: 200px"/]
    [@b.field label="学号"]${(apply.std.user.code)!}[/@]
    [@b.field label="姓名"]${(apply.std.user.name)!}[/@]
    [#list 1..2 as idx]
    [@b.field label="校外学校志愿"+idx required= (idx==1)?string]
      [@b.select name="choice"+idx+".school.id" items=scheme.schools?sort_by("name") empty="...." value=(apply.getChoice(idx).school.id)! theme="html" /]
      <input type="text" name="choice${idx}.major" maxlength="100" placeholder="输入专业名" title="输入专业名" value="${(apply.getChoice(idx).major)!}" style="width:200px"/]
    [/@]
    [/#list]
    [@b.textfield label="专业排名" name="apply.rankInMajor" value=apply.rankInMajor! required="true" placeholder="名次/同专业学生人数" /]
    [@b.textarea label="个人陈述" name="apply.statements" value=apply.statements! required="true"
                 placeholder="何时受过何种奖励，有何学术论文及科研经历，政治表现、外语水平；参加交流的目的、计划以及当前已具备的条件等"
                 maxlength="500" cols="80" rows="30"/]

    [@b.formfoot]
      <input type="hidden" name="scheme.id" value="${(scheme.id)!}"/>
      [@b.submit value="提交" /]
    [/@]
  [/@]
  [/@]
[/@]
</div>
[@b.foot/]
