<@override name="css_body">
<link rel="stylesheet" href="${base}/assets/css/bootstrap-datepicker.css">
<title>平均房价走势图</title>
</@override>
<@override name="page-header">
平均房价走势图<small>获取某区县，在某段周期类的平均房价走势情况</small>
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
				<form class="form-inline" id="searchAvg">
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
					
					<div class="form-group">
						<label for="showMaxMinPrice">是否显示最高最低价</label> 
						<div class="radio">
  							<label>
    							<input type="radio" name="showMax" value="0" checked>
    							不显示
  							</label>
						</div>
						<div class="radio">
  							<label> 
    							<input type="radio" name="showMax" value="1">
    							显示
  							</label>
						</div>
					 </div>
					 
					<button type="button" class="btn btn-info" onclick="searchAvg()">查询</button>
				</form>
			</div>
		</div>
	</div>
</div>
<div class="row">
<div class="col-md-12">
	<div class="panel panel-default">
		<div class="panel-heading" id="panelHeader">
				平均房价分步情况
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
    
    $('#searchAvg').ajaxForm({
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
	var showMax = $("input:radio[name='showMax']:checked").val();
	// 初始化图表标签
	var myChart = echarts.init(document.getElementById('chart'));
	var series = new Array();
	var legend = new Array();
	for(var k in disMap){
 	   var value = disMap[k];
 	   var avgs = new Array();
 	   var maxs = new Array();
 	   var mins = new Array();
 	   for(var i = 0 ; i<value.length ;i++){
 		   avgs.push(value[i].avgPrice);
 		   maxs.push(value[i].maxPrice);
 		   mins.push(value[i].minPrice);
 	   }
 	   legend.push('平均价');
 	   series.push({name:'平均价', type:'line', data: avgs , label: {
 	        normal: {
 	            show: true,
 	            position: 'outside',
 	            formatter: '{c}' // 这里是数据展示的时候显示的数据
 	        	}
 	    	}
 	    });
 	   if(showMax == "1"){
 		  legend.push('最高价');
 		  series.push({name:'最高价', type:'line', data: maxs , label: {
 	 	        normal: {
 	 	            show: true,
 	 	            position: 'outside',
 	 	            formatter: '{c}' // 这里是数据展示的时候显示的数据
 	 	        	}
 	 	    	}
 	 	    });
 		  legend.push('最低价');
 		  series.push({name:'最低价', type:'line', data: mins , label: {
  	        normal: {
  	            show: true,
  	            position: 'outside',
  	            formatter: '{c}' // 这里是数据展示的时候显示的数据
  	        	}
  	    	}
  	    	});
 	   }
 	   
    } 
	$("#panelHeader").text($("select[name='district']").val() + '平均房价折线图');
	var options = {
	    title: {
	        text: $("select[name='district']").val() + $("input[name='startTime']").val() + '至'+ $("input[name='endTime']").val() + '平均房价分步情况'
	    },
	    tooltip: {
	        trigger: 'axis'
	    },
	    legend: {
	        data:legend
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
	            formatter: '{value} 元'
	        },
	        min:'dataMin'
	    },
	    series: series
	};
	myChart.setOption(options);
}

//查询表单提交
function searchAvg(){
	$("#searchAvg").submit();
	return false;
}
</script>
</@override>
<@extends name="/index.ftl"/>  