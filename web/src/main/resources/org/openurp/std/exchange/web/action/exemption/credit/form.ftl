[#ftl]
[@b.head/]
  [@b.toolbar title="免修学分上限"]
    bar.addBack();
  [/@]
  [@b.form name="exemptionCreditForm" action=b.rest.save(exemptionCredit) theme="list"]
    [#if (exemptionCredit.id)?exists]
      [@b.field label="学号"]<span style="display: inline-block;">${(exemptionCredit.std.user.code)!} ${(exemptionCredit.std.user.name)!}[/@]
    [#else]
      [@b.field label="学号"]
       <input name="stdCode"  placeholder="输入学号后，点击页面空白处，即可获取该学生信息">
       <input type="hidden" id="stdId" name="exemptionCredit.std.id" value="${(exemptionCredit.std.id)!}"/>
       <span id="stdName"></span>
      [/@]
    [/#if]
    [@b.textfield label="上限" name="exemptionCredit.maxValue" value=(exemptionCredit.maxValue)! required="true" maxlength="4" /]
    [@b.field label="已经免修"]
      ${exemptionCredit.credits!"--"}
    [/@]
    [@b.formfoot]
      [@b.submit value="提交"/]
    [/@]
  [/@]
[#if !(exemptionCredit.id)?exists]
  <script>
    $(function() {
      function init(form) {
        var formObj = $(form);
        var stdNameObj = formObj.find("#stdName");

        formObj.find("[name=stdCode]").blur(function() {
          var thisObj = $(this);
          thisObj.parent().find(".error").remove();
          thisObj.parent().next().find(".error").remove();
          stdNameObj.empty();
          var code = thisObj.val().trim();
          if (code.length == 0) {
            throwError(thisObj.parent(), "请输入一个有效的学号");
            stdNameObj.html("<br>");
          } else {
            $.ajax({
              "type": "POST",
              "url": "${b.url("!loadStudent")}",
              "async": false,
              "dataType": "json",
              "data": {
                "q": code
              },
              "success": function(data) {
                  var dataObj = eval(data);
                  if(dataObj.length>0){
                    $("#stdId").parent().find(".error").remove();
                    $("#stdName").html(dataObj[0].name);
                    $("#stdId").val(dataObj[0].id);
                  }else{
                    throwError(thisObj.parent(), "请输入一个有效的学号");
                  }
              }
            });
          }
        });

        formObj.find(":submit").click(function() {
          var errObj = formObj.find("[name=stdCode]").parent().find(".error");
          formObj.find("[name=stdCode]").parent().append(errObj);
        });
      }

      function throwError(parentObj, msg) {
        var errObj = parentObj.find(".error");
          errObj = $("<label>");
          errObj.addClass("error");
          parentObj.append(errObj);
        errObj.text(msg);
      }

      $(document).ready(function() {
        init(document.exemptionCreditForm);
      });
    });
  </script>
  [/#if]
[@b.foot/]
