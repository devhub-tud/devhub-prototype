#parse("header.tpl") 
#parse("menu.tpl")

<div class="container">
	<div class="content offset2 span8">
		
		<div class="row" style="margin-bottom: 48px;">
			<div class="span12">
				<img style="float: left;" src="${user.getGravatarUrl(128)}" class="img-polaroid" />
				<div class="left">
					<div class="page-header">
						<h2><span class="muted">Hey, </span>$user.getDisplayName()</h2>
					</div>
					<h4>Can you give us some feedback?</h4>
				</div>
			</div>
		</div>
		
		<div class="alerts"></div>
		
		<form id="feedback-form" class="form-vertical">
			<div class="control-group">
				<div class="controls">
					<input class="input-fullwidth" type="text" name="title" placeholder="Subject" />
				</div>
			</div>
			<div class="control-group">
				<div class="controls">
					<textarea class="input-fullwidth" rows="7" name="content" placeholder="Your message..."></textarea>
				</div>
			</div>
			<div class="control-group">
				<input class="btn btn-primary right" name="submit" type="submit" value="Submit feedback" />
			</div>
		</form>
		
	</div>
</div>
#parse("footer.tpl")
