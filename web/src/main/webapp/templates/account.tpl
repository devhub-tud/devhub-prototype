#parse("header.tpl") #parse("menu.tpl")

<div class="container">
	<div class="content row">
		<div class="span3">
			<div style="margin: 0px 10px 40px 0px;">
				<img src="${user.getGravatarUrl(512)}" class="img-polaroid" />
				<div style="margin: 0px 5px;">
					<h4>$user.getDisplayName()</h4>
				</div>
			</div>
			<ul class="nav nav-tabs nav-stacked affix-top">
				<li><a href="/account/details">Account details <i class="icon-chevron-right right"></i></a></li>
				<li><a href="/account/change-password">Change password <i class="icon-chevron-right right"></i></a></li>
				<li><a href="/account/ssh-keys">SSH key management <i class="icon-chevron-right right"></i></a></li>
			</ul>
		</div>
		<div class="span9">
			${INNER-PAGE}
		</div>
	</div>
</div>
#parse("footer.tpl")
