<div class="hero-unit">
	<h1>${_post.title}</h1>
	<p>${_post.content}</p>
	<h3>by <a href="#">${_post.author.fullName}</a></h3>
	<p><a class="btn primary large" href="@{Application.show(_post.id)}">Learn more &raquo;</a></p>
</div>