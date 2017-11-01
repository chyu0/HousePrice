<@override name="css_body">
<link rel="stylesheet" href="${base}/assets/css/bootstrap-datepicker.css">
<title>房价涨幅走势图</title>
</@override>
<@override name="page-header">
房价涨幅走势图<small>获取具体周期内，某市区下所有区县房价涨幅情况</small>
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
				<form class="form-inline" id="searchDistRise">
					<div class="form-group city_select">
						<label for="district">区县</label> 
						<select class="form-control prov" name="province"></select> 
						<select class="form-control city" name="city"></select>
					</div>
					<div class="form-group">
						<label for="startTime">日期</label> 
						<input type="text" class="form-control datetime" name="startTime" data-date-format="yyyy-mm">
						<label for="endTime">-</label> 
						<input type="text" class="form-control datetime" name="endTime" data-date-format="yyyy-mm">
					</div>
					<button type="button" class="btn btn-info" onclick="searchDistRise()">查询</button>
				</form>
			</div>
		</div>
	</div>
</div>
<div class="row">
<div class="col-md-12">
	<div class="panel panel-default">
		<div class="panel-heading" id="panelHeader">
				各区县涨幅情况
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
    
    $('#searchDistRise').ajaxForm({
        url: '/housePrice/canvasDistRiseChart.action',
        type: 'POST',
        dataType : "json",
        success: function(responseText, statusText){
        	var data = responseText;
        	if(data.code==200){
        		refreshChart(data.data.priceRise);
        	}else{
        		bootbox.alert({title:'警告',message:data.msg!=null?data.msg:'数据获取异常'});
        	}
        }
    });
});

function refreshChart(priceRiseList){
	var city = $("select[name='city']").val();
	// 初始化图表标签
	var myChart = echarts.init(document.getElementById('chart'));
	var series = new Array();
	var dists = new Array();
	var rise = new Array();
	for(var i=0 ;i < priceRiseList.length ;i++){
		dists.push(priceRiseList[i].value);
		rise.push((priceRiseList[i].score*100).toFixed(2));
	}
	series.push({name:city, type:'line', data: rise  ,  label: {
        normal: {
            show: true,
            position: 'outside',
            formatter: '{c}%' // 这里是数据展示的时候显示的数据
        	}
    	}
    });
	$("#panelHeader").text(city + '各区县涨幅情况');
	var options = {
	    title: {
	        text: city + '各区县涨幅分步情况'
	    },
	    tooltip: {
	        trigger: 'axis'
	    },
	    legend: {
	        data:[city]
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
	        data: dists
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
function searchDistRise(){
	$("#searchDistRise").submit();
	return false;
}
</script>
</@override>
<@extends name="/index.ftl"/>  