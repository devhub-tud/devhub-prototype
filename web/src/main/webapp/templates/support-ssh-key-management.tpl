#parse("header.tpl")
#parse("menu.tpl")

		<div class="container">
			<div class="content">
				<div class="page-header">
					<h2>
						SSH key management
						<span class="right">
							<a href="#" class="label win">Windows</a>
							<a href="#" class="label mac">Mac OS X</a>
							<a href="#" class="label linux">Linux</a>
						</span>
					</h2>
				</div>
				
				<div class="contents">
					<div class="win">
						<ol>
							<li><a class="slide" href="#win-step1">Check if you already have SSH keys</a></li>
							<li><a class="slide" href="#win-step2">Generate a new SSH key</a></li>
							<li><a class="slide" href="#win-step3">Uploading your key to DevHub</a></li>
							<li><a class="slide" href="#win-step4">Testing your SSH key</a></li>
						</ol>
						
						<div class="steps">
							<div class="step">
								<a id="win-step1"></a>
								<h4>1. Check if you already have SSH keys</h4>
								<p>
									If you already have SSH keys you can use these to authenticate with 
									DevHub's git server. To find out if you have SSH keys open a command 
									line prompt and type in the following commands.
									<pre>cd ~/.ssh<br/>dir</pre>
									In case the second command lists a file called 
									<code>id_rsa.pub</code> you can proceed to <a class="slide" href="#win-step3">step 3</a>.
								</p>
							</div>
							<div class="step">
								<a id="win-step2"></a>
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
								<a id="win-step3"></a>
								<h4>3. Uploading your key to DevHub</h4>
								<p>
									Copy the contents of your public SSH key file to the clipboard.
			#if ($user)
									Then head over to the <a href="/account/ssh-keys">SSH key management page</a> in your account settings. 
			#else
									Now log in on DevHub with your account and head over to the <a href="/account/ssh-keys">SSH key management page</a> in your account settings. 
			#end
									Click on the <span class="label">Add new key</span> button. 
									Enter a name for the key: we suggest that you fill something in which describes the computer you are 
									currently using. For instance: <i>Dell-Inspiron</i>. Paste the public SSH key on your clipboard to 
									the key field, and press the <span class="label">Add key</span> button. As soon as the popup closes, you 
									should be able access your projects from your current computer.
								</p> 
							</div>
							<div class="step">
								<a id="win-step4"></a>
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
						</div>
					</div>
					<div class="mac">
						<ol>
							<li><a class="slide" href="#mac-step1">Check if you already have SSH keys</a></li>
							<li><a class="slide" href="#mac-step2">Generate a new SSH key</a></li>
							<li><a class="slide" href="#mac-step3">Uploading your key to DevHub</a></li>
							<li><a class="slide" href="#mac-step4">Testing your SSH key</a></li>
						</ol>
						
						<div class="steps">
							<div class="step">
								<a id="mac-step1"></a>
								<h4>1. Check if you already have SSH keys</h4>
								<p>
									If you already have SSH keys you can use these to authenticate with 
									DevHub's git server. To find out if you have SSH keys open a terminal 
									and type in the following commands.
									<pre>cd ~/.ssh<br/>ls</pre>
									In case the second command lists a file called 
									<code>id_rsa.pub</code> you can proceed to <a class="slide" href="#mac-step3">step 3</a>.
								</p>
							</div>
							<div class="step">
								<a id="mac-step2"></a>
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
								<a id="mac-step3"></a>
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
								<a id="mac-step4"></a>
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
						</div>
					</div>
					<div class="linux">
						<ol>
							<li><a class="slide" href="#linux-step1">Check if you already have SSH keys</a></li>
							<li><a class="slide" href="#linux-step2">Generate a new SSH key</a></li>
							<li><a class="slide" href="#linux-step3">Uploading your key to DevHub</a></li>
							<li><a class="slide" href="#linux-step4">Testing your SSH key</a></li>
						</ol>
						
						<div class="steps">
							<div class="step">
								<a id="linux-step1"></a>
								<h4>1. Check if you already have SSH keys</h4>
								<p>
									If you already have SSH keys you can use these to authenticate with 
									DevHub's git server. To find out if you have SSH keys open a terminal 
									and type in the following commands.
									<pre>cd ~/.ssh<br/>ls</pre>
									In case the second command lists a file called 
									<code>id_rsa.pub</code> you can proceed to <a class="slide" href="#linux-step3">step 3</a>.
								</p>
							</div>
							<div class="step">
								<a id="linux-step2"></a>
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
								<a id="linux-step3"></a>
								<h4>3. Uploading your key to DevHub</h4>
								<p>
									Copy the contents of your public SSH key to the clipboard using the following command.
									<pre>xclip -sel clip < ~/.ssh/id_rsa.pub</pre>
			#if ($user)
									Head over to the <a href="/account/ssh-keys">SSH key management page</a> in your account settings. 
			#else
									Now log in on DevHub with your account and head over to the <a href="/account/ssh-keys">SSH key management page</a> in your account settings. 
			#end
									Click on the <span class="label">Add new key</span> button. 
									Enter a name for the key: we suggest that you fill something in which describes the computer you are 
									currently using. For instance: <i>Dell-Inspiron</i>. Paste the public SSH key on your clipboard to 
									the key field, and press the <span class="label">Add key</span> button. As soon as the popup closes, you 
									should be able access your projects from your current computer.
								</p> 
							</div>
							<div class="step">
								<a id="linux-step4"></a>
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
						</div>
					</div>
				</div>
			</div>
		</div>
		
#parse("footer.tpl")