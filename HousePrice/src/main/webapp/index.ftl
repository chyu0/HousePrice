<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
	<meta content="" name="description" />
    <meta content="webthemez" name="author" />
    <!-- Bootstrap Styles-->
    <link href="${base}/assets/css/bootstrap.css" rel="stylesheet" />
    <!-- FontAwesome Styles-->
    <link href="${base}/assets/css/font-awesome.css" rel="stylesheet" />
    <!-- Morris Chart Styles-->
    <link href="${base}/assets/js/morris/morris-0.4.3.min.css" rel="stylesheet" />
    <!-- Custom Styles-->
    <link href="${base}/assets/css/custom-styles.css" rel="stylesheet" />
    <!-- Google Fonts-->
    <link href='https://fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css' />
    <link rel="stylesheet" href="${base}/assets/js/Lightweight-Chart/cssCharts.css"> 
    <link rel="stylesheet" href="${base}/assets/css/bootstrap-select.min.css">
    <@block name="css_body"><title>房价趋势系统</title></@block>
</head>

<body>
    <div id="wrapper">
    	<#include "/common/macro/header.ftl">
        <#include "/common/macro/menu.ftl">
      
		<div id="page-wrapper">
		  	<div class="header"> 
                       <h1 class="page-header">
                            <@block name="page-header">房价趋势系统</@block>
                        </h1>
<!-- 					<ol class="breadcrumb">
					  <li><a href="#">Home</a></li>
					  <li><a href="#">Dashboard</a></li>
					  <li class="active">Data</li>
					</ol>  -->
									
			</div>
            <div id="page-inner">
            	<@block name="page-inner"></@block>
            </div>
            <!-- /. PAGE INNER  -->
        </div>
        <!-- /. PAGE WRAPPER  -->
    </div>
    <!-- /. WRAPPER  -->
	<#include "/common/macro/script.ftl">
	<@block name="script_body"></@block>
</body>

</html>