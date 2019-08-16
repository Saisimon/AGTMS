<!DOCTYPE html>
<html>
	<head>
		<link href="https://cdn.bootcss.com/twitter-bootstrap/4.3.1/css/bootstrap.min.css" rel="stylesheet"></link>
		<style type="text/css">
			body {
				font-family: '${font!"Arial Unicode MS"}';
				font-size: 12px!important;
			}
			table {
				max-width: 690px!important;
			}
			tr {
				max-width: 130px!important;
			}
			td, th {
				padding: .25rem!important;
				max-width: 130px!important;
				word-wrap: break-word!important;
				word-break: normal!important;
			}
		</style>
	</head>
	<body>
		<table class="table table-striped table-bordered">
		<#list datas as data>
			<tr>
			<#if data_index == 0>
			<#list data as val>
				<th class="text-center">${val}</th>
			</#list>
			<#else>
			<#list data as val>
				<td>${val!""}</td>
			</#list>
			</#if>
			</tr>
		</#list>
		</table>
	</body>
</html>