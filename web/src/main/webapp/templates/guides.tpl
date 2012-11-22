#parse("header.tpl")
#parse("menu.tpl")

		<div class="container">
			<div class="content">
				<h1>DevHub guide</h1>
				<ul>
					<li>
						<a href="#key-management">SSH key management</a>
						<ol>
							<li><a href="#key-management:step1">Check if you already have SSH keys</a></li>
							<li><a href="#key-management:step2">Generate a new SSH key</a></li>
							<li><a href="#key-management:step3">Uploading your key to DevHub</a></li>
							<li><a href="#key-management:step4">Testing your SSH key</a></li>
						</ol>
					</li>
				</ul>
				
				<section id="key-management">
					<a id="key-management"></a>
					<div class="page-header">
						<h2>SSH key management</h2>
					</div>
					<div class="step">
						<a id="key-management:step1"></a>
						<h4>1. Check if you already have SSH keys</h4>
						<p>
							If you already have SSH keys you can use these to authenticate with 
							DevHub's git server. To find out if you have SSH keys open a terminal 
							or command line prompt and type in the following command.
							<pre>ls ~/.ssh</pre>
							In case the result of this command contains 
							<code>id_rsa.pub</code> you can proceed to <a href="#key-management:step3">step 3</a>.
						</p>
					</div>
					<div class="step">
						<a id="key-management:step2"></a>
						<h4>2. Generate a new SSH key</h4>
						<p>
							In case the previous step did not list the <code>id_rsa.pub</code> file in the results, 
							execute the following command and follow the instructions.
	#if ($user)
							<pre>ssh-keygen -t rsa -C "<i>${user.getEmail()}</i>"</pre>
	#else
							<pre>ssh-keygen -t rsa -C "<i>your_netid@student.tudelft.nl</i>"</pre>
	#end
							Once you have completed this step, you should have generated a public and a private SSH key.
						</p> 
					</div>
					<div class="step">
						<a id="key-management:step3"></a>
						<h4>3. Uploading your key to DevHub</h4>
						<p>
							Copy the contents of your public SSH key to the clipboard using the following command.
							<pre>pbcopy < ~/.ssh/id_rsa.pub</pre>
	#if ($user)
							Head over to the <a href="/account/ssh-keys">SSH key management page</a> in your account settings. 
	#else
							Now log in on DevHub with your account and head over to the <a href="/account/ssh-keys">SSH key management page</a> in your account settings. 
	#end
							Click on the <span class="label">Add new key</span> button. 
							Enter a name for the key: we suggest that you fill something in which describes the computer you are 
							currently using. For instance: <i>MacBook-Air</i>. Paste the public SSH key on your clipboard to 
							the key field, and press the <span class="label">Add key</span> button. As soon as the popup closes, you 
							should be able access your projects from your current computer.
						</p> 
					</div>
					<div class="step">
						<a id="key-management:step4"></a>
						<h4>4. Testing your SSH key</h4>
						<p>
							To test if you can properly connect to the Git server enter the following command:
							<pre>ssh -T ${gitUser}@${gitHost}</pre>
							You may encounter the following response:
							<pre>The authenticity of host '${gitHost} (<span class="muted">Git server IP address</span>)' can't be established.<br/># RSA key fingerprint is <span class="muted"><i>Your SSH key fingerprint</i></span>.<br/># Are you sure you want to continue connecting (yes/no)?</pre>
							In which case you should accept by entering <code>yes</code>. The response of the command should be a 
							welcome message, containing your name, the server address, the Git version, and a list of 
							Git repositories you have access to.
						</p> 
					</div>
				</section>
			</div>
		</div>
		
#parse("footer.tpl")