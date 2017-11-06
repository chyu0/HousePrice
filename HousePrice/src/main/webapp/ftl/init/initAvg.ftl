<@override name="css_body">
<link rel="stylesheet" href="${base}/assets/css/bootstrap-datepicker.css">
<link href="${base}/assets/js/dataTables/dataTables.bootstrap.css" rel="stylesheet" />
<title>初始化均价数据</title>
</@override>
<@override name="page-header">
均价数据初始化<small>初始化某段周期类某市所有区县数据</small>
</@override>
<@override name="page-inner">
<div class="row">
	<div class="col-xs-12">
		<div class="panel panel-default">
			<div class="panel-heading">
				<div class="card-title">
					<div class="title">初始化条件</div>
				</div>
			</div>
			<div class="panel-body">
				<form class="form-inline" id="initAvg">
					<div class="form-group city_select">
						<label for="district">区县</label> 
						<select class="form-control prov" name="province"></select> 
						<select class="form-control city" name="city"></select>
					</div>
					<div class="form-group">
						<label for="startTime">日期</label> 
						<input type="text" class="form-control datetime" name="startDate" data-date-format="yyyy-mm">
						<label for="endTime">-</label> 
						<input type="text" class="form-control datetime" name="endDate" data-date-format="yyyy-mm">
					</div>
					<button type="button" class="btn btn-info" onclick="initAvg()">初始化</button>
				</form>
			</div>
		</div>
	</div>
</div>

<div class="row">
	<div class="col-md-12">
        <div class="panel panel-default">
             <div class="panel-heading">
                   	失败记录
             </div>
             <div class="panel-body">
                <div class="table-responsive">
                   <table class="table table-striped table-bordered table-hover" id="errorTable">
                         <thead>
                              <tr>
                                 <th>序号</th>
                                 <th>日期</th>
                                 <th>错误码</th>
                                 <th>错误信息</th>
                              </tr>
                         </thead>
                         <tbody id="errorTbody">
                         	
                         </tbody>
                   </table>
                 </div>
              </div>
        </div>
    </div>
</div>

</@override>
<@override name="script_body"> 
<script src="${base}/assets/js/city/jquery.cityselect.js"></script>
<script src="${base}/assets/js/bootstrap/bootstrap-datepicker.js"></script>
<script src="${base}/assets/js/bootstrap/bootbox.min.js"></script>
<script src="${base}/assets/js/dataTables/jquery.dataTables.js"></script>
<script src="${base}/assets/js/dataTables/dataTables.bootstrap.js"></script>
<script type="text/javascript">
$(function() {
    //初始化城市信息
    $(".city_select").citySelect({prov:"湖北",city:"武汉"});
    //日期控件初始化
    $('.datetime').datepicker({
    	format: 'yyyy-mm',  
        weekStart: 1,  
        autoclose: true,     
        startView: 2,  
        maxViewMode: 2,
        minViewMode:1,
        forceParse: false,  
        language: 'cn'  
    });
    refreshTable();
    $('#initAvg').ajaxForm({
        url: '/init/initData.action',
        type: 'POST',
        dataType : 'json',
        success: function(responseText, statusText){
        	var data = responseText;
        	if(data.code==200){
        		var failResult = data.data;
        		$("#errorTable").dataTable().fnDestroy();
        		if(failResult != null && failResult.length>0){
        			var tbodyhtml = "";
        			for(var i=0 ;i<failResult.length;i++){
						tbodyhtml = tbodyhtml + "<tr><td>" + (i+1) + "</td><td>" + failResult[i].date;
						tbodyhtml = tbodyhtml + "</td><td>" + failResult[i].code + "</td><td>" + failResult[i].message + "</td></tr>";
        			}
        			$("#errorTbody").html(tbodyhtml);
        			refreshTable();
        		}else{
        			bootbox.alert({title:'警告',message:'初始化全部成功'});
        		}
        	}else{
        		bootbox.alert({title:'警告',message:data.msg!=null?data.msg:'数据获取异常'});
        	}
        }
    });
});

function refreshTable(){
	$('#errorTable').dataTable({
    	// 汉化语言
        'language': {
          'lengthMenu': '每页 _MENU_ 条记录',
          'zeroRecords': '没有找到记录',
          'info': '显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项',
          'infoFiltered': '(从 _MAX_ 条记录过滤)',
          'paginate': {
            'first': '首页',
            'last': '尾页',
            'next': '下一页',
            'previous': '上一页'
          }
        },
        // 分页dom结构（搜索框，分页组件）
        'dom': 't<"bottom"<"pull-left info"l><"pull-right"p>>'
    });
}

//表单提交
function initAvg(){
	$("#initAvg").submit();
	return false;
}
</script>
</@override>
<@extends name="/index.ftl"/>  