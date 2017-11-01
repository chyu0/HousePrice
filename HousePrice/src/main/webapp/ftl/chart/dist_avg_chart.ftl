<@override name="css_body">
<link rel="stylesheet" href="${base}/assets/css/bootstrap-datepicker.css">
<title>平均房价走势图</title>
</@override>
<@override name="page-header">
平均房价走势图<small>获取具体某月分，某市区下所有区县平均房价走势情况</small>
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
				<form class="form-inline" id="searchDistAvg">
					<div class="form-group city_select">
						<label for="district">区县</label> 
						<select class="form-control prov" name="province"></select> 
						<select class="form-control city" name="city"></select>
					</div>
					<div class="form-group">
						<label for="startTime">日期</label> 
						<input type="text" class="form-control datetime" name="date">
					</div>
					<button type="button" class="btn btn-info" onclick="searchDistAvg()">查询</button>
				</form>
			</div>
		</div>
	</div>
</div>
<div class="row">
<div class="col-md-12">
	<div class="panel panel-default">
		<div class="panel-heading">
				单月各区县均价情况
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
    
    $('#searchDistAvg').ajaxForm({
        url: '/housePrice/canvasDistPriceChart.action',
        type: 'POST',
        dataType : "json",
        success: function(responseText, statusText){
        	var data = responseText;
        	if(data.code==200){
        		refreshChart(data.data.housePrice);
        	}else{
        		bootbox.alert({title:'警告',message:data.msg!=null?data.msg:'数据获取异常'});
        	}
        }
    });
});

function refreshChart(housePrice){
	// 初始化图表标签
	var myChart = echarts.init(document.getElementById('chart'));
	var series = new Array();
	var dists = new Array();
	var price = new Array();
	var distrits = housePrice.districts;
	for(var i=0 ;i < distrits.length ;i++){
		dists.push(distrits[i].district);
		price.push(distrits[i].baseData.avgPrice);
	}
	series.push({name:housePrice.date, type:'line', data: price,  label: {
        normal: {
            show: true,
            position: 'outside',
            formatter: '{c}' // 这里是数据展示的时候显示的数据
        	}
    	}
    });
	var options = {
	    title: {
	        text: housePrice.city + '各区县'+ housePrice.date +'涨幅分步情况'
	    },
	    tooltip: {
	        trigger: 'axis'
	    },
	    legend: {
	        data:[housePrice.date]
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
	            formatter: '{value} 元'
	        },
	        min:'dataMin'
	    },
	    series: series
	};
	myChart.setOption(options);
}

//查询表单提交
function searchDistAvg(){
	$("#searchDistAvg").submit();
	return false;
}
</script>
</@override>
<@extends name="/index.ftl"/>  