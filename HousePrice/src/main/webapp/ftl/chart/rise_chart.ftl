<@override name="css_body">
<link rel="stylesheet" href="${base}/assets/css/bootstrap-datepicker.css">
<title>房价涨幅走势图</title>
</@override>
<@override name="page-header">
房价涨幅走势图<small>获取具体某区县，在某周期内时间段内，平均房价走势情况</small>
</@override>
<@override name="page-inner">
<div class="row">
	<div class="col-xs-12">
		<div class="panel panel-default">
			<div class="panel-heading">
				<div class="card-title">
					<div class="title">查询条件</div>
				</div>
			</div>
			<div class="panel-body">
				<form class="form-inline" id="searchRise">
					<div class="form-group city_select">
						<label for="district">区县</label> 
						<select class="form-control prov" name="province"></select> 
						<select class="form-control city" name="city"></select>
						<select class="form-control dist" name="district"></select>
					</div>
					<div class="form-group">
						<label for="startTime">日期</label> 
						<input type="text" class="form-control datetime" name="startTime" data-date-format="yyyy-mm">
						<label for="endTime">-</label> 
						<input type="text" class="form-control datetime" name="endTime" data-date-format="yyyy-mm">
					</div>
					<button type="button" class="btn btn-info" onclick="searchRise()">查询</button>
				</form>
			</div>
		</div>
	</div>
</div>
<div class="row">
<div class="col-md-12">
	<div class="panel panel-default">
		<div class="panel-heading" id = "panelHeader">
				涨幅趋势图
		</div>
		<div class="panel-body">
			<div id="chart" style="width: 1500px; height: 500px;"></div>
		</div>
	</div>
</div>
</div>
</@override>
<@override name="script_body"> 
<script src="${base}/assets/js/echarts.js"></script>
<script src="${base}/assets/js/city/jquery.cityselect.js"></script>
<script src="${base}/assets/js/bootstrap/bootstrap-datepicker.js"></script>
<script src="${base}/assets/js/bootstrap/bootbox.min.js"></script>
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
    
    $('#searchRise').ajaxForm({
        url: '/housePrice/canvasBaseDataChart.action',
        type: 'POST',
        dataType : "json",
        success: function(responseText, statusText){
        	var data = responseText;
        	if(data.code==200){
        		refreshChart(data.data.dates , data.data.disMap);
        	}else{
        		bootbox.alert({title:'警告',message:data.msg!=null?data.msg:'数据获取异常'});
        	}
        }
    });
});

function refreshChart(dates , disMap){
	// 初始化图表标签
	var myChart = echarts.init(document.getElementById('chart'));
	var series = new Array();
	for(var k in disMap){
 	   var value = disMap[k];
 	   var avgs = new Array();
 	   for(var i = 0 ; i<value.length ;i++){
 		   avgs.push((value[i].avgPriceRise * 100).toFixed(2));
 	   }
 	   series.push({name:k, type:'line', data: avgs ,smooth:true,  label: {
 	        normal: {
 	            show: true,
 	            position: 'outside',
 	            formatter: '{c}%' // 这里是数据展示的时候显示的数据
 	        	}
 	    	}
 	    });
    } 
	$("#panelHeader").text($("select[name='district']").val() + '涨幅趋势图');
	var options = {
	    title: {
	        text: $("select[name='city']").val() + $("select[name='district']").val() + $("input[name='startTime']").val() + '至'+ $("input[name='endTime']").val() +'涨幅分步情况'
	    },
	    tooltip: {
	        trigger: 'axis'
	    },
	    legend: {
	        data:['${district!''}']
	    },
	    itemStyle : { 
	    	normal: {
	    		label : {
	    			show: true
	    			}
	    		}
	    },
	    toolbox: {
	        show: true,
	        feature: {
	            dataZoom: {
	                yAxisIndex: 'none'
	            },
	            dataView: {readOnly: false},
	            magicType: {type: ['line', 'bar']},
	            restore: {},
	            saveAsImage: {}
	        }
	    },
	    xAxis:  {
	        type: 'category',
	        boundaryGap: false,
	        data: dates
	    },
	    yAxis: {
	        type: 'value',
	        axisLabel: {
	            formatter: '{value} %'
	        },
	        min:'dataMin'
	    },
	    series: series
	};
	myChart.setOption(options);
}

//查询表单提交
function searchRise(){
	$("#searchRise").submit();
	return false;
}
</script>
</@override>
<@extends name="/index.ftl"/>  