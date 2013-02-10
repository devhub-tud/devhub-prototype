<div class="navbar navbar-inverse navbar-fixed-top">
	<div class="navbar-inner">
		<div class="container">
			<a class="brand" href="/">DevHub <span class="muted" style="font-size: 12px;">(BETA)</span></a>

#if ($user) 
			<div class="pull-right">
				<ul class="nav">
					<li class="dropdown">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown">
							<img src="${user.getGravatarUrl(24)}" />
							${user.getDisplayName()} <b class="caret"> </b>
						</a>
						<ul class="dropdown-menu user-menu">
							<li style="margin: 0px 6px 0px 6px; height: 46px; ">
								<img class="bordered" style="float: left;" src="${user.getGravatarUrl(48)}" />
								<span style="float: left; padding-left: 6px;">
									<span style="display: block;"><strong>${user.getDisplayName()}</strong></span>
									<span style="display: block;">$user.getRole().getDisplayName()</span>
								</span>
							</li>
							<li class="divider"></li>
	#if(${user.isAdmin()})
							<li><a href="/admin">Admin panel</a></li>
							<li><a href="/metrics">System status</a></li>
	#end
							<li><a href="/account">Show account details</a></li>
							<li class="divider"></li>
							<li><a href="/logout">Logout</a></li>
						</ul>
					</li>
				</ul>
			</div>
#end
		</div>
	</div>
</div>