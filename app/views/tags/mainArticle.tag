<div class="hero-unit">
	<h1>${_Post.title}</h1>
	<p>${_Post.content}</p>
	<h3>by <a href="#">${_Post.author.fullName}</a></h3>
	<p><a class="btn primary large" href="@{Application.show(_Post.id)}">Learn more &raquo;</a></p>
</div>