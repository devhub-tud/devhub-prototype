#parse("header.tpl") #parse("menu.tpl")

<div class="container">
	<div class="content row">
		<div class="span3">
			<ul class="nav nav-tabs nav-stacked affix-top">
				<li><a href="#jvm">Virtual Machine<i
						class="icon-chevron-right right"></i></a></li> #foreach ($mapEntry in
				$customMetrics.keySet())
				<li><a href="#$mapEntry">$mapEntry<i
						class="icon-chevron-right right"></i></a></li> #end
			</ul>
		</div>
		<div class="span9">
			<section id='jvm'>
				<div class="page-header">
					<h2>Java Virtual Machine</h2>
				</div>
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
			</section>
			#foreach ($mapEntry in $customMetrics.entrySet())
			<section id="$mapEntry.key">
				<div class="page-header">
					<h2>$mapEntry.key</h2>
				</div>
				<table class="values table table-striped table-bordered"
					style="margin-top: 12px;">
					<tbody>$mapEntry.value
					</tbody>
				</table>
			</section>
			#end
		</div>
	</div>
</div>
#parse("footer.tpl")
