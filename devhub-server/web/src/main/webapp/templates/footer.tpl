
		<footer class="footer">
			<div style="text-align: center; margin-top: 32px;" class="muted">
				<a style="padding: 4px;" href="/">Home</a> - 
				<a style="padding: 4px;" href="/support">Support</a> - 
				<a style="padding: 4px;" href="/api">API</a> - 
				<a style="padding: 4px;" href="/feedback">Feedback</a> - 
				<a style="padding: 4px;" href="/about">About</a>
			</div> 
		</footer>
		
		<script src="http://code.jquery.com/jquery-1.8.1.min.js"></script>
		<script src="/js/bootstrap.js"></script>
		<script src="/js/common.js"></script>
#foreach($SCRIPT in ${SCRIPTS})
		<script src="/js/$SCRIPT"></script>
#end
	</body>
</html>


