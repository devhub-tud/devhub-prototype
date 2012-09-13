<div class="navbar navbar-inverse navbar-fixed-top">
	<div class="navbar-inner">
		<div class="container">
			<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
			</a>
			<a class="brand" href="/">DevHub</a>

			<div class="pull-right">
				<ul class="nav">
#if(${isAdmin})
					<li><a href="/admin">Admin panel</a></li>
#end
					<li class="dropdown">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown">${userDisplayName}<b class="caret"></b></a>
						<ul class="dropdown-menu">
							<li><a href="/account/${userId}">Show profile</a></li>
							<li class="divider"></li>
							<li><a href="/logout">Logout</a></li>
						</ul>
					</li>
				</ul>
			</div>
		</div>
	</div>
</div>