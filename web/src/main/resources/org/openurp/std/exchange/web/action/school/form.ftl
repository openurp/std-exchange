[#ftl]
[@b.head/]
[@b.toolbar title="修改出版社"]bar.addBack();[/@]
[@b.tabs]
  [@b.form action=b.rest.save(externSchool) theme="list"]
    [@b.textfield name="externSchool.code" label="代码" value="${externSchool.code!}" required="true" maxlength="20"/]
    [@b.textfield name="externSchool.name" label="名称" value="${externSchool.name!}" required="true" maxlength="20"/]
    [@b.startend label="有效期"
      name="externSchool.beginOn,externSchool.endOn" required="true,false"
      start=externSchool.beginOn end=externSchool.endOn format="date"/]
    [@b.textfield name="externSchool.remark" label="备注" value="${externSchool.remark!}" maxlength="3"/]
    [@b.formfoot]
      [@b.reset/]&nbsp;&nbsp;[@b.submit value="action.submit"/]
    [/@]
  [/@]
[/@]
[@b.foot/]
