[#ftl/]
[@b.head/]
[@b.toolbar title=Parameters["display"]!("导入")]
    bar.addItem("模板下载","downloadTemplate()","action-download");
[/@]
[@b.messages/]
<br/>
[@b.form action="!importData" theme="list" enctype="multipart/form-data"]
    [@b.field label="文件目录"]
    <input type="file" name="importFile" id="importFile"/>
    [/@]
    [@b.formfoot]
      [@b.submit value="提交" onsubmit="validateExtendName"/]
      <input type="reset" value="重置" class="buttonStyle"/>
    [#list Parameters?keys as key]
          [#if key!='method']<input type="hidden" name="${key}" value="${Parameters[key]}" />[/#if]
     [/#list]
    [/@]
[/@]
<div style="color:red;font-size:2">上传文件中的所有信息均要采用文本格式。对于日期和数字等信息也是一样。</div>
 [@b.form name="downloadForm" action="!downloadTemplate" target="_blank"]
     [#list Parameters?keys as key]
          [#if key!='method']<input type="hidden" name="${key}" value="${Parameters[key]}" />[/#if]
     [/#list]
 [/@]
<script type="text/javascript">
    function downloadTemplate(){
        bg.form.submit(document.downloadForm);
    }
    function validateExtendName(form){
        var value = form.importFile.value;
        if(value == ""){
          alert("请选择文件");
          return false;
        }
        var index1 = value.indexOf(".xlsx");
        var index2 = value.indexOf(".xlsx");
        if(index1 < 0 || index2 < 0){
            alert("只能接受以xls或者xlsx结尾的电子表格");
            return false;
        }
        return true;
    }
</script>
[@b.foot/]
