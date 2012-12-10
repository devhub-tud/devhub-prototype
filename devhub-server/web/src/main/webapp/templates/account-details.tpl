			<section id="account-details">
				<div class="page-header">
					<h1>Account details</h1>
				</div>
				
				<table class="values table table-striped table-bordered" style="margin-top: 12px;">
					<tbody>
						<tr>
							<td>Email</td>
							<td>${user.getEmail()}</td>
						</tr>
						<tr>
							<td>Net ID</td>
							<td>${user.getNetId()}</td>
						</tr>
	#if (${user.getStudentNumber()} > 0) 
						<tr>
							<td>Student number</td>
							<td>${user.getStudentNumber()}</td>
						</tr>
	#end
					</tbody>
				</table>
			</section>