[#ftl]
[@b.head/]
[@b.toolbar  title="交换生报名配置"]bar.addBack();[/@]
[@b.tabs]
  [@b.form action=b.rest.save(exchangeScheme) theme="list" ]
    [@b.textfield name="exchangeScheme.name" label="年度批次" value=exchangeScheme.name! required="true" /]
    [@b.select label="计划" name="exchangeScheme.program.id" items=programs value=exchangeScheme.program! required="true"/]
    [@b.textfield name="exchangeScheme.grades" label="年级" value="${exchangeScheme.grades!}" required="true" comment="多个年级可用半角逗号隔开"/]
    [@b.number name="exchangeScheme.choiceCount" label="志愿数" value=exchangeScheme.choiceCount required="true" comment="最多几个志愿"/]
    [@b.textfield name="exchangeScheme.minGpa" label="最低绩点" value=exchangeScheme.minGpa required="true"/]

    [@b.startend label="有效期限"
      name="exchangeScheme.beginAt,exchangeScheme.endAt" required="true,true"
      start=exchangeScheme.beginAt end=exchangeScheme.endAt format="yyyy-MM-dd HH:mm"/]
    [@b.select label="学校" multiple="true" name="schoolId" items=schools values=exchangeScheme.schools required="true"/]
    [@b.formfoot]
      [@b.reset/]&nbsp;&nbsp;[@b.submit value="action.submit"/]
    [/@]
  [/@]
[/@]
[@b.foot/]