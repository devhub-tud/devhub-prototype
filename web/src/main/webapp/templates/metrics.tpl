#parse("header.tpl") #parse("menu.tpl")

<div class="container">
	<div class="content row">
		<div class="span3">
			<ul class="nav nav-tabs nav-stacked affix-top">
				<li><a href="#M">Virtual Machine<i
						class="icon-chevron-right right"></i></a></li>
				<li><a href="#Users">Users<i
						class="icon-chevron-right right"></i></a></li>
			</ul>
		</div>
		<div class="span9">
			<table class="values table table-striped table-bordered"
				style="margin-top: 12px;">
				<tbody>
					#foreach ($mapEntry in $vmMetrics.entrySet())
					<tr>
						<td>$mapEntry.key</td>
						<td>$mapEntry.value</td>
					</tr>
					#end					
				</tbody>
			</table>
		</div>
	</div>
</div>
#parse("footer.tpl")
