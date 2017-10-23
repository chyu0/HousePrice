<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>echarts.js案例一</title>
    <script type="text/javascript" src='/common/js/echart/echarts.js'></script>
</head>
<body>
    <div id="chart" style="width:1500px;height:1500px;"></div>
</body>
<script type="text/javascript">
    <#if avgPriceList?? && avgPriceList?size gt 0>
    	<#assign dates ="">
    	<#list dateList as date>
    		<#assign dates = dates + date + ",">
    	</#list>
 		// 初始化图表标签
    	var myChart = echarts.init(document.getElementById('chart'));
    	var dates = "${(dates?substring(0,dates?length - 1))!''}";
    	var options = {
    	    title: {
    	        text: '${city!''}各区县${startTime!''}至${endTime!''}平均房价分步情况'
    	        //subtext: '纯属虚构'
    	    },
    	    tooltip: {
    	        trigger: 'axis'
    	    },
    	    legend: {
    	        data:['最高气温','最低气温']
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
    	        data: dates.split(",")
    	    },
    	    yAxis: {
    	        type: 'value',
    	        axisLabel: {
    	            formatter: '{value} 元'
    	        },
    	        min:'dataMin'
    	    },
    	    series: [
    	       <#assign count = 0>
    	       <#list disMap?keys as key>
    	       	<#assign vals ="">
    	       	<#list disMap[key] as value>
    	       		<#assign vals = vals + value.avgPrice?c + ",">
    	       	</#list>
    	       	<#assign count = count + 1>
    	        {
    	            name:"${key}",
    	            type:'line',
    	            data: "${(vals?substring(0,vals?length - 1))!''}".split(",")
    	            /*,markPoint: {
    	                data: [
    	                    {type: 'max', name: '最大值'},
    	                    {type: 'min', name: '最小值'}
    	                ]
    	            },
    	            markLine: {
    	                data: [
    	                    {type: 'average', name: '平均值'}
    	                ]
    	            }*/
    	        }<#if count != disMap?size>,</#if>
    	        </#list>
    	    ]
    	};
    	myChart.setOption(options);
    </#if>
    
</script>
</html>