<div class="comments">
        <h3>
            ${_post.comments.size() ?: 'No'} 
            comment${_post.comments.size().pluralize()}
        </h3>
        
        #{list items:_post.comments, as:'comment'}
            <div class="well">
                <div class="comment-metadata">
                    <h4>by ${comment.author}, ${comment.postedAt.format('dd MMM yy')}</h4>
                </div><br/>
                <div>
                <h5>
                    ${comment.content.escape().nl2br()}
                </h5>
                </div>
            </div>
        #{/list}
        
</div>