[#ftl]
[@b.head/]

<div class="container" style="width:95%">

<nav class="navbar navbar-default" role="navigation">
  <div class="container-fluid">
    <div class="navbar-header">
        <a class="navbar-brand" href="#"><i class="fas fa-graduation-cap"></i>交换生报名</a>
    </div>
    <ul class="nav navbar-nav navbar-right">
        <li>
        </li>
    </ul>
    </div>
</nav>
[@b.messages/]
<div class="jumbotron">
    <div class="container">
        <h2>交换生报名</h2>
        <p>欢迎进入交换生报名，还没有申请您的申请信息。[#if schemes?size>0]现在就申请。[/#if]</p>
        <p>
         [#list schemes as scheme]
         [@b.a class="btn btn-lg btn-info" role="button" href="!editPerson?schemeId="+scheme.id]<i class="fas fa-plus"></i>添加[/@]&nbsp;
         [/#list]
        </p>
    </div>
</div>
</div>
[@b.foot/]
