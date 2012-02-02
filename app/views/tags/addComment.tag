<h3>Post a comment</h3>
<div class="span4">
#{form @Application.postComment(_post.id), name:'commentForm'}

	#{ifErrors}
	<div class="error">
        	<div class="alert alert-error">
            	All fields are required!
        	</div>
	</div>
    #{/ifErrors}
    
    #{if flash.success}

        	<div class="alert alert-success">
            	${flash.success}
        	</div>

	#{/if}
    <p>
        <label for="author">Your name: </label>
        <input type="text" name="author" id="author" value="${params.author}"/>
    </p>
    <p>
        <label for="content">Your message: </label>
        <textarea name="content" id="content">${params.content}</textarea>
    </p>
    <p>
        <a class="btn btn-large" href="#" onclick="commentForm.submit();"><i class="icon-comment"></i> Comment</a>
    </p>
#{/form}
</div>

<script type="text/javascript" charset="utf-8">
    $(function() {         
        // Expose the form 
        $('form').click(function() { 
            $('form').expose({api: true}).load(); 
        }); 
        
        // If there is an error, focus to form
        if($('form .error').size()) {
            $('form').expose({api: true, loadSpeed: 0}).load(); 
            $('form input').get(0).focus();
        }
    });
</script>
