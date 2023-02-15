[#ftl]
   <table class="infoTable">
      <tr>
        <td class="title" width="10%">学号:</td>
        <td class="content">${(apply.std.code)!}</td>
        <td class="title" width="10%" >姓名:</td>
        <td class="content">${(apply.std.name)!}</td>
        <td class="title" rowspan="6" style="text-align:left">照片<br>
        正面、免冠、白底证件照;jpg格式;300x420 像素;分辨率不小于300 dpi;大小不超过200k
        </td>
        <td rowspan="6">
        <img src="${avatar_url}"/>
         <input type="button" name="photoButton" value="更新" class="btn btn-info btn-sm"
               onclick="popupCommonWindow('${avatar_upload_url}','studentFileImportWin', 550, 450);"/>
        </td>
      </tr>
      [#assign person=apply.std.person/]
      <tr>
        <td class="title" width="10%" >性别:</td>
        <td class="content">${(apply.std.person.gender.name)!}</td>
        <td class="title" >出生日期:</td>
        <td class="content">${(person.birthday?string('yyyy-MM-dd'))!}</td>
      </tr>
      <tr>
        <td class="title" >民族:</td>
        <td class="content">${(person.nation.name)!}</td>
        <td class="title" >政治面貌:</td>
        <td class="content">${(person.politicalStatus.name)!}</td>
      </tr>
      <tr>
        <td class="title" >籍贯:</td>
        <td class="content">${(person.homeTown)!}</td>
        <td class="title" >出生地:</td>
        <td class="content">${(person.birthplace)!}</td>
      </tr>
      <tr>
        <td class="title" >手机:</td>
        <td class="content">${(apply.mobile)!}</td>
        <td class="title" >身份证号:</td>
        <td class="content">${(person.code)!}</td>
      </tr>
      <tr>
        <td class="title" >邮箱:</td>
        <td class="content">${(apply.email)!}</td>
        <td class="title" >联系地址:</td>
        <td class="content">${(apply.address)!}</td>
      </tr>
      <tr>
        <td colspan="6" style="text-align:center">学籍、成绩和志愿信息</td>
      </tr>
      <tr>
        <td class="title" >院系:</td>
        <td class="content">${(apply.std.state.department.name)!}</td>
        <td class="title" >专业:</td>
        <td class="content">${(apply.std.state.major.name)!}</td>
        <td class="title" >年级:</td>
        <td class="content">${(apply.std.state.grade)!}</td>
      </tr>
      <tr>
        <td class="title" >绩点:</td>
        <td class="content">${(apply.gpa)!}</td>
        <td class="title" >获得总学分:</td>
        <td class="content">${(apply.credits)!}</td>
        <td class="title" >专业排名:</td>
        <td class="content">${(apply.rankInMajor)!}</td>
      </tr>
      <tr>
        <td class="title">第一志愿:</td>
        <td class="content" [#if !apply.getChoice(2?int)??]colspan="3"[/#if]>${(apply.getChoice(1?int).school.name)!} ${(apply.getChoice(1?int).major)!} </td>
        <td class="title" >第二志愿:</td>
        <td class="content" colspan="3">[#if apply.getChoice(2?int)??]${(apply.getChoice(2?int).school.name)!} ${(apply.getChoice(2?int).major)!}[/#if]</td>
      </tr>
      <tr>
        <td colspan="6" style="text-align:center">家庭主要成员</td>
      </tr>
      [#list relations as rl]
      <tr>
        <td class="title" >姓名:</td>
        <td class="content">${rl.name}(${rl.relationship.name})</td>
        <td class="title" >工作单位、职务:</td>
        <td class="content">${(rl.duty)!}</td>
        <td class="title" >联系电话:</td>
        <td class="content">${(rl.phone)!}</td>
      </tr>
      [/#list]
      <tr>
        <td class="title">家庭地址:</td>
        <td colspan="5">${home.address!}</td>
      </tr>
       <td colspan="6" style="text-align:center">何时受过何种奖励，有何学术论文及科研经历，政治表现、外语水平；参加交流的目的、计划以及当前已具备的条件等</td>
      <tr>
        <td class="title">个人陈述:</td>
        <td colspan="5">${apply.statements}</td>
      </tr>
    </table>
<script>
function popupCommonWindow(url){
  var name = '';
  if (popupCommonWindow.arguments.length > 1){
    name = popupCommonWindow.arguments[1];
  }

    var width = 500;
    var height = 600;
    if (popupCommonWindow.arguments.length > 2)
       width = popupCommonWindow.arguments[2];
    if (popupCommonWindow.arguments.length > 3)
       height = popupCommonWindow.arguments[3];

    var win = window.open(url, name, 'scrollbars=yes,width='+width+',height='+height+',status=no,resizable=yes,depended=yes');
  win.self.resizeTo(width, height);
  win.self.moveTo((screen.width-width)/2, (screen.height-height)/2);
  win.self.focus();
}
</script>
